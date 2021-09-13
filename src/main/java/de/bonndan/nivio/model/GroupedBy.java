package de.bonndan.nivio.model;

import org.springframework.lang.NonNull;

import java.util.*;
import java.util.function.Function;

import static org.springframework.util.StringUtils.hasLength;

/**
 * Helper to groups Items differently (e.g. by owner).
 */
public class GroupedBy {

    private final Map<String, List<Item>> groups = new HashMap<>();

    /**
     * Groups services by any string field (e.g. owner).
     *
     * @param supplier function providing the group key for each item
     * @param items    items to group
     * @return grouped items
     */
    @NonNull
    public static GroupedBy by(Function<Item, String> supplier, Collection<Item> items) {
        var groups = new GroupedBy();
        items.forEach(item -> groups.add(supplier.apply(item), item));
        return groups;
    }

    /**
     * Returns the services sorted by group.
     *
     * @return map with keys naming the groups
     */
    @NonNull
    public Map<String, List<Item>> getAll() {
        return groups;
    }

    /**
     * Add a service into a group by a custom field
     *
     * @param groupKey the group key
     * @param item     service to add
     */
    private void add(String groupKey, Item item) {
        String key = !hasLength(groupKey) ? Group.COMMON : groupKey;
        groups.computeIfAbsent(key, s -> new ArrayList<>()).add(item);
    }
}
