package de.bonndan.nivio.landscape;

import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class Utils {

    /**
     * Makes sure the services is returned or throws an exception.
     *
     * @param identifier service identifier
     * @param serviceList all services
     * @return the service with the given identifier
     */
    public static Service pick(final String identifier, final Collection<Service> serviceList) {
        if (StringUtils.isEmpty(identifier)) {
            throw new IllegalArgumentException("Identifier is empty");
        }
        return serviceList.stream()
                .filter(service -> service.getIdentifier().equals(identifier))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Element not found: " + identifier + " in collection " + serviceList));
    }

    /**
     * TODO remove, this is a workaround for https://hibernate.atlassian.net/browse/HHH-3799
     *
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
     * @param services all services
     * @return the service or null
     */
    public static Service find(String identifier, Collection<Service> services) {
        return services.stream()
                .filter(service -> service.getIdentifier().equals(identifier))
                .findFirst()
                .orElse(null);
    }
}
