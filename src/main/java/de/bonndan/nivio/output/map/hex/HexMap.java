package de.bonndan.nivio.output.map.hex;

import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.output.map.svg.HexPath;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
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
    private final BidiMap<Hex, Object> hexesToItems = new DualHashBidiMap<>();
    private final PathFinder pathFinder;

    public HexMap(boolean debug) {

        // find and render relations
        pathFinder = new PathFinder(this);
        pathFinder.debug = debug;
    }

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
    public Hex findFreeSpot(long x, long y) {
        Hex hex = forCoordinates(x, y);
        if (!hasItem(hex)) {
            return hex;
        }

        //trying to find a free space on the map, i.e. a tile that has no item
        for (Hex hex1 : hex.neighbours()) {
            if (hasItem(hex1))
                continue;

            return hex1;
        }

        throw new IllegalStateException(String.format("Could not find free spot on map for coordinates %s %s", x, y));
    }

    private boolean hasItem(Hex hex) {
        if (!hexesToItems.containsKey(hex)) {
            return false;
        }
        return StringUtils.hasLength(hexesToItems.getKey(hex).item);
    }

    /**
     * Add a previously layouted item to the map.
     *
     * @param item landscape item
     * @param hex  free spot on map
     * @return the created hex
     */
    public Hex add(@NonNull final Item item, @NonNull final Hex hex) {
        hex.item = Objects.requireNonNull(item).getFullyQualifiedIdentifier().toString();
        hexesToItems.put(hex, item);
        return hex;
    }

    /**
     * Returns the hex tile of an {@link Item}
     *
     * @param item the item (must have been added before)
     * @return the corresponding {@link Hex}
     */
    public Hex hexForItem(Item item) {
        return Objects.requireNonNull(hexesToItems.getKey(item), String.format("Item %s has no hex tile assigned.", item));
    }

    /**
     * Uses the pathfinder to create a path between start and target.
     *
     * @param start  the relation source item
     * @param target the relation target item
     * @return a path if one could be found
     */
    public Optional<HexPath> getPath(Item start, Item target) {
        Optional<HexPath> path = pathFinder.getPath(hexForItem(start), hexForItem(target));
        path.ifPresent(hexPath -> hexPath.getHexes().forEach(hex -> hexesToItems.computeIfAbsent(hex, hex1 -> hex1)));
        return path;
    }

    /**
     * Returns all hexes which form a group area.
     *
     * @param group the group with items
     * @return a set of (adjacent) hexes
     */
    public Set<Hex> getGroupArea(@NonNull final Group group) {
        Set<Hex> inArea = GroupAreaFactory.getGroup(hexesToItems.inverseBidiMap(), group);
        inArea.forEach(hex -> hexesToItems.putIfAbsent(hex, UUID.randomUUID()));
        //set group identifier to all
        inArea.forEach(hex -> hex.group = group.getFullyQualifiedIdentifier().toString());
        return inArea;
    }

    /**
     * Returns the hex that is used in the map.
     *
     * @param hex any hex with coordinates
     * @return hex on the map
     */
    @NonNull
    public Hex getFromMap(@NonNull final Hex hex) {
        if (hexesToItems.containsKey(Objects.requireNonNull(hex))) {
            Object val = hexesToItems.get(hex);
            return hexesToItems.inverseBidiMap().get(val);
        }

        hexesToItems.put(hex, UUID.randomUUID());
        return hex;
    }
}
