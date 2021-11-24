package de.bonndan.nivio.search;

import com.googlecode.cqengine.ConcurrentIndexedCollection;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.support.SimpleFunction;
import com.googlecode.cqengine.query.Query;
import com.googlecode.cqengine.query.parser.common.InvalidQueryException;
import com.googlecode.cqengine.query.parser.sql.SQLParser;
import com.googlecode.cqengine.resultset.ResultSet;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.model.Component;
import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import de.bonndan.nivio.model.ItemComponent;
import de.bonndan.nivio.util.URLFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.googlecode.cqengine.query.QueryFactory.attribute;
import static com.googlecode.cqengine.query.QueryFactory.in;
import static de.bonndan.nivio.model.IdentifierValidation.PATTERN;

/**
 * A queryable index on all landscape items.
 *
 * TODO the API is too wide
 * TODO cqengine based search could be replaced completely by lucene if SQL-like queries were not used and items were kept in a hashmap
 */
public class ItemIndex<T extends ItemComponent> {

    public static final String CQE_FIELD_FQI = "fqi";

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
    @SuppressWarnings({"Convert2Lambda"})
    private final Attribute<T, String> CQE_ATTR_IDENTIFIER = attribute("identifier", new SimpleFunction<>() {
        @Override
        public String apply(T item) {
            return item.getIdentifier() != null ? item.getIdentifier().toLowerCase() : "";
        }
    });

    /**
     * See {@link #CQE_ATTR_FQI}
     */
    @SuppressWarnings({"Convert2Lambda"})
    private final Attribute<T, String> CQE_ATTR_NAME = attribute("name", new SimpleFunction<>() {
        @Override
        public String apply(T item) {
            return item.getName() != null ? item.getName().toLowerCase() : "";
        }
    });

    /**
     * See {@link #CQE_ATTR_FQI}
     */
    @SuppressWarnings({"Convert2Lambda"})
    private final Attribute<T, String> CQE_ATTR_ADDRESS = attribute("address", new SimpleFunction<>() {
        @Override
        public String apply(T item) {
            return item.getAddress() != null ? item.getAddress().toLowerCase() : "";
        }
    });

    private final SQLParser<T> parser;

    private IndexedCollection<T> index = new ConcurrentIndexedCollection<>();

