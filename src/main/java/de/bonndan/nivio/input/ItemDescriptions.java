package de.bonndan.nivio.input;


import com.googlecode.cqengine.ConcurrentIndexedCollection;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.query.parser.sql.SQLParser;
import com.googlecode.cqengine.resultset.ResultSet;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import de.bonndan.nivio.model.LandscapeItem;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.googlecode.cqengine.query.QueryFactory.attribute;
import static de.bonndan.nivio.model.LandscapeItem.IDENTIFIER_VALIDATION;

public class ItemDescriptions {

    private static final Attribute<ItemDescription, String> IDENTIFIER = attribute("identifier", ItemDescription::getIdentifier);
    private static final Attribute<ItemDescription, String> NAME = attribute("name", ItemDescription::getName);

    private ConcurrentIndexedCollection<ItemDescription> index = new ConcurrentIndexedCollection<>();

    /**
     * Ensures that the given item has a sibling in the list, returns the item from the list.
     *
     * @param item  item to search for
     * @return the sibling from the list
     */
    public LandscapeItem pick(final LandscapeItem item) {
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
                new RuntimeException("Element '" + identifier + "' not found  in collection ")
        );
    }

    /**
     * Returns a the item from the list or null. Uses the matching criteria of {@link FullyQualifiedIdentifier}
     *
     * @param identifier the identifier
     * @return the item or null
     */
    public Optional<ItemDescription> find(String identifier, String group) {
        if (StringUtils.isEmpty(identifier)) {
            throw new IllegalArgumentException("Identifier to find is empty");
        }

        List<ItemDescription> found = findAll(identifier, group);

        if (found.size() > 1)
            throw new RuntimeException("Ambiguous result for " + group + "/" + identifier + ": " + found + " in collection ");

        return Optional.ofNullable((found.size() == 1) ? found.get(0) : null);
    }

    private List<ItemDescription> findAll(
            final String identifier,
            final String group
    ) {
        FullyQualifiedIdentifier fqi;
        if (group == null)
            fqi = FullyQualifiedIdentifier.from(identifier);
        else
            fqi = FullyQualifiedIdentifier.build(null, group, identifier);
        return findAll(fqi);
    }

    private List<ItemDescription> findAll(FullyQualifiedIdentifier fqi) {
        return index.stream().filter(fqi::isSimilarTo).collect(Collectors.toList());
    }

    /**
     * Returns a the item from the list or null. Uses the matching criteria of {@link FullyQualifiedIdentifier}
     *
     * @param fqi   the identifier
     * @return the or null
     */
    public Optional<LandscapeItem> find(FullyQualifiedIdentifier fqi) {
        List<ItemDescription> found = findAll(fqi);

        if (found.size() > 1)
            throw new RuntimeException("Ambiguous result for " + fqi + ": " + found + " in collection ");

        return Optional.ofNullable((found.size() == 1) ? found.get(0) : null);
    }

    public Collection<? extends LandscapeItem> query(String term) {

        if ("*".equals(term))
            return index;

        if (term.contains("/")) {
            FullyQualifiedIdentifier from = FullyQualifiedIdentifier.from(term);
            return findAll(from);
        }

        //single word compared against identifier
        String query = term.matches(IDENTIFIER_VALIDATION) ? selectByIdentifierOrName(term) : "SELECT * FROM items WHERE " + term;
        return cqnQueryOnIndex(query);
    }

    private static String selectByIdentifierOrName(String term) {
        return "SELECT * FROM items WHERE (identifier = '" + term + "' OR name = '" + term + "')";
    }

    /**
     * Puts all items into an indexed collection for querying.
     *
     * @param items landscape items
     * @return indexed collection
     */
    private static IndexedCollection<LandscapeItem> index(Collection<? extends LandscapeItem> items) {
        IndexedCollection<LandscapeItem> index = new ConcurrentIndexedCollection<>();
        index.addAll(items);
        return index;
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
