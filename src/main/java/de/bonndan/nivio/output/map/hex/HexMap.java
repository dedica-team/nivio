package de.bonndan.nivio.output.map.hex;

import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.LandscapeItem;
import de.bonndan.nivio.output.layout.LayoutedComponent;
import de.bonndan.nivio.output.map.svg.HexPath;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Representation of a hex map.
 */
public class HexMap {

    private final Set<Hex> occupied = new HashSet<>();
    private final Map<Item, Hex> vertexHexes = new HashMap<>();
    private final PathFinder pathFinder;

    public HexMap(boolean debug) {
        // find and render relations
        pathFinder = new PathFinder(occupied);
        pathFinder.debug = debug;
    }

    /**
     * Add a previously layouted item to the map.
     *
     * @param layoutedItem landscape item plus coordinates
     */
    public void add(LayoutedComponent layoutedItem) {
        Hex hex = null;
        int i = 0;
        while (hex == null || occupied.contains(hex)) {
            hex = Hex.of(Math.round(layoutedItem.getX()) - i, Math.round(layoutedItem.getY()) - i);
            i++;
        }

        Item item = (Item) layoutedItem.getComponent();
        hex.id = item.getFullyQualifiedIdentifier().jsonValue();
        vertexHexes.put(item, hex);
        occupied.add(hex);
    }

    /**
     * Returns the hex tile of an {@link Item}
     *
     * @param item the item (must have been added before)
     * @return the corresponding {@link Hex}
     */
    public Hex hexForItem(Item item) {
        return vertexHexes.get(item);
    }

    /**
     * Uses the pathfinder to create a path between start and target.
     *
     * @param start  the relation source item
     * @param target the relation target item
     * @return a path if one could be found
     */
    public Optional<HexPath> getPath(Item start, Item target) {
        return pathFinder.getPath(hexForItem(start), hexForItem(target));
    }

    /**
     * Returns all hexes which form a group area.
     *
     * @param group the group with items
     * @return a set of (adjacent) hexes
     */
    public Set<Hex> getGroupArea(Group group) {
        return GroupAreaFactory.getGroup(occupied, group, vertexHexes);
    }
}
