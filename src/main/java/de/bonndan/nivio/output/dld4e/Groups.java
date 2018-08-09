package de.bonndan.nivio.output.dld4e;

import de.bonndan.nivio.landscape.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.util.StringUtils.isEmpty;

public class Groups {

    private Map<String, List<Service>> groups = new HashMap<>();

    public void add(Service service) {
        if (isEmpty(service.getGroup()))
            return;

        if (!groups.containsKey(service.getGroup())) {
            groups.put(service.getGroup(), new ArrayList<>());
        }
        groups.get(service.getGroup()).add(service);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("groups:\n");
        groups.forEach((g, members) -> {
            sb.append("  " + g + ": {<<: *group, ");
            sb.append("name: " + g + ", ");
            String[] ids = members.stream().map(Service::getIdentifier).toArray(String[]::new);
            sb.append("members: [" + StringUtils.arrayToDelimitedString(ids, ",") + "]");
            sb.append("}\n");
        });
        return sb.toString();
    }

    public Map<String, List<Service>> getAll() {
        return groups;
    }
}
