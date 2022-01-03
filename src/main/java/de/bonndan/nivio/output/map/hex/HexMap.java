package de.bonndan.nivio.output.map.hex;

import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.output.layout.LayoutedComponent;
import de.bonndan.nivio.output.map.hex.gojuno.HexFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import java.awt.geom.Point2D;
import java.util.*;

/**
 * Representation of a hex map.
 */
public class HexMap {

    private static final Logger LOGGER = LoggerFactory.getLogger(HexMap.class);

    /**
     * key is a {@link Hex}, value an {@link Item}
     */
    private final MapState mapState = new MapState();

    /**
     * Add a previously layouted item to the map.
     *
     * @return the created hex
     */
    public MapTile findFreeSpot(LayoutedComponent component) {
        Hex hex = HexFactory.getInstance().hexAt(new Point2D.Double(component.getX(), component.getY()));
        MapTile tile = mapState.getOrAdd(hex);
        if (!mapState.hasItem(hex)) {
            return tile;
        }

        var msg = String.format("Could not find free spot on map for component %s at %s, already blocked by %s at %s",
                component, hex.toPixel(),
                tile.getItem(), tile.getHex().toPixel());
        LOGGER.error(msg);

        for (MapTile mapTile : getNeighbours(tile.getHex())) {
            if (!mapState.hasItem(mapTile.getHex())) {
                return mapTile;
            }
        }

        throw new IllegalStateException(msg);
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
                if (i == tilesSize - 2) {
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
     * @param items
     * @return a set of (adjacent) hexes
     */
    public Set<MapTile> getGroupArea(@NonNull final Group group, Set<Item> items) {
        Set<MapTile> inArea = GroupAreaFactory.getGroup(this, group, items);
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
