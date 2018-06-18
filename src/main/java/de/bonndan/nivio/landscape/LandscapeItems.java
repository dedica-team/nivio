package de.bonndan.nivio.landscape;

import de.bonndan.nivio.landscape.LandscapeItem;

import java.util.List;
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
    public static List<LandscapeItem> added(List<? extends LandscapeItem> items1, List<? extends LandscapeItem> items2) {
        return items1.stream().filter(item -> !exists(item, items2)).collect(Collectors.toList());
    }

    private static boolean exists(LandscapeItem item, List<? extends LandscapeItem> items) {
        return items.stream().anyMatch(o -> o.getIdentifier().equals(item.getIdentifier()));
    }
}
