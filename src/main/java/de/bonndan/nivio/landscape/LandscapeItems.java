package de.bonndan.nivio.landscape;


import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LandscapeItems {

    /**
     * Returns all elements kept in the second list.
     */
    public static List<LandscapeItem> kept(List<? extends LandscapeItem> items1, List<? extends LandscapeItem> items2) {
        return items2.stream().filter(item -> exists(item, items1)).collect(Collectors.toList());
    }

    /**
     * Returns all elements removed from the second list.
     */
    public static List<LandscapeItem> removed(List<? extends LandscapeItem> items1, List<? extends LandscapeItem> items2) {
        return items2.stream().filter(item -> !exists(item, items1)).collect(Collectors.toList());
    }

    /**
     * Returns all elements which are not in the second list
     */
    public static List<LandscapeItem> added(List<? extends LandscapeItem> items1, List<? extends LandscapeItem> existing) {
        return items1.stream()
                .filter(item -> !exists(item, existing))
                .collect(Collectors.toList());
    }

    private static boolean exists(LandscapeItem item, List<? extends LandscapeItem> items) {
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
    public static LandscapeItem pick(final LandscapeItem item, final List<? extends LandscapeItem> items) {
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
     * Returns a the item from the list or null. Uses the matching criteria of {@link FullyQualifiedIdentifier}
     *
     * @param identifier the service identifier
     * @param items   all services
     * @return the service or null
     */
    public static LandscapeItem find(String identifier, String group, List<? extends LandscapeItem> items) {
        if (StringUtils.isEmpty(identifier)) {
            throw new IllegalArgumentException("Identifier is empty");
        }

        List<LandscapeItem> found = findAll(identifier, group, items);

        if (found.size() == 1)
            return found.get(0);
        if (found.size() > 1)
            throw new RuntimeException("Ambiguous result for " + group + "/" + identifier + ": " + found + " in collection " + items);

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
    public static LandscapeItem find(FullyQualifiedIdentifier fqi, List<? extends LandscapeItem> services) {
        List<LandscapeItem> found = findAll(fqi, services);

        if (found.size() == 1)
            return found.get(0);
        if (found.size() > 1)
            throw new RuntimeException("Ambiguous result for " + fqi + ": " + found + " in collection " + services);

        return null;
    }

    public static Groups getGroups(Landscape landscape) {
        Groups groups = new Groups();
        landscape.getServices().forEach(groups::add);
        return groups;
    }
}