    /**
     * Creates a new empty index.
     */
    public ItemIndex(Class<T> tClass) {

        //init cq engine
        parser = SQLParser.forPojoWithAttributes(tClass,
                Map.of(
                        CQE_FIELD_FQI, CQE_ATTR_FQI,
                        "identifier", CQE_ATTR_IDENTIFIER,
                        "name", CQE_ATTR_NAME,
                        "address", CQE_ATTR_ADDRESS)
        );

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
     * @todo refactor, test
     */
    public Collection<T> query(String term) {
        if ("*".equals(term)) {
            return all();
        }

        if (term.contains("/")) {

            if (URLFactory.getURL(term).isPresent()) {
                term = "address = '" + term + "'";
            } else if (!term.contains(" ")) {
                Optional<ItemMatcher> itemMatcher = ItemMatcher.forTarget(term);
                if (itemMatcher.isPresent()) {
                    return findAll(itemMatcher.get());
                }

                //something else that is not an url nor identifier nor where-condition
                term = String.format("(identifier = '%s' OR name = '%s')", term, term);
            }
        }

        //single word compared against identifier
        String query = term.matches(PATTERN) ? selectByIdentifierOrName(term) : "SELECT * FROM items WHERE " + term;
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
    @NonNull
    public T pick(final String identifier, String group) {
        if (!StringUtils.hasLength(identifier)) {
            throw new IllegalArgumentException("Identifier to pick is empty");
        }

        return find(identifier, group).orElseThrow(() ->
                new NoSuchElementException(String.format("Element '%s' not found  in collection %s.", identifier , all()))
        );
    }

    /**
     * Returns a the item from the list or null. Uses the matching criteria of {@link FullyQualifiedIdentifier}
     *
     * @param identifier the identifier
     * @return the item or null
     */
    public Optional<T> find(String identifier, String group) {
        if (!StringUtils.hasLength(identifier)) {
            throw new IllegalArgumentException("Identifier to find is empty");
        }

        List<T> found = findAll(identifier, group);

        if (found.size() > 1) {
            throw new SearchException(String.format("Ambiguous result for %s/%s: %s in collection ", group, identifier, found));
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
            throw new SearchException(String.format("Ambiguous result for %s: %s in collection.", itemMatcher, found));
        }

        return Optional.ofNullable((found.size() == 1) ? found.get(0) : null);
    }

    public List<T> cqnQueryOnIndex(String condition) {
        try {
            ResultSet<T> results = parser.retrieve(index, condition);
            return results.stream().collect(Collectors.toList());
        } catch (InvalidQueryException e) {
            throw new SearchException(String.format("Failed to run query '%s'", condition), e);
        }
    }

    private List<T> findAll(final String identifier, final String group) {
        return findAll(ItemMatcher.build(null, group, identifier));
    }

    /**
     * Find all items matching the given matcher.
     *
     * @param itemMatcher the search criteria
     * @return list of results
     */
    public List<T> findAll(@NonNull final ItemMatcher itemMatcher) {
        Objects.requireNonNull(itemMatcher, "ItemMatcher is null");
        return itemStream()
                .filter(item -> itemMatcher.isSimilarTo(item.getFullyQualifiedIdentifier()))
                .collect(Collectors.toList());
    }

    /**
     * Finds all items matching the given term.
     *
     * First tries an {@link ItemMatcher} and then falls back to a regular query.
     *
     * @param term preferably string representation of an FQI, or a simple identifier
     * @return all matched items
     */
    public List<T> findBy(@NonNull final String term) {
        Objects.requireNonNull(term);

        return ItemMatcher.forTarget(term)
                .map(this::findAll)
                .orElseGet(() -> new ArrayList<>(query(term)));
    }

    /**
     * Returns one distinct item for a query term.
     *
     * @param term  search term
     * @param group optional group to narrow
     * @return the matched item
     * @throws NoSuchElementException if not exactly one item could be determined
     */
    public T findOneBy(@NonNull final String term, @Nullable final String group) {
        List<T> items = findBy(term);
        if (items == null || items.isEmpty()) {
            throw new NoSuchElementException("Could not extract distinct item from empty list");
        }

        if (items.size() == 1) {
            return items.get(0);
        }

        if (group == null) {
            throw new NoSuchElementException("Could not extract distinct item from ambiguous result without group: " + items);
        }

        return firstWithGroup(items, group).orElseThrow(() -> new NoSuchElementException("Could not extract distinct item from ambiguous result: " + items));
    }

    public Optional<T> firstWithGroup(Collection<T> items, String group) {
        return items.stream().filter(item -> group.equalsIgnoreCase(item.getGroup())).findFirst();
    }


    public void remove(T item) {
        index.remove(item);
    }

    /**
     * Retrieves all Items corresponding the set of FQIs.
     *
     * @param fullyQualifiedIdentifiers a set of fqis
     * @return a set of components (e.g. items) in the same order as the given FQIs
     */
    public Set<T> retrieve(@NonNull Set<FullyQualifiedIdentifier> fullyQualifiedIdentifiers) {
        Query<T> nativeQuery = in(CQE_ATTR_FQI, fullyQualifiedIdentifiers);
        Map<FullyQualifiedIdentifier, T> collect = index.retrieve(nativeQuery).stream().collect(Collectors.toMap(Component::getFullyQualifiedIdentifier, o -> o));
        LinkedHashSet<T> result = new LinkedHashSet<>();
        fullyQualifiedIdentifiers.forEach(fqi -> result.add(
                Optional.ofNullable(collect.get(fqi))
                        .orElseThrow(() -> new NoSuchElementException(String.format("Could not retrieve item with fqi %s: Mismatching state of item index?", fqi)))
        ));
        return result;
    }

    public T pick(FullyQualifiedIdentifier fqi) {
        return pick(fqi.getItem(), fqi.getGroup());
    }
}
