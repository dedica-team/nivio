package de.bonndan.nivio.landscape;


import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ServiceItems {

    /**
     * Returns all elements kept in the second list.
     */
    public static List<ServiceItem> kept(Collection<? extends ServiceItem> items1, Collection<? extends ServiceItem> items2) {
        return items2.stream().filter(item -> exists(item, items1)).collect(Collectors.toList());
    }

    /**
     * Returns all elements removed from the second list.
     */
    public static List<ServiceItem> removed(Collection<? extends ServiceItem> items1, Collection<? extends ServiceItem> items2) {
        return items2.stream().filter(item -> !exists(item, items1)).collect(Collectors.toList());
    }

    /**
     * Returns all elements which are not in the second list
     */
    public static List<ServiceItem> added(Collection<? extends ServiceItem> items1, Collection<? extends ServiceItem> existing) {
        return items1.stream()
                .filter(item -> !exists(item, existing))
                .collect(Collectors.toList());
    }

    private static boolean exists(ServiceItem item, Collection<? extends ServiceItem> items) {
        return items.stream().anyMatch(
                inList -> item.getFullyQualifiedIdentifier().isSimilarTo(inList)
        );
    }

    /**
     * Ensures that the given item has a sibling in the list, returns the item from the list.
     *
     * @param item item to search for
     * @param items list of landscape items
     * @return the sibling from the list
     */
    public static ServiceItem pick(final ServiceItem item, final Collection<? extends ServiceItem> items) {
        return pick(item.getIdentifier(), item.getGroup(), items);
    }

    /**
     * Makes sure the sibling of the item is returned or throws an exception.
     *
     * @param identifier  service identifier
     * @param group       the group to search in
     * @param serviceList all services
     * @return the sibling with the given identifier
     */
    public static ServiceItem pick(final String identifier, String group, final Collection<? extends ServiceItem> serviceList) {
        if (StringUtils.isEmpty(identifier)) {
            throw new IllegalArgumentException("Identifier is empty");
        }

        return find(identifier, group, serviceList).orElseThrow(() ->
                new RuntimeException("Element not found " + identifier + " in collection " + serviceList)
        );
    }

    /**
     * Returns a the item from the list or null. Uses the matching criteria of {@link FullyQualifiedIdentifier}
     *
     * @param identifier the service identifier
     * @param items   all services
     * @return the service or null
     */
    public static Optional<ServiceItem> find(String identifier, String group, Collection<? extends ServiceItem> items) {
        if (StringUtils.isEmpty(identifier)) {
            throw new IllegalArgumentException("Identifier is empty");
        }

        List<ServiceItem> found = findAll(identifier, group, items);

        if (found.size() > 1)
            throw new RuntimeException("Ambiguous result for " + group + "/" + identifier + ": " + found + " in collection " + items);

        return Optional.ofNullable((found.size() == 1) ? found.get(0): null);
    }

    private static List<ServiceItem> findAll(
            final String identifier,
            final String group,
            final Collection<? extends ServiceItem> serviceList
    ) {
        FullyQualifiedIdentifier fqi = FullyQualifiedIdentifier.build(null, group, identifier);
        return findAll(fqi, serviceList);
    }

    private static List<ServiceItem> findAll(FullyQualifiedIdentifier fqi, Collection<? extends ServiceItem> serviceList) {
        return serviceList.stream()
                .filter(fqi::isSimilarTo)
                .collect(Collectors.toList());
    }

    /**
     * Returns a the item from the list or null. Uses the matching criteria of {@link FullyQualifiedIdentifier}
     *
     * @param fqi the service identifier
     * @param services   all services
     * @return the service or null
     */
    public static Optional<ServiceItem> find(FullyQualifiedIdentifier fqi, Collection<? extends ServiceItem> services) {
        List<ServiceItem> found = findAll(fqi, services);

        if (found.size() > 1)
            throw new RuntimeException("Ambiguous result for " + fqi + ": " + found + " in collection " + services);

        return Optional.ofNullable((found.size() == 1) ? found.get(0): null);
    }
}
