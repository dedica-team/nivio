package de.bonndan.nivio.model;

import de.bonndan.nivio.assessment.Assessment;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.search.ComponentMatcher;
import de.bonndan.nivio.util.URLFactory;
import org.apache.lucene.facet.FacetResult;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import static de.bonndan.nivio.model.IdentifierValidation.PATTERN;

/**
 * A read proxy to the index.
 *
 * @param <T> component / graph component
 */
public class IndexReadAccess<T extends Component> {

    private final Index<T> index;

    public IndexReadAccess(Index<T> index) {
        this.index = index;
    }

    public List<FacetResult> getFacets() {
        return index.getFacets();
    }

    /**
     * Executes a lucene search using the given query string.
     *
     * @param queryString the lucene format query string
     * @param cls         class to map
     * @return all matched items
     */
    public <C> List<C> search(@NonNull final String queryString, Class<C> cls) {
        if (!StringUtils.hasLength(queryString)) {
            return Collections.emptyList();
        }
        return index.search(queryString).stream()
                .filter(cls::isInstance)
                .map(t -> (C) t)
                .collect(Collectors.toList());
    }

    /**
     *
     * @param term identifier
     * @param cls
     * @param <C>
     * @return
     * @todo remove fallback to search query?
     */
    @NonNull
    public <C extends Component> List<C> findMatching(@NonNull final String term, @NonNull final Class<C> cls) {
        Objects.requireNonNull(term, "Search term is null");
        try {
            ComponentMatcher componentMatcher = ComponentMatcher.forTarget(term, cls);
            return all(cls).stream()
                    .filter(item -> componentMatcher.isSimilarTo(item.getFullyQualifiedIdentifier()))
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            return new ArrayList<>(queryRegardingAddress(term, cls));
        }
    }

    /**
     * Returns a select query.
     *
     * @param term equals identifier or name
     * @return query string
     */
    private String selectByIdentifierOrName(String term) {
        return String.format("identifier:%s OR name:%s)", term, term);
    }

    /**
     * All components of the given type.
     *
     * @param cls type
     * @return filtered nodes (not from query execution)
     */
    public <C> Set<C> all(Class<C> cls) {
        return index.all().filter(cls::isInstance).map(cls::cast).collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Returns all items matching the given term.
     *
     * @param term "*" as wildcard for all | {@link FullyQualifiedIdentifier} string paths | identifier | url
     * @return all matching items.
     */
    <C extends Component> Collection<C> queryRegardingAddress(String term, Class<C> cls) {
        if ("*".equals(term)) {
            return all(cls);
        }

        if (term.contains("/")) {

            if (URLFactory.getURL(term).isPresent()) {
                term = "address:\"" + term + "\"";
            } else if (!term.contains(" ")) {
                try {
                    String finalTerm = term;
                    return all(cls).stream()
                            .filter(item -> ComponentMatcher.forTarget(finalTerm, ItemDescription.class).isSimilarTo(item.getFullyQualifiedIdentifier()))
                            .collect(Collectors.toList());
                } catch (IllegalArgumentException e) {
                    //something else that is not an url nor identifier nor where-condition
                    term = selectByIdentifierOrName(term);
                }

            }
        }

        //single word compared against identifier
        String query = term.matches(PATTERN) ? selectByIdentifierOrName(term) : term;
        return search(query, cls);
    }

    /**
     * Returns one distinct item for a query term.
     *
     * @param term             search term
     * @param parentIdentifier optional parent identifier to narrow
     * @param cls              class for filter results
     * @return the matched item
     * @throws NoSuchElementException if not exactly one item could be determined
     */
    public <C extends Component> Optional<C> findOneByIdentifiers(@NonNull final String term,
                                                                  @Nullable final String parentIdentifier,
                                                                  @NonNull final Class<C> cls
    ) {
        if (Landscape.class.equals(cls) || LandscapeDescription.class.equals(cls)) {
            return Optional.of((C) getRoot());
        }
        List<C> components = findMatching(term, cls);
        if (components.isEmpty()) {
            return Optional.empty();
        }

        if (components.size() == 1 && !StringUtils.hasLength(parentIdentifier)) {
            return Optional.of(components.get(0));
        }

        if (parentIdentifier == null) {
            var msg = String.format("Could not extract distinct %s matching '%s' from ambiguous result without group: %s", cls.getSimpleName(), term, components);
            throw new NoSuchElementException(msg);
        }

        Optional<C> first = components.stream()
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

    /**
     * TODO move to IndexWriteAccess
     */
    public void indexForSearch(@Nullable final Assessment assessment) {
        index.indexWith(assessment);
    }

    /**
     * Find one exactly component.
     *
     * @param matcher matcher to
     * @param cls     type
     * @return one if present
     * @throws NoSuchElementException if no result or ambiguous
     */
    public <C extends Component> Optional<C> findOneMatching(@NonNull final ComponentMatcher matcher, @NonNull final Class<C> cls) {
        List<C> components = all(cls).stream()
                .filter(item -> matcher.isSimilarTo(item.getFullyQualifiedIdentifier()))
                .collect(Collectors.toList());
        if (components.isEmpty()) {
            return Optional.empty();
        }

        if (components.size() == 1) {
            return Optional.of(components.get(0));
        }

        throw new NoSuchElementException("Could not find a distinct component from ambiguous result: " + components);
    }

    /**
     * Returns the root node, e.g. a {@link Landscape}
     *
     * @return root node
     * @throws NoSuchElementException if no node exists
     */
    public T getRoot() {
        return index.getRoot();
    }

    /**
     * Finds an existing relation between two nodes.
     *
     * @param source source uri
     * @param target target uri
     * @return the relation if present
     */
    public Optional<Relation> findRelation(URI source, URI target) {
        return index.getRelations(source).stream()
                .filter(relation -> relation.getTarget().getFullyQualifiedIdentifier().equals(target))
                .findFirst();
    }

    public Optional<T> get(URI parent) {
        return index.get(parent);
    }

    public List<T> getChildren(URI fullyQualifiedIdentifier) {
        return index.getChildren(fullyQualifiedIdentifier);
    }

    public Set<Relation> getRelations(URI fullyQualifiedIdentifier) {
        return index.getRelations(fullyQualifiedIdentifier);
    }
}
