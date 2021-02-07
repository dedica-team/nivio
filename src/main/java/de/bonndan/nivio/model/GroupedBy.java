package de.bonndan.nivio.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.springframework.util.StringUtils.isEmpty;

/**
 * Helper to groups Items differently (e.g. by owner).
 *
 *
 */
public class GroupedBy {

    private final Map<String, List<Item>> groups = new HashMap<>();

    /**
     * Default grouping by group field.
     *
     * @param landscape landscape containing the item
     * @return groups
     */
    public static GroupedBy from(Landscape landscape) {
        GroupedBy groupedBy = new GroupedBy();
        landscape.getItems().itemStream().forEach(groupedBy::add);
        return groupedBy;
    }

    /**
     * Add a service into a group by a custom field
     *
     * @param groupKey the group key
     * @param service  service to add
     */
    private void add(String groupKey, Item service) {

        String key = isEmpty(groupKey) ? Group.COMMON : groupKey;

        if (!groups.containsKey(key)) {
            groups.put(key, new ArrayList<>());
        }
        groups.get(key).add(service);
    }

    /**
     * Returns the services sorted by group.
     *
     * @return map with keys naming the groups
     */
    public Map<String, List<Item>> getAll() {
        return groups;
    }

    /**
     * Groups services by any string field (e.g. owner).
     *
     * @param supplier function providing the group key for each item
     * @param items    services
     * @return grouped services
     */
    public static GroupedBy by(Function<Item, String> supplier, List<Item> items) {
        var groups = new GroupedBy();
        items.forEach(serviceItem -> {
            String key = supplier.apply(serviceItem);
            groups.add(key, serviceItem);
        });

        return groups;
    }

    private void add(Item service) {
        add(service.getGroup(), service);
    }

}
