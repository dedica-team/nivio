package de.bonndan.nivio.model;

import de.bonndan.nivio.assessment.StatusValue;
import org.springframework.lang.NonNull;

import java.util.*;
import java.util.function.Function;

import static org.springframework.util.StringUtils.isEmpty;

/**
 * Helper to groups Items differently (e.g. by owner).
 */
public class GroupedBy {

    private final Map<String, List<Item>> groups = new HashMap<>();


    /**
     * Groups services by any string field (e.g. owner).
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
     *Groups services by the Map<String,String> field (e.g. labels).
     * @param sup function providing the group key for each item
     * @param items    items to group
     * @return grouped items
     */

    @NonNull
    public static GroupedBy newBy(Function<Item,Map<String,String>> sup, Collection<Item> items){
        var groups = new GroupedBy();
        String key = "lifecycle";
        items.forEach(item -> groups.add(sup.apply(item).get(key),item));
        return groups;
    }

    /**
     *Groups services by the Map<String,List<StatusValue>> field (e.g. results)
     * @param newSupplier function providing the group key for each item
     * @param items    items to group
     * @return grouped items
     */
    @NonNull
   public static GroupedBy newFunc(Function<Item,Map<String,List<StatusValue>>> newSupplier,Collection<Item>items){
        var groups = new GroupedBy();
        items.forEach(item -> groups.add(newSupplier.apply(item).get(item.getAssessmentIdentifier()).toString(),item));
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

        String key = isEmpty(groupKey) ? Group.COMMON : groupKey;

        if (!groups.containsKey(key)) {
            groups.put(key, new ArrayList<>());
        }
        groups.get(key).add(item);
    }
}
