package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.model.Component;
import de.bonndan.nivio.model.Group;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class SortedGroups implements java.util.Comparator<Component> {

    public static Map<URI, Group> sort(Set<Group> all) {
        Map<URI, Group> sortedGroups = new LinkedHashMap<>();
        all.stream()
                .sorted(new SortedGroups())
                .forEach(group -> sortedGroups.put(group.getFullyQualifiedIdentifier(), group));
        return sortedGroups;
    }

    @Override
    public int compare(Component o1, Component o2) {
        return o1.getIdentifier().compareToIgnoreCase(o2.getIdentifier());
    }
}
