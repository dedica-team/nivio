package de.bonndan.nivio.output.map.hex;

import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.LandscapeItem;
import de.bonndan.nivio.output.layout.LayoutedComponent;
import de.bonndan.nivio.output.map.svg.HexPath;
import org.springframework.lang.Nullable;

import java.util.*;

/**
 * Representation of a hex map.
 *
 *
 *
 */
public class HexMap {

    private final Set<Hex> occupied = new HashSet<>();
    private final boolean debug;
    private final Map<LandscapeItem, Hex> vertexHexes = new HashMap<>();
    private final PathFinder pathFinder;
    private final List<HexPath> paths = new ArrayList<>();

    public HexMap(boolean debug) {
        this.debug = debug;
        // find and render relations
        pathFinder = new PathFinder(occupied);
        pathFinder.debug = this.debug;
    }

    public void add(LayoutedComponent layoutedItem) {
        Hex hex = null;
        int i = 0;
        while (hex == null || occupied.contains(hex)) {
            hex = Hex.of(Math.round(layoutedItem.getX()) - i, Math.round(layoutedItem.getY()) - i);
            i++;
        }

        Item item = (Item) layoutedItem.getComponent();
        hex.id = item.getFullyQualifiedIdentifier().jsonValue();
        hex.item = item;
        vertexHexes.put(item, hex); //this is obsolete
        occupied.add(hex);
    }

    public Hex hexForItem(Item item) {
        return vertexHexes.get(item);
    }

    @Nullable
    public HexPath getPath(Item item, Item target) {
        HexPath path = pathFinder.getPath(hexForItem(item), hexForItem(target));
        paths.add(path);
        return path;
    }

    /**
     * Returns all hexes which form a group area.
     *
     * @param group the group with items
     * @return a set of (adjacent) hexes
     */
    public Set<Hex> getGroupArea(Group group) {
        return GroupAreaFactory.getGroup(occupied, group, vertexHexes, paths);
    }
}
