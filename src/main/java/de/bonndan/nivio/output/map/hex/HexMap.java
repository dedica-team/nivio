package de.bonndan.nivio.output.map.hex;

import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.output.map.svg.HexPath;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.util.*;

import static de.bonndan.nivio.output.map.svg.SVGRenderer.DEFAULT_ICON_SIZE;

/**
 * Representation of a hex map.
 */
public class HexMap {

    /**
     * key is a {@link Hex}, value an {@link Item}
     */
    private final MapState mapState = new MapState();

    static Hex forCoordinates(long x, long y) {
        var q = (2. / 3 * x) / (DEFAULT_ICON_SIZE);
        var r = (-1. / 3 * x + Math.sqrt(3) / 3 * y) / (DEFAULT_ICON_SIZE);

        double s = -q - r;
        if (Math.round(q + r + s) != 0) {
            throw new IllegalArgumentException("q + r + s must be 0");
        }

        int qi = (int) Math.round(q);
        int ri = (int) Math.round(r);
        int si = (int) Math.round(s);

        var q_diff = Math.abs(qi - q);
        var r_diff = Math.abs(ri - r);
        var s_diff = Math.abs(si - s);

        if (q_diff > r_diff && q_diff > s_diff) {
            qi = -ri - si;
        } else if (r_diff > s_diff) {
            ri = -qi - si;
        }

        return new Hex(qi, ri);
    }

    /**
     * Add a previously layouted item to the map.
     *
     * @return the created hex
     */
    public MapTile findFreeSpot(long x, long y) {
        Hex hex = forCoordinates(x, y);
        if (!mapState.hasItem(hex)) {
            return mapState.getOrAdd(hex);
        }

        //trying to find a free space on the map, i.e. a tile that has no item
        for (MapTile hex1 : getNeighbours(hex)) {
            if (StringUtils.hasLength(hex1.getItem().toString()))
                continue;

            return hex1;
        }

        throw new IllegalStateException(String.format("Could not find free spot on map for coordinates %s %s", x, y));
    }


    /**
     * Add a previously layouted item to the map.
     *
     * @param item    landscape item
     * @param mapTile free spot on map
     * @return the created hex
     */
    public MapTile add(@NonNull final Item item, @NonNull final MapTile mapTile) {
        mapTile.setItem(Objects.requireNonNull(item).getFullyQualifiedIdentifier());
        return mapState.add(mapTile, item);
    }

    /**
     * Returns the hex tile of an {@link Item}
     *
     * @param item the item (must have been added before)
     * @return the corresponding {@link Hex}
     */
    public MapTile getTileForItem(Item item) {
        return mapState.getHexForItem(item).orElseThrow(() -> new NoSuchElementException(String.format("Item %s has no hex tile assigned.", item)));
    }

    /**
     * Uses the pathfinder to create a path between start and target.
     *
     * @param start  the relation source item
     * @param target the relation target item
     * @return a path if one could be found
     */
    public Optional<HexPath> getPath(Item start, Item target, boolean debug) {
        return new PathFinder(this, debug).getPath(getTileForItem(start), getTileForItem(target));
    }

    /**
     * Returns all hexes which form a group area.
     *
     * @param group the group with items
     * @return a set of (adjacent) hexes
     */
    public Set<MapTile> getGroupArea(@NonNull final Group group) {
        Set<MapTile> inArea = GroupAreaFactory.getGroup(this, group);
        //set group identifier to all
        inArea.forEach(hex -> hex.setGroup(group.getFullyQualifiedIdentifier().toString()));
        return inArea;
    }

    public List<MapTile> getNeighbours(@NonNull final Hex hex) {
        List<MapTile> neighbours = new ArrayList<>();
        for (Hex neighbour : Hex.neighbours(Objects.requireNonNull(hex))) {
            neighbours.add(mapState.getOrAdd(neighbour));
        }
        return neighbours;
    }
}
