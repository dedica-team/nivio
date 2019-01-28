package de.bonndan.nivio.landscape;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.util.StringUtils.isEmpty;

public class Groups {

    public static final String COMMON = "Common";

    private Map<String, List<ServiceItem>> groups = new HashMap<>();

    public static Groups from(Landscape landscape) {
        Groups groups = new Groups();
        landscape.getServices().forEach(groups::add);
        return groups;
    }

    public void add(Service service) {

        String key = isEmpty(service.getGroup()) ? COMMON : service.getGroup();

        if (!groups.containsKey(key)) {
            groups.put(key, new ArrayList<>());
        }
        groups.get(key).add(service);
    }

    public Map<String, List<ServiceItem>> getAll() {
        return groups;
    }
}
