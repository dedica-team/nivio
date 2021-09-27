package de.bonndan.nivio.output.map.hex;

import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import org.springframework.lang.NonNull;

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

        var qDiff = Math.abs(qi - q);
        var rDiff = Math.abs(ri - r);
        var sDiff = Math.abs(si - s);

        if (qDiff > rDiff && qDiff > sDiff) {
            qi = -ri - si;
        } else if (rDiff > sDiff) {
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
        Optional<HexPath> path = new PathFinder(this, debug).getPath(getTileForItem(start), getTileForItem(target));
        path.ifPresent(hexPath -> {
            List<PathTile> tiles = hexPath.getTiles();
            for (int i = 0, tilesSize = tiles.size(); i < tilesSize; i++) {
                PathTile tile = tiles.get(i);
                if (tile.getDirectionFromParent() != null) {
                    tile.getMapTile().addPathDirection(tile.getDirectionFromParent());
                }
                if (i == tilesSize -2) {
                    int portCount = tile.getMapTile().incrementPortCount();
                    hexPath.setPortCount(portCount);
                }
            }
        });
        return path;
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

    /**
     * Returns the adjacent maptiles of a map location.
     *
     * @param hex locatopm
     * @return list or neighbours, starting with direction "southeast"
     */
    public List<MapTile> getNeighbours(@NonNull final Hex hex) {
        List<MapTile> neighbours = new ArrayList<>();
        for (Hex neighbour : Hex.neighbours(Objects.requireNonNull(hex))) {
            neighbours.add(mapState.getOrAdd(neighbour));
        }
        return neighbours;
    }
}
