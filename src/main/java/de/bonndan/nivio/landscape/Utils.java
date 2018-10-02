package de.bonndan.nivio.landscape;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class Utils {

    public static Service pick(final String identifier, final Collection<Service> serviceList) {
        return serviceList.stream()
                .filter(service -> service.getIdentifier().equals(identifier))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Element not found: " + identifier));
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

    public static Service find(String identifier, Collection<Service> services) {
        return services.stream()
                .filter(service -> service.getIdentifier().equals(identifier))
                .findFirst()
                .orElse(null);
    }
}
