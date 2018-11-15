package de.bonndan.nivio.output.dld4e;

import de.bonndan.nivio.landscape.Service;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.isEmpty;

public class Icons {

    private Map<Service, Icon> icons = new HashMap<>();

    public void add(Service service) {
        Icon icon = new Icon(service);
        icon.merge("service");
        icon.set("icon", "\"" + IconFamily.AzureEnterprise.iconFor(service) + "\"");
        icons.put(service, icon);
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
                .append(icon.getService().getIdentifier())
                .append(": ")
                .append(icon.inline()));
        sb.append("\n");
        return sb.toString();
    }

    private String getName(Service service) {
        return !isEmpty(service.getName()) ? service.getName() : service.getIdentifier();
    }

    public Optional<Icon> by(Service service) {

        return icons.entrySet().stream()
                .map(serviceIconEntry -> serviceIconEntry.getValue())
                .filter(icon -> icon.getService() == service).findFirst();
    }
}
