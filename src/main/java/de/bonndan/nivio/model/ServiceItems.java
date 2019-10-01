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

public class ServiceItems {

    /**
     * Returns all elements kept in the second list.
     */
    public static List<LandscapeItem> kept(Collection<? extends LandscapeItem> items1, Collection<? extends LandscapeItem> items2) {
        return items2.stream().filter(item -> exists(item, items1)).collect(Collectors.toList());
    }

    /**
     * Returns all elements removed from the second list.
     */
    public static List<LandscapeItem> removed(Collection<? extends LandscapeItem> items1, Collection<? extends LandscapeItem> items2) {
        return items2.stream().filter(item -> !exists(item, items1)).collect(Collectors.toList());
    }

    /**
     * Returns all elements which are not in the second list
     */
    public static List<LandscapeItem> added(Collection<? extends LandscapeItem> items1, Collection<? extends LandscapeItem> existing) {
        return items1.stream()
                .filter(item -> !exists(item, existing))
                .collect(Collectors.toList());
    }

    private static boolean exists(LandscapeItem item, Collection<? extends LandscapeItem> items) {
        return items.stream().anyMatch(
                inList -> item.getFullyQualifiedIdentifier().isSimilarTo(inList)
        );
    }

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
     * @param identifier  service identifier
     * @param group       the group to search in
     * @param serviceList all services
     * @return the sibling with the given identifier
     */
    public static LandscapeItem pick(final String identifier, String group, final Collection<? extends LandscapeItem> serviceList) {
        if (StringUtils.isEmpty(identifier)) {
            throw new IllegalArgumentException("Identifier is empty");
        }

        return find(identifier, group, serviceList).orElseThrow(() ->
                new RuntimeException("Element not found " + identifier + " in collection " + serviceList)
        );
    }

    /**
     * Returns a the item from the list or null. Uses the matching criteria of {@link FullyQualifiedIdentifier}
     *
     * @param identifier the service identifier
     * @param items      all services
     * @return the service or null
     */
    public static Optional<LandscapeItem> find(String identifier, String group, Collection<? extends LandscapeItem> items) {
        if (StringUtils.isEmpty(identifier)) {
            throw new IllegalArgumentException("Identifier is empty");
        }

        List<LandscapeItem> found = findAll(identifier, group, items);

        if (found.size() > 1)
            throw new RuntimeException("Ambiguous result for " + group + "/" + identifier + ": " + found + " in collection " + items);

        return Optional.ofNullable((found.size() == 1) ? found.get(0): null);
    }

    private static List<LandscapeItem> findAll(
            final String identifier,
            final String group,
            final Collection<? extends LandscapeItem> serviceList
    ) {
        FullyQualifiedIdentifier fqi;
        if (group == null)
            fqi = FullyQualifiedIdentifier.from(identifier);
        else
            fqi = FullyQualifiedIdentifier.build(null, group, identifier);
        return findAll(fqi, serviceList);
    }

    private static List<LandscapeItem> findAll(FullyQualifiedIdentifier fqi, Collection<? extends LandscapeItem> serviceList) {
        return serviceList.stream()
                .filter(fqi::isSimilarTo)
                .collect(Collectors.toList());
    }

    /**
     * Returns a the item from the list or null. Uses the matching criteria of {@link FullyQualifiedIdentifier}
     *
     * @param fqi      the service identifier
     * @param services all services
     * @return the service or null
     */
    public static Optional<LandscapeItem> find(FullyQualifiedIdentifier fqi, Collection<? extends LandscapeItem> services) {
        List<LandscapeItem> found = findAll(fqi, services);

        if (found.size() > 1)
            throw new RuntimeException("Ambiguous result for " + fqi + ": " + found + " in collection " + services);

        return Optional.ofNullable((found.size() == 1) ? found.get(0): null);
    }

    public static List<? extends LandscapeItem> filter(String condition, List<? extends LandscapeItem> services) {

        if ("*" .equals(condition))
            return services;

        //single word compared against identifier
        if (condition.matches(IDENTIFIER_VALIDATION)) {
            return services.stream()
                    .filter(serviceItem -> serviceItem.getIdentifier().equals(condition))
                    .collect(Collectors.toList());
        }

        if (condition.contains("/")) {
            FullyQualifiedIdentifier from = FullyQualifiedIdentifier.from(condition);
            return findAll(from, services);
        }

        String query = "SELECT * FROM services WHERE " + condition;
        return query(query, services);
    }

    public static final Attribute<LandscapeItem, String> IDENTIFIER = attribute("identifier", LandscapeItem::getIdentifier);
    public static final Attribute<LandscapeItem, String> NAME = attribute("name", LandscapeItem::getName);

    /**
     * Run the condition as CQN query. See https://github.com/npgall/cqengine
     *
     * @param condition query where part
     * @param serviceItems list to operate on
     * @return resultset
     */
    public static List<? extends LandscapeItem> query(String condition, List<? extends LandscapeItem> serviceItems) {
        SQLParser<LandscapeItem> parser = SQLParser.forPojoWithAttributes(LandscapeItem.class,
                Map.of("identifier", IDENTIFIER, "name", NAME)
        );
        IndexedCollection<LandscapeItem> index = new ConcurrentIndexedCollection<>();
        index.addAll(serviceItems);

        ResultSet<LandscapeItem> results = parser.retrieve(index, condition);

        return results.stream().collect(Collectors.toList());
    }
}
