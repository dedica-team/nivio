package de.bonndan.nivio.landscape;

import de.bonndan.nivio.input.dto.ServiceDescription;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Utils {

    public static Service pick(final LandscapeItem description, final Collection<Service> services) {
        return pick(description.getIdentifier(), description.getGroup(), services);
    }

    /**
     * Makes sure the services is returned or throws an exception.
     *
     * @param identifier  service identifier
     * @param group       the group to search in
     * @param serviceList all services
     * @return the service with the given identifier
     */
    public static Service pick(final String identifier, String group, final Collection<Service> serviceList) {
        if (StringUtils.isEmpty(identifier)) {
            throw new IllegalArgumentException("Identifier is empty");
        }
        if (StringUtils.isEmpty(group)) {

            List<Service> found = serviceList.stream()
                    .filter(service -> service.getIdentifier().equals(identifier))
                    .collect(Collectors.toList());
            if (found.size() == 1)
                return found.get(0);
            if (found.size() > 1)
                throw new RuntimeException("Ambiguous result for " + identifier + ": " + found + " in collection " + serviceList);

            throw new RuntimeException("Element not found " + identifier + " in collection " + serviceList);
        }

        return serviceList.stream()
                .filter(service -> service.getIdentifier().equals(identifier) && service.getGroup().equals(group))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Element not found: " + identifier + "/" + group + " in collection " + serviceList));
    }

    /**
     * TODO remove, this is a workaround for https://hibernate.atlassian.net/browse/HHH-3799
     */
    public static boolean contains(Service provider, Set<Service> providedBy) {
        for (Service s : providedBy) {
            if (s.equals(provider))
                return true;
        }
        return false;
    }

    /**
     * Returns a service or null.
     *
     * @param identifier the service identifier
     * @param services   all services
     * @return the service or null
     */
    public static Service find(String identifier, String group, Collection<Service> services) {
        return services.stream()
                .filter(service -> service.getIdentifier().equals(identifier))
                .findFirst()
                .orElse(null);
    }
}
