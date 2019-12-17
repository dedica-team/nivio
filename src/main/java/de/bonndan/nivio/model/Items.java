package de.bonndan.nivio.model;


import com.googlecode.cqengine.ConcurrentIndexedCollection;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.query.parser.sql.SQLParser;
import com.googlecode.cqengine.resultset.ResultSet;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.googlecode.cqengine.query.QueryFactory.attribute;
import static de.bonndan.nivio.model.LandscapeItem.IDENTIFIER_VALIDATION;

public class Items {

    private static final Attribute<LandscapeItem, String> IDENTIFIER = attribute("identifier", LandscapeItem::getIdentifier);
    private static final Attribute<LandscapeItem, String> NAME = attribute("name", LandscapeItem::getName);

    /**
     * Ensures that the given item has a sibling in the list, returns the item from the list.
     *
     * @param item  item to search for
     * @param items list of landscape items
     * @return the sibling from the list
     */
    public static LandscapeItem pick(final LandscapeItem item, final Collection<? extends LandscapeItem> items) {
        return pick(item.getIdentifier(), item.getGroup(), items);
    }

    /**
     * Makes sure the sibling of the item is returned or throws an exception.
     *
     * @param identifier identifier
     * @param group      the group to search in
     * @param items      all items
     * @return the sibling with the given identifier
     */
    public static LandscapeItem pick(final String identifier, String group, final Collection<? extends LandscapeItem> items) {
        if (StringUtils.isEmpty(identifier)) {
            throw new IllegalArgumentException("Identifier to pick is empty");
        }

        return find(identifier, group, items).orElseThrow(() ->
                new RuntimeException("Element '" + identifier + "' not found  in collection " + items)
        );
    }

    /**
     * Returns a the item from the list or null. Uses the matching criteria of {@link FullyQualifiedIdentifier}
     *
     * @param identifier the identifier
     * @param items      all items
     * @return the item or null
     */
    public static Optional<LandscapeItem> find(String identifier, String group, Collection<? extends LandscapeItem> items) {
        if (StringUtils.isEmpty(identifier)) {
            throw new IllegalArgumentException("Identifier to find is empty");
        }

        List<LandscapeItem> found = findAll(identifier, group, items);

        if (found.size() > 1)
            throw new RuntimeException("Ambiguous result for " + group + "/" + identifier + ": " + found + " in collection " + items);

        return Optional.ofNullable((found.size() == 1) ? found.get(0) : null);
    }

    private static List<LandscapeItem> findAll(
            final String identifier,
            final String group,
            final Collection<? extends LandscapeItem> items
    ) {
        FullyQualifiedIdentifier fqi;
        if (group == null)
            fqi = FullyQualifiedIdentifier.from(identifier);
        else
            fqi = FullyQualifiedIdentifier.build(null, group, identifier);
        return findAll(fqi, items);
    }

    private static List<LandscapeItem> findAll(FullyQualifiedIdentifier fqi, Collection<? extends LandscapeItem> items) {
        return items.stream()
                .filter(fqi::isSimilarTo)
                .collect(Collectors.toList());
    }

    /**
     * Returns a the item from the list or null. Uses the matching criteria of {@link FullyQualifiedIdentifier}
     *
     * @param fqi   the identifier
     * @param items all items
     * @return the or null
     */
    public static Optional<LandscapeItem> find(FullyQualifiedIdentifier fqi, Collection<? extends LandscapeItem> items) {
        List<LandscapeItem> found = findAll(fqi, items);

        if (found.size() > 1)
            throw new RuntimeException("Ambiguous result for " + fqi + ": " + found + " in collection " + items);

        return Optional.ofNullable((found.size() == 1) ? found.get(0) : null);
    }

    public static Collection<? extends LandscapeItem> query(String term, Collection<? extends LandscapeItem> items) {

        if ("*".equals(term))
            return items;

        if (term.contains("/")) {
            FullyQualifiedIdentifier from = FullyQualifiedIdentifier.from(term);
            return findAll(from, items);
        }

        //single word compared against identifier
        String query = term.matches(IDENTIFIER_VALIDATION) ? selectByIdentifierOrName(term) : "SELECT * FROM items WHERE " + term;
        return cqnQueryOnIndex(query, index(items));
    }

    public static String selectByIdentifierOrName(String term) {
        return "SELECT * FROM items WHERE (identifier = '" + term + "' OR name = '" + term + "')";
    }

    /**
     * Puts all items into an indexed collection for querying.
     *
     * @param items landscape items
     * @return indexed collection
     */
    public static IndexedCollection<LandscapeItem> index(Collection<? extends LandscapeItem> items) {
        IndexedCollection<LandscapeItem> index = new ConcurrentIndexedCollection<>();
        index.addAll(items);
        return index;
    }

    public static List<? extends LandscapeItem> cqnQueryOnIndex(String condition, IndexedCollection<LandscapeItem> index) {
        SQLParser<LandscapeItem> parser = SQLParser.forPojoWithAttributes(LandscapeItem.class,
                Map.of("identifier", IDENTIFIER, "name", NAME)
        );

        ResultSet<LandscapeItem> results = parser.retrieve(index, condition);

        return results.stream().collect(Collectors.toList());
    }
}
