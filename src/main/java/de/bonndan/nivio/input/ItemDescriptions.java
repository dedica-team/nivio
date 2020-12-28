package de.bonndan.nivio.input;

import com.googlecode.cqengine.ConcurrentIndexedCollection;
import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.support.SimpleFunction;
import com.googlecode.cqengine.query.parser.sql.SQLParser;
import com.googlecode.cqengine.resultset.ResultSet;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import de.bonndan.nivio.model.ItemMatcher;
import org.springframework.util.StringUtils;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

import static com.googlecode.cqengine.query.QueryFactory.attribute;
import static de.bonndan.nivio.model.Item.IDENTIFIER_VALIDATION;

public class ItemDescriptions {

    /**
     * The {@link com.googlecode.cqengine.query.QueryFactory#attribute(String, SimpleFunction)})} relies on a method
     * {@link net.jodah.typetools.TypeResolver#resolveRawArguments(Type, Class)}, which in Java 13 is not able to retrieve
     * information about the generic types, if a lambda or anonymous method reference is provided. By providing an anonymous
     * class of the {@link SimpleFunction}, the generic types can be resolved without running into exceptions.
     */
    @SuppressWarnings({"Convert2Lambda", "Anonymous2MethodRef"})
    private static final Attribute<ItemDescription, String> IDENTIFIER = attribute("identifier", new SimpleFunction<>() {
        @Override
        public String apply(ItemDescription itemDescription) {
            return itemDescription.getIdentifier();
        }
    });

    /**
     * See {@link #IDENTIFIER}
     */
    @SuppressWarnings({"Convert2Lambda", "Anonymous2MethodRef"})
    private static final Attribute<ItemDescription, String> NAME = attribute("name", new SimpleFunction<>() {
        @Override
        public String apply(ItemDescription itemDescription) {
            return itemDescription.getName();
        }
    });

    private ConcurrentIndexedCollection<ItemDescription> index = new ConcurrentIndexedCollection<>();

    /**
     * Ensures that the given item has a sibling in the list, returns the item from the list.
     *
     * @param item  item to search for
     * @return the sibling from the list
     */
    public ItemDescription pick(final ItemDescription item) {
        return pick(item.getIdentifier(), item.getGroup());
    }

    /**
     * Makes sure the sibling of the item is returned or throws an exception.
     *
     * @param identifier identifier
     * @param group      the group to search in
     * @return the sibling with the given identifier
     */
    public ItemDescription pick(final String identifier, String group) {
        if (StringUtils.isEmpty(identifier)) {
            throw new IllegalArgumentException("Identifier to pick is empty");
        }

        return find(identifier, group).orElseThrow(() ->
                new RuntimeException(String.format("Element '%s' not found  in collection ", identifier))
        );
    }

    /**
     * Returns a the item from the list or null. Uses the matching criteria of {@link ItemMatcher}
     *
     * @param identifier the identifier
     * @return the item or null
     */
    public Optional<ItemDescription> find(String identifier, String group) {
        if (StringUtils.isEmpty(identifier)) {
            throw new IllegalArgumentException("Identifier to find is empty");
        }

        List<ItemDescription> found = findAll(identifier, group);

        if (found.size() > 1) {
            throw new RuntimeException("Ambiguous result for " + group + "/" + identifier + ": " + found + " in collection ");
        }

        return Optional.ofNullable((found.size() == 1) ? found.get(0) : null);
    }

    private List<ItemDescription> findAll(
            final String identifier,
            final String group
    ) {
        return findAll(ItemMatcher.build(null, group, identifier));
    }

    private List<ItemDescription> findAll(ItemMatcher itemMatcher) {
        return index.stream()
                .filter(itemDescription -> itemMatcher.isSimilarTo(itemDescription.getFullyQualifiedIdentifier()))
                .collect(Collectors.toList());
    }

    /**
     * Returns a the item from the list or null. Uses the matching criteria of {@link FullyQualifiedIdentifier}
     *
     * @param itemMatcher  the identifier
     * @return the or null
     */
    public Optional<ItemDescription> find(ItemMatcher itemMatcher) {
        List<ItemDescription> found = findAll(itemMatcher);

        if (found.size() > 1)
            throw new RuntimeException("Ambiguous result for " + itemMatcher + ": " + found + " in collection ");

        return Optional.ofNullable((found.size() == 1) ? found.get(0) : null);
    }

    /**
     * Executes a search query.
     *
     * @param term wildcard, ItemMatcher as string, or sql-like query where-condition
     * @return matched items
     */
    public Collection<? extends ItemDescription> query(String term) {

        if ("*".equals(term))
            return index;

        if (term.contains(ItemMatcher.SEPARATOR)) {
            return findAll(ItemMatcher.forTarget(term));
        }

        //single word compared against identifier
        String query = term.matches(IDENTIFIER_VALIDATION) ? selectByIdentifierOrName(term) : "SELECT * FROM items WHERE " + term;
        return cqnQueryOnIndex(query);
    }

    private static String selectByIdentifierOrName(String term) {
        return "SELECT * FROM items WHERE (identifier = '" + term + "' OR name = '" + term + "')";
    }

    private List<ItemDescription> cqnQueryOnIndex(String condition) {
        SQLParser<ItemDescription> parser = SQLParser.forPojoWithAttributes(ItemDescription.class,
                Map.of("identifier", IDENTIFIER, "name", NAME)
        );

        ResultSet<ItemDescription> results = parser.retrieve(index, condition);

        return results.stream().collect(Collectors.toList());
    }

    public void set(List<ItemDescription> itemDescriptions) {
        index = new ConcurrentIndexedCollection<>();
        index.addAll(itemDescriptions);
    }

    public Set<ItemDescription> all() {
        return index;
    }

    public void add(ItemDescription description) {
        index.add(description);
    }

    public void remove(ItemDescription description) {
        index.remove(description);
    }
}
