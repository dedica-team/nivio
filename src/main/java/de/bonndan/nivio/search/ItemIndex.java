package de.bonndan.nivio.search;

import com.googlecode.cqengine.ConcurrentIndexedCollection;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.support.SimpleFunction;
import com.googlecode.cqengine.query.Query;
import com.googlecode.cqengine.query.parser.sql.SQLParser;
import com.googlecode.cqengine.resultset.ResultSet;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.model.Component;
import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import de.bonndan.nivio.util.URLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.googlecode.cqengine.query.QueryFactory.attribute;
import static com.googlecode.cqengine.query.QueryFactory.in;
import static de.bonndan.nivio.model.Item.IDENTIFIER_VALIDATION;

/**
 * A queryable index on all landscape items.
 *
 * TODO the API is too wide
 * TODO cqengine based search could be replaced completely by lucene if SQL-like queries were not used and items were kept in a hashmap
 */
public class ItemIndex<T extends Component> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemIndex.class);
    private static final String CQE_FIELD_FQI = "fqi";

    /**
     * The {@link com.googlecode.cqengine.query.QueryFactory#attribute(String, SimpleFunction)})} relies on a method
     * {@link net.jodah.typetools.TypeResolver#resolveRawArguments(Type, Class)}, which in Java 13 is not able to retrieve
     * information about the generic types, if a lambda or anonymous method reference is provided. By providing an anonymous
     * class of the {@link SimpleFunction}, the generic types can be resolved without running into exceptions.
     */
    @SuppressWarnings({"Convert2Lambda"})
    private final Attribute<T, FullyQualifiedIdentifier> CQE_ATTR_FQI = attribute("fqi", new SimpleFunction<>() {
        @Override
        public FullyQualifiedIdentifier apply(T item) {
            return item.getFullyQualifiedIdentifier();
        }
    });

    /**
     * See {@link #CQE_ATTR_FQI}
     */
    @SuppressWarnings({"Convert2Lambda", "Anonymous2MethodRef"})
    private final Attribute<T, String> CQE_ATTR_IDENTIFIER = attribute("identifier", new SimpleFunction<>() {
        @Override
        public String apply(T item) {
            return item.getIdentifier() != null ? item.getIdentifier().toLowerCase() : "";
        }
    });

    /**
     * See {@link #CQE_ATTR_FQI}
     */
    @SuppressWarnings({"Convert2Lambda", "Anonymous2MethodRef"})
    private final Attribute<T, String> CQE_ATTR_NAME = attribute("name", new SimpleFunction<>() {
        @Override
        public String apply(T item) {
            return item.getName() != null ? item.getName().toLowerCase() : "";
        }
    });

    /**
     * See {@link #CQE_ATTR_FQI}
     */
    @SuppressWarnings({"Convert2Lambda", "Anonymous2MethodRef"})
    private final Attribute<T, String> CQE_ATTR_ADDRESS = attribute("address", new SimpleFunction<>() {
        @Override
        public String apply(T item) {
            return item.getAddress() != null ? item.getAddress().toLowerCase() : "";
        }
    });

    private final SQLParser<T> parser;

    /**
     * The search index is only used for the final models, absent for item description objects.
     */
    private final Optional<SearchIndex> searchIndex;

    private IndexedCollection<T> index = new ConcurrentIndexedCollection<>();

    /**
     * Creates a new empty index.
     */
    public ItemIndex(@Nullable SearchIndex searchIndex, Class<T> tClass) {

        //init cq engine
        parser = SQLParser.forPojoWithAttributes(tClass,
                Map.of(
                        CQE_FIELD_FQI, CQE_ATTR_FQI,
                        "identifier", CQE_ATTR_IDENTIFIER,
                        "name", CQE_ATTR_NAME,
                        "address", CQE_ATTR_ADDRESS)
        );

        this.searchIndex = Optional.ofNullable(searchIndex);
    }

    public Stream<T> itemStream() {
        return index.stream();
    }

    public void setItems(Set<T> items) {
        index = new ConcurrentIndexedCollection<>();
        index.addAll(items);
    }

    /**
     * Returns all items matching the given term.
     *
     * @param term "*" as wildcard for all | {@link FullyQualifiedIdentifier} string paths | identifier | url
     * @return all matching items.
     * @todo refactor param, too ambiguous
     */
    public Collection<T> query(String term) {
        if ("*".equals(term)) {
            return all();
        }

        if (URLHelper.getURL(term).isPresent()) {
            term = "address = '" + term + "'";
        }

        if (term.contains("/") && !term.contains(" ")) {
            return findAll(ItemMatcher.forTarget(term));
        }

        //single word compared against identifier
        String query = term.matches(IDENTIFIER_VALIDATION) ? selectByIdentifierOrName(term) : "SELECT * FROM items WHERE " + term;
        return cqnQueryOnIndex(query);
    }

    /**
     * Returns a select query.
     *
     * @param term equals identifier or name
     * @return query string
     */
    public String selectByIdentifierOrName(String term) {
        return String.format("SELECT * FROM items WHERE (identifier = '%s' OR name = '%s')", term, term);
    }

    public Set<T> all() {
        return index;
    }

    public void add(T item) {
        index.add(item);
    }

    /**
     * Ensures that the given item has a sibling in the list, returns the item from the list.
     *
     * @param itemDescription item to search for
     * @return the sibling from the list
     */
    public T pick(final ItemDescription itemDescription) {
        return pick(itemDescription.getIdentifier(), itemDescription.getGroup());
    }

    /**
     * Makes sure the sibling of the item is returned or throws an exception.
     *
     * @param identifier identifier
     * @param group      the group to search in
     * @return the sibling with the given identifier
     */
    public T pick(final String identifier, String group) {
        if (StringUtils.isEmpty(identifier)) {
            throw new IllegalArgumentException("Identifier to pick is empty");
        }

        return find(identifier, group).orElseThrow(() ->
                new RuntimeException("Element '" + identifier + "' not found  in collection.")
        );
    }

    /**
     * Returns a the item from the list or null. Uses the matching criteria of {@link FullyQualifiedIdentifier}
     *
     * @param identifier the identifier
     * @return the item or null
     */
    public Optional<T> find(String identifier, String group) {
        if (StringUtils.isEmpty(identifier)) {
            throw new IllegalArgumentException("Identifier to find is empty");
        }

        List<T> found = findAll(identifier, group);

        if (found.size() > 1) {
            throw new RuntimeException("Ambiguous result for " + group + "/" + identifier + ": " + found + " in collection ");
        }

        return Optional.ofNullable((found.size() == 1) ? found.get(0) : null);
    }

    /**
     * Returns a the item from the list or null. Uses the matching criteria of {@link ItemMatcher}
     *
     * @param itemMatcher the identifier
     * @return the or null
     */
    public Optional<T> find(ItemMatcher itemMatcher) {
        List<T> found = findAll(itemMatcher);

        if (found.size() > 1) {
            throw new RuntimeException("Ambiguous result for " + itemMatcher + ": " + found + " in collection.");
        }

        return Optional.ofNullable((found.size() == 1) ? found.get(0) : null);
    }

    /**
     * Creates a search index based in a snapshot of current items state (later modifications won't be shown).
     */
    public void indexForSearch() {
        searchIndex.ifPresent(searchIndex1 -> searchIndex1.indexForSearch(all()));
    }

    public Set<T> search(String queryString) {
        if (searchIndex.isEmpty()) {
            throw new IllegalStateException("No search index provided. Cannot execute search.");
        }

        Set<FullyQualifiedIdentifier> search = searchIndex.get().search(queryString);
        Query<T> nativeQuery = in(CQE_ATTR_FQI, search);

        return index.retrieve(nativeQuery).stream().collect(Collectors.toSet());
    }


    public List<T> cqnQueryOnIndex(String condition) {
        ResultSet<T> results = parser.retrieve(index, condition);
        return results.stream().collect(Collectors.toList());
    }

    private List<T> findAll(final String identifier, final String group) {
        return findAll(ItemMatcher.build(null, group, identifier));
    }

    private List<T> findAll(ItemMatcher itemMatcher) {
        return itemStream()
                .filter(item -> itemMatcher.isSimilarTo(item.getFullyQualifiedIdentifier()))
                .collect(Collectors.toList());
    }

    public void remove(T item) {
        index.remove(item);
    }
}
