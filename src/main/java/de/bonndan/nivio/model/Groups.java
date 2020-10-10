package de.bonndan.nivio.model;

import de.bonndan.nivio.input.dto.GroupDescription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static de.bonndan.nivio.util.SafeAssign.assignSafeIfAbsent;
import static org.springframework.util.StringUtils.isEmpty;

public class Groups {

    private final Map<String, List<LandscapeItem>> groups = new HashMap<>();

    /**
     * Default grouping by group field.
     *
     * @param landscape landscape containing the item
     * @return groups
     */
    public static Groups from(LandscapeImpl landscape) {
        Groups groups = new Groups();
        landscape.getItems().stream().forEach(groups::add);
        return groups;
    }

    /**
     * Merges all absent values from the second param into the first.
     */
    public static void merge(final Group group, Group groupItem) {
        if (groupItem == null)
            return;

        assignSafeIfAbsent(groupItem.getColor(), group.getColor(), group::setColor);
        assignSafeIfAbsent(groupItem.getContact(), group.getContact(), group::setContact);
        assignSafeIfAbsent(groupItem.getDescription(), group.getDescription(), group::setDescription);
        assignSafeIfAbsent(groupItem.getOwner(), group.getOwner(), group::setOwner);
        groupItem.getLinks().forEach((s, url) -> group.getLinks().putIfAbsent(s, url));
        Labeled.merge(groupItem, group);
    }

    public static void mergeWithGroupDescription(final Group group, GroupDescription groupDescription) {
        if (groupDescription == null)
            return;

        assignSafeIfAbsent(groupDescription.getColor(), group.getColor(), group::setColor);
        assignSafeIfAbsent(groupDescription.getContact(), group.getContact(), group::setContact);
        assignSafeIfAbsent(groupDescription.getDescription(), group.getDescription(), group::setDescription);
        assignSafeIfAbsent(groupDescription.getOwner(), group.getOwner(), group::setOwner);
        groupDescription.getLinks().forEach((s, url) -> group.getLinks().putIfAbsent(s, url));
        Labeled.merge(groupDescription, group);
    }

    /**
     * Add a service into a group by a custom field
     *
     * @param groupKey the group key
     * @param service  service to add
     */
    @Deprecated
    public void add(String groupKey, LandscapeItem service) {

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
    public Map<String, List<LandscapeItem>> getAll() {
        return groups;
    }

    /**
     * Groups services by any string field (e.g. owner).
     *
     * @param supplier function providing the group key for each item
     * @param items    services
     * @return grouped services
     */
    public static Groups by(Function<LandscapeItem, String> supplier, List<LandscapeItem> items) {
        var groups = new Groups();
        items.forEach(serviceItem -> {
            String key = supplier.apply(serviceItem);
            groups.add(key, serviceItem);
        });

        return groups;
    }

    private void add(LandscapeItem service) {
        add(service.getGroup(), service);
    }

}
