package de.bonndan.nivio.output.dld4e;

import de.bonndan.nivio.landscape.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.util.StringUtils.isEmpty;

public class Icons {

    private List<Icon> icons = new ArrayList<>();

    public void add(Service service) {
        Icon icon = new Icon(service);
        icon.merge("service");
        icon.set("icon", "\"" + IconFamily.AzureEnterprise.iconFor(service) + "\"");
        icons.add(icon);
    }

    public List<Icon> getAll() {
        return icons;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("\nicons:\n");
        icons.forEach((icon) -> {
            sb.append("  ")
                    .append(icon.getService().getIdentifier())
                    .append(": ")
                    .append(icon.inline());
        });
        sb.append("\n");
        return sb.toString();
    }

    private String getName(Service service) {
        return !isEmpty(service.getName()) ? service.getName() : service.getIdentifier();
    }

    public Optional<Icon> by(Service service) {
        return icons.stream().filter(icon -> icon.getService() == service).findFirst();
    }
}
