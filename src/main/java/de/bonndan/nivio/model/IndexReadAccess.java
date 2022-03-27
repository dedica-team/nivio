package de.bonndan.nivio.model;

import de.bonndan.nivio.assessment.Assessment;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.search.ComponentMatcher;
import org.apache.lucene.facet.FacetResult;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

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
     * Returns all the components matching the given {@link ComponentMatcher}
     *
     * Operates on the nodes, does not search.
     *
     * @param componentMatcher matcher by identifiers
     */
    @NonNull
    public <C extends Component> List<C> match(@NonNull final ComponentMatcher componentMatcher, @NonNull final Class<C> cls) {
        return all(cls).stream()
                .filter(item -> componentMatcher.isSimilarTo(item.getFullyQualifiedIdentifier()))
                .collect(Collectors.toList());
    }

    /**
     * All components of the given type.
     *
     * Operates on the nodes, independent form search index.
     *
     * @param cls type
     * @return filtered nodes (not from query execution)
     */
    public <C> Set<C> all(@NonNull final Class<C> cls) {
        return index.all()
                .filter(cls::isInstance)
                .map(cls::cast)
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Explicitly search for an address.
     *
     * @param term URL
     */
    public <C extends Component> Collection<C> searchAddress(String term, Class<C> cls) {
        try {
            URI uri = URI.create(term);
            if (uri.getScheme() != null) {
                term = "address:\"" + term + "\"";
                return search(term, cls);
            }
            return Collections.emptyList();
        } catch (IllegalArgumentException ignored) {
            return Collections.emptyList();
        }
    }

    /**
     * Returns one distinct item for an identifier that is used with a {@link ComponentMatcher}
     *
     * @param identifier       search term
     * @param parentIdentifier optional parent identifier to narrow
     * @param cls              class for filter results
     * @return the matched item
     * @throws NoSuchElementException if not exactly one item could be determined
     */
    public <C extends Component> Optional<C> matchOneByIdentifiers(@NonNull final String identifier,
                                                                   @Nullable final String parentIdentifier,
                                                                   @NonNull final Class<C> cls
    ) {
        if (Landscape.class.equals(cls) || LandscapeDescription.class.equals(cls)) {
            return Optional.of((C) getRoot());
        }
        List<C> components = match(ComponentMatcher.forComponent(identifier, cls), cls);
        if (components.isEmpty()) {
            return Optional.empty();
        }

        if (components.size() == 1 && !StringUtils.hasLength(parentIdentifier)) {
            return Optional.of(components.get(0));
        }

        if (parentIdentifier == null) {
            var msg = String.format("Could not extract distinct %s matching '%s' from ambiguous result without group: %s", cls.getSimpleName(), identifier, components);
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
    public <C extends Component> Optional<C> matchOne(@NonNull final ComponentMatcher matcher, @NonNull final Class<C> cls) {
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
    public Optional<Relation> findRelation(@NonNull final URI source, @NonNull final URI target) {
        return index.getRelations(source).stream()
                .filter(relation -> relation.getTarget().getFullyQualifiedIdentifier().equals(target))
                .findFirst();
    }

    public Optional<T> get(URI uri) {
        return index.get(uri);
    }

    public List<T> getChildren(URI fullyQualifiedIdentifier) {
        return index.getChildren(fullyQualifiedIdentifier);
    }

    public Set<Relation> getRelations(URI fullyQualifiedIdentifier) {
        return index.getRelations(fullyQualifiedIdentifier);
    }

    public Optional<Relation> getRelation(@NonNull final URI uri) {
        return index.getRelation(uri);
    }
}
