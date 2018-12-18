package de.bonndan.nivio.landscape;

import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Utils {

    public static LandscapeItem pick(final LandscapeItem item, final List<? extends LandscapeItem> services) {
        return pick(item.getIdentifier(), item.getGroup(), services);
    }

    /**
     * Makes sure the services is returned or throws an exception.
     *
     * @param identifier  service identifier
     * @param group       the group to search in
     * @param serviceList all services
     * @return the service with the given identifier
     */
    public static LandscapeItem pick(final String identifier, String group, final List<? extends LandscapeItem> serviceList) {
        if (StringUtils.isEmpty(identifier)) {
            throw new IllegalArgumentException("Identifier is empty");
        }

        LandscapeItem landscapeItem = find(identifier, group, serviceList);
        if (landscapeItem == null)
            throw new RuntimeException("Element not found " + identifier + " in collection " + serviceList);

        return landscapeItem;
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
    public static LandscapeItem find(String identifier, String group, List<? extends LandscapeItem> services) {
        if (StringUtils.isEmpty(identifier)) {
            throw new IllegalArgumentException("Identifier is empty");
        }

        List<LandscapeItem> found = findAll(identifier, group, services);

        if (found.size() == 1)
            return found.get(0);
        if (found.size() > 1)
            throw new RuntimeException("Ambiguous result for " + group + "/" + identifier + ": " + found + " in collection " + services);

        return null;
    }

    private static List<LandscapeItem> findAll(
            final String identifier,
            final String group,
            final List<? extends LandscapeItem> serviceList
    ) {
        FullyQualifiedIdentifier fqi = FullyQualifiedIdentifier.build(null, group, identifier);
        return findAll(fqi, serviceList);
    }

    private static List<LandscapeItem> findAll(FullyQualifiedIdentifier fqi, List<? extends LandscapeItem> serviceList) {
        return serviceList.stream()
                .filter(fqi::equalsIgnoringLandscape)
                .collect(Collectors.toList());
    }

    public static LandscapeItem find(FullyQualifiedIdentifier fqi, List<? extends LandscapeItem> services) {
        List<LandscapeItem> found = findAll(fqi, services);

        if (found.size() == 1)
            return found.get(0);
        if (found.size() > 1)
            throw new RuntimeException("Ambiguous result for " + fqi + ": " + found + " in collection " + services);

        return null;
    }
}
