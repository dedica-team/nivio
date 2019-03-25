package de.bonndan.nivio.landscape;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.springframework.util.StringUtils.isEmpty;

public class Groups {

    public static final String COMMON = "Common";

    private Map<String, List<ServiceItem>> groups = new HashMap<>();

    /**
     * Default grouping by group field.
     *
     * @param landscape landscape containing the services
     * @return groups
     */
    public static Groups from(Landscape landscape) {
        Groups groups = new Groups();
        landscape.getServices().forEach(groups::add);
        return groups;
    }

    /**
     * Add a service into a group by a custom field
     *
     * @param groupKey the group key
     * @param service service to add
     */
    public void add(String groupKey, ServiceItem service) {

        String key = isEmpty(groupKey) ? COMMON : groupKey;

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
    public Map<String, List<ServiceItem>> getAll() {
        return groups;
    }

    /**
     * Groups services by any string field (e.g. owner).
     *
     * @param supplier function providing the group key for each item
     * @param items services
     * @return grouped services
     */
    public static Groups by(Function<ServiceItem, String> supplier, List<ServiceItem> items) {
        var groups = new Groups();
        items.forEach(serviceItem -> {
            String key = supplier.apply(serviceItem);
            groups.add(key, serviceItem);
        });

        return groups;
    }

    private void add(ServiceItem service) {
        add(service.getGroup(), service);
    }

}
