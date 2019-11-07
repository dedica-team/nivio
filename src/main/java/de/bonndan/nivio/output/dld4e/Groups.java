package de.bonndan.nivio.output.dld4e;

import de.bonndan.nivio.model.Item;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.util.StringUtils.isEmpty;

public class Groups {

    private Map<String, List<Item>> groups = new HashMap<>();

    public void add(Item item) {
        if (isEmpty(item.getGroup()))
            return;

        if (!groups.containsKey(item.getGroup())) {
            groups.put(item.getGroup(), new ArrayList<>());
        }
        groups.get(item.getGroup()).add(item);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("groups:\n");
        groups.forEach((g, members) -> {
            sb.append("  " + g + ": {<<: *group, ");
            sb.append("name: " + g + ", ");
            String[] ids = members.stream().map(Item::getIdentifier).toArray(String[]::new);
            sb.append("members: [" + StringUtils.arrayToDelimitedString(ids, ",") + "]");
            sb.append("}\n");
        });
        return sb.toString();
    }

    public Map<String, List<Item>> getAll() {
        return groups;
    }
}
