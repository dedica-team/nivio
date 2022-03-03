package de.bonndan.nivio.model;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.search.ComponentMatcher;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.bonndan.nivio.model.IdentifierValidation.IDENTIFIER_PATTERN;

/**
 * Search abstraction which tries direct matching via {@link ComponentMatcher} first and then falls back to search index.
 *
 *
 */
public class FlexSearch<C extends Component, T extends Component> {

    private final Class<T> cls;
    private final IndexReadAccess<C> readAccess;

    public static <C extends Component, T extends Component> FlexSearch<C, T> forClassOn(
            @NonNull final Class<T> cls,
            @NonNull final IndexReadAccess<C> readAccess
    ) {
        return new FlexSearch<>(cls, readAccess);
    }

    FlexSearch(@NonNull final Class<T> cls, @NonNull final IndexReadAccess<C> readAccess) {
        this.cls = Objects.requireNonNull(cls);
        this.readAccess = Objects.requireNonNull(readAccess);
    }

    /**
     * Returns all items matching the given term.
     *
     * Uses a {@link ComponentMatcher} for path-like terms, otherwise executes a search.
     *
     * @param term "*" as wildcard for all | {@link FullyQualifiedIdentifier} string paths | identifier | url
     * @return all matching items, might contain partial matches and not ordered by best match
     */
    public Collection<T> search(@NonNull final String term) {
        if (!StringUtils.hasLength(term)) {
            throw new IllegalArgumentException("Empty term given");
        }

        if ("*".equals(term)) {
            return readAccess.all(cls);
        }

        //term is like "groupA/itemB"
        boolean isPath = term.contains(FullyQualifiedIdentifier.SEPARATOR) && (!term.contains(" "));
        boolean isIdentifier = term.matches(IDENTIFIER_PATTERN);
        if (isPath || isIdentifier) {
            var results = readAccess.all(cls).stream()
                    .filter(item -> ComponentMatcher.forComponent(term, ItemDescription.class).isSimilarTo(item.getFullyQualifiedIdentifier()))
                    .collect(Collectors.toList());
            if (!results.isEmpty()) {
                return results;
            }
        }

        //single word compared against identifier
        String query = isIdentifier ? String.format("identifier:%s OR name:%s", term, term) : term;
        return readAccess.search(query, cls);
    }

    /**
     * Search, reduces to one result if possible.
     *
     * @param term             search term
     * @param parentIdentifier to narrow results
     * @return one or zero components
     */
    public Optional<T> searchOne(@NonNull final String term, @Nullable final String parentIdentifier) {
        var components = search(term);
        if (components.isEmpty()) {
            return Optional.empty();
        }

        if (components.size() == 1) {
            return Optional.of(components.iterator().next());
        }

        if (parentIdentifier == null) {
            var msg = String.format("Could not extract distinct %s matching '%s' from ambiguous result without group: %s", cls.getSimpleName(), term, components);
            throw new NoSuchElementException(msg);
        }

        Optional<T> first = components.stream()
                .filter(component -> parentIdentifier.equalsIgnoreCase(component.getParentIdentifier()))
                .findFirst();

        if (first.isPresent()) {
            return first;
        }

        if (components.size() > 1) {
            throw new NoSuchElementException("Could not extract distinct item from ambiguous result: " + components);
        }

        return first;
    }
}
