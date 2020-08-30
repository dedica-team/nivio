package de.bonndan.nivio.model;

import com.googlecode.cqengine.ConcurrentIndexedCollection;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.support.SimpleFunction;
import com.googlecode.cqengine.query.parser.sql.SQLParser;
import com.googlecode.cqengine.resultset.ResultSet;
import org.springframework.util.StringUtils;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.googlecode.cqengine.query.QueryFactory.attribute;
import static de.bonndan.nivio.model.LandscapeItem.IDENTIFIER_VALIDATION;

/**
 * A queryable index on all landscape items.
 *
 *
 *
 */
public class LandscapeItems {

    /**
     * The {@link com.googlecode.cqengine.query.QueryFactory#attribute(String, SimpleFunction)})} relies on a method
     * {@link net.jodah.typetools.TypeResolver#resolveRawArguments(Type, Class)}, which in Java 13 is not able to retrieve
     * information about the generic types, if a lambda or anonymous method reference is provided. By providing an anonymous
     * class of the {@link SimpleFunction}, the generic types can be resolved without running into exceptions.
     */
    @SuppressWarnings({"Convert2Lambda", "Anonymous2MethodRef"})
    private static final Attribute<Item, String> IDENTIFIER = attribute("identifier", new SimpleFunction<>() {
        @Override
        public String apply(Item item) {
            return item.getIdentifier();
        }
    });

    /**
     * See {@link #IDENTIFIER}
     */
    @SuppressWarnings({"Convert2Lambda", "Anonymous2MethodRef"})
    private static final Attribute<Item, String> NAME = attribute("name", new SimpleFunction<>() {
        @Override
        public String apply(Item item) {
            return item.getName();
        }
    });

    IndexedCollection<Item> index = new ConcurrentIndexedCollection<>();

    /**
     * @deprecated use only for testing
     */
    public static LandscapeItems of(List<Item> items) {
        LandscapeItems landscapeItems = new LandscapeItems();
        landscapeItems.setItems(new HashSet<>(items));
        return landscapeItems;
    }

    public Stream<Item> stream() {
        return index.stream();
    }

    public void setItems(Set<Item> items) {
        index = new ConcurrentIndexedCollection<>();
        index.addAll(items);
    }

    public void add(Item item) {
        index.add(item);
    }

    /**
     * Returns all items matching the given term.
     *
     * @param term "*" as wildcard for all | {@link FullyQualifiedIdentifier} string pathes | identifier
     * @return all matching items.
     */
    public Collection<Item> query(String term) {
        if ("*".equals(term))
            return all();

        if (term.contains("/")) {
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
        return "SELECT * FROM items WHERE (identifier = '" + term + "' OR name = '" + term + "')";
    }

    public Set<Item> all() {
        return index;
    }

    /**
     * Ensures that the given item has a sibling in the list, returns the item from the list.
     *
     * @param item item to search for
     * @return the sibling from the list
     */
    public Item pick(final LandscapeItem item) {
        return pick(item.getIdentifier(), item.getGroup());
    }

    /**
     * Makes sure the sibling of the item is returned or throws an exception.
     *
     * @param identifier identifier
     * @param group      the group to search in
     * @return the sibling with the given identifier
     */
    public Item pick(final String identifier, String group) {
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
    public Optional<Item> find(String identifier, String group) {
        if (StringUtils.isEmpty(identifier)) {
            throw new IllegalArgumentException("Identifier to find is empty");
        }

        List<Item> found = findAll(identifier, group);

        if (found.size() > 1)
            throw new RuntimeException("Ambiguous result for " + group + "/" + identifier + ": " + found + " in collection ");

        return Optional.ofNullable((found.size() == 1) ? found.get(0) : null);
    }

    /**
     * Returns a the item from the list or null. Uses the matching criteria of {@link ItemMatcher}
     *
     * @param itemMatcher the identifier
     * @return the or null
     */
    public Optional<Item> find(ItemMatcher itemMatcher) {
        List<Item> found = findAll(itemMatcher);

        if (found.size() > 1) {
            throw new RuntimeException("Ambiguous result for " + itemMatcher + ": " + found + " in collection.");
        }

        return Optional.ofNullable((found.size() == 1) ? found.get(0) : null);
    }

    public List<Item> cqnQueryOnIndex(String condition) {
        SQLParser<Item> parser = SQLParser.forPojoWithAttributes(Item.class,
                Map.of("identifier", IDENTIFIER, "name", NAME)
        );

        ResultSet<Item> results = parser.retrieve(index, condition);
        return results.stream().collect(Collectors.toList());
    }

    private List<Item> findAll(final String identifier, final String group) {
        return findAll(ItemMatcher.build(null, group, identifier));
    }

    private List<Item> findAll(ItemMatcher itemMatcher) {
        return stream()
                .filter(itemMatcher::isSimilarTo)
                .collect(Collectors.toList());
    }
}
