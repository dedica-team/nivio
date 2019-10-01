package de.bonndan.nivio.output.dld4e;

import de.bonndan.nivio.model.Item;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.isEmpty;

public class Icons {

    private Map<Item, Icon> icons = new HashMap<>();

    public void add(Item item) {
        Icon icon = new Icon(item);
        icon.merge("service");
        icon.set("icon", "\"" + IconFamily.AzureEnterprise.iconFor(item) + "\"");
        icons.put(item, icon);
    }

    public List<Icon> getAll() {
        return icons.entrySet().stream()
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("\nicons:\n");
        icons.forEach((service, icon) -> sb.append("  ")
                .append(icon.getItem().getIdentifier())
                .append(": ")
                .append(icon.inline()));
        sb.append("\n");
        return sb.toString();
    }

    private String getName(Item item) {
        return !isEmpty(item.getName()) ? item.getName() : item.getIdentifier();
    }

    public Optional<Icon> by(Item item) {

        return icons.entrySet().stream()
                .map(serviceIconEntry -> serviceIconEntry.getValue())
                .filter(icon -> icon.getItem() == item).findFirst();
    }
}
