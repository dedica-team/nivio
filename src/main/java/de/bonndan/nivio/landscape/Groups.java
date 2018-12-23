package de.bonndan.nivio.landscape;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.util.StringUtils.isEmpty;

public class Groups {

    private Map<String, List<LandscapeItem>> groups = new HashMap<>();

    public void add(Service service) {
        if (isEmpty(service.getGroup()))
            return;

        if (!groups.containsKey(service.getGroup())) {
            groups.put(service.getGroup(), new ArrayList<>());
        }
        groups.get(service.getGroup()).add(service);
    }

    public Map<String, List<LandscapeItem>> getAll() {
        return groups;
    }
}
