package de.bonndan.nivio.model;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import de.bonndan.nivio.assessment.Assessment;
import de.bonndan.nivio.search.SearchDocumentValueObject;
import de.bonndan.nivio.search.SearchEngineException;
import de.bonndan.nivio.search.SearchIndex;
import org.apache.lucene.facet.FacetResult;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Contains the nodes and edges of the landscape graph and and embedded search index.
 *
 * @param <T> component type
 */
public class Index<T extends Component> {

    private final Map<URI, T> nodes = new ConcurrentHashMap<>();
    private final Table<URI, URI, Relation> edges = TreeBasedTable.create();

    private final SearchIndex searchIndex;
    private URI root;

    public Index(@NonNull final SearchIndex searchIndex) {
        this.searchIndex = Objects.requireNonNull(searchIndex);
    }

    /**
     * Returns for node for an URI.
     */
    public Optional<T> get(@NonNull final URI uri) {
        if (ComponentClass.relation.name().equals(uri.getScheme())) {
            URI target = Relation.parseTargetURI(uri);
            return edges.columnMap().get(target).values().stream()
                    .filter(relation -> relation.getFullyQualifiedIdentifier().equals(uri))
                    .map(relation -> (T)relation)
                    .findFirst();
        }
        return Optional.ofNullable(nodes.get(uri));
    }

    public List<T> getChildren(@NonNull final URI uri) {
        Map<URI, Relation> uriRelationMap = edges.rowMap().get(Objects.requireNonNull(uri));
        if (uriRelationMap == null) {
            return new ArrayList<>();
        }

        return uriRelationMap.entrySet().stream()
                .filter(uriRelationEntry -> RelationType.CHILD.name().equals(uriRelationEntry.getValue().getType()))
                .map(uriRelationEntry -> get(uriRelationEntry.getValue().targetURI).orElseThrow())
                .collect(Collectors.toList());
    }

    /**
     * @param uri uri of an endpoint
     * @return all inbound and outbound relations
     */
    public Set<Relation> getRelations(@NonNull final URI uri) {
        Map<URI, Relation> outgoing = edges.rowMap().get(Objects.requireNonNull(uri));
        if (outgoing == null) {
            outgoing = new HashMap<>();
        }
        Map<URI, Relation> incoming = edges.columnMap().get(Objects.requireNonNull(uri));
        if (incoming == null) {
            incoming = new HashMap<>();
        }

        return Stream.concat(outgoing.entrySet().stream(), incoming.entrySet().stream())
                .map(Map.Entry::getValue)
                .filter(relation -> !RelationType.CHILD.name().equalsIgnoreCase(relation.getType()))
                .collect(Collectors.toSet());
    }

    /**
     * Adds or replaces a node and indexes it.
     */
    public Optional<T> addOrReplace(@NonNull final T component) {
        if (nodes.isEmpty()) {
            this.root = component.getFullyQualifiedIdentifier();
        }
        return Optional.ofNullable(nodes.put(Objects.requireNonNull(component).getFullyQualifiedIdentifier(), component));
    }

    Optional<Relation> addOrReplace(@NonNull final Relation relation) {
        URI source = relation.sourceURI;
        if (!nodes.containsKey(source)) {
            throw new IllegalArgumentException(String.format("Source %s not known.", source));
        }

        URI target = relation.targetURI;
        if (!nodes.containsKey(target)) {
            throw new IllegalArgumentException(String.format("Target %s not known.", target));
        }

        return Optional.ofNullable(edges.put(source, target, relation));
    }

    /**
     * Removes the child node and the related relations.
     *
     * @param parent parent node
     * @param child  child node
     * @return true if removed
     */
    boolean removeChild(@Nullable final T parent, @NonNull final T child) {

        URI fullyQualifiedIdentifier = child.getFullyQualifiedIdentifier();
        if (!getChildren(fullyQualifiedIdentifier).isEmpty()) {
            throw new IllegalArgumentException(String.format("%s still has children", child));
        }

        //node
        Optional<T> remove = Optional.ofNullable(nodes.remove(fullyQualifiedIdentifier));

        //child relation
        if (parent != null) {
            edges.remove(parent.getFullyQualifiedIdentifier(), fullyQualifiedIdentifier);
        }

        //other
        edges.rowMap().remove(fullyQualifiedIdentifier);
        edges.columnMap().remove(fullyQualifiedIdentifier);

        return remove.isPresent();
    }

    /**
     * Removes a relation between two components.
     *
     * @param relation to remove
     * @return the removed relation
     * @throws IllegalArgumentException if a parent-child relation was given
     */
    public Optional<Relation> removeRelation(Relation relation) {
        if (RelationType.CHILD.name().equalsIgnoreCase(relation.getType())) {
            throw new IllegalArgumentException("Child relations cannot be deleted.");
        }
        return Optional.ofNullable(edges.remove(relation.sourceURI, relation.targetURI));
    }

    public List<FacetResult> getFacets() {
        return searchIndex.facets();
    }

    public List<T> search(@NonNull final String queryString) {
        final Set<URI> results = searchIndex.search(queryString);
        List<T> list = new ArrayList<>();
        for (URI uri : results) {
            var o = get(uri);
            if (o.isEmpty()) {
                throw new SearchEngineException(String.format("No node for URI %s", uri));
            }
            list.add(o.get());
        }
        return list;
    }

    Stream<Component> all() {
        Stream<Component> relationStream = edges.rowMap().values().stream()
                .flatMap(uriRelationMap -> uriRelationMap.values().stream());
        return Stream.concat(nodes.values().stream(), relationStream);
    }

    /**
     * Reindex all nodes with assessment info.
     *
     * @param assessment optional assessment to index
     */
    public void indexWith(@Nullable final Assessment assessment) {
        Set<SearchDocumentValueObject> components = all()
                .map(SearchDocumentValueObjectFactory::createFor)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        searchIndex.indexForSearch(components, assessment == null ? Assessment.empty() : assessment);
    }

    public T getRoot() {

        var ex = new NoSuchElementException("Index root has not been set.");
        if (root == null) {
            throw ex;
        }
        return get(root).orElseThrow(() -> ex);
    }
}
