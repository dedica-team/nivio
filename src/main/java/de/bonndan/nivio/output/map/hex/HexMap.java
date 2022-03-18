package de.bonndan.nivio.output.map.hex;

import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Relation;
import de.bonndan.nivio.output.layout.LayoutedComponent;
import de.bonndan.nivio.output.map.hex.gojuno.HexFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import java.awt.geom.Point2D;
import java.net.URI;
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
    private final Map<URI, Set<MapTile>> groupAreas = new HashMap<>();
    private final Map<URI, HexPath> paths = new HashMap<>();

    /**
     * Add a previously layouted item to the map.
     *
     * @return the created hex
     */
    public MapTile findFreeSpot(LayoutedComponent component) {
        Hex hex = HexFactory.getInstance().hexAt(new Point2D.Double(component.getCenterX(), component.getCenterY()));
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
    public MapTile getTileForItem(@NonNull final Item item) {
        return mapState.getHexForItem(item)
                .orElseThrow(() -> new NoSuchElementException(String.format("Item %s has no hex tile assigned.", item)));
    }

    /**
     * Uses the pathfinder to create a path between start and target.
     *
     * @param rel   relation
     * @param debug debug
     */
    public void addPath(@NonNull final Relation rel, boolean debug) {
        Optional<HexPath> path = new PathFinder(this, debug).getPath(
                getTileForItem(rel.getSource()),
                getTileForItem(rel.getTarget())
        );

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
            this.paths.put(rel.getFullyQualifiedIdentifier(), hexPath);
        });
    }

    /**
     * Uses the pathfinder to create a path between start and target.
     *
     * @param rel relation to find path for
     * @return a path if one could be found
     */
    public Optional<HexPath> getPath(@NonNull final Relation rel) {
        return Optional.ofNullable(paths.get(Objects.requireNonNull(rel).getFullyQualifiedIdentifier()));
    }

    /**
     * Returns all hexes which form a group area.
     *
     * @param group the group with items
     */
    public void addGroupArea(@NonNull final Group group) {
        Set<MapTile> inArea = GroupAreaFactory.getGroup(this, group, group.getChildren());
        //set group identifier to all
        inArea.forEach(hex -> hex.setGroup(group.getFullyQualifiedIdentifier().toString()));
        groupAreas.put(group.getFullyQualifiedIdentifier(), inArea);
    }

    /**
     * Returns all hexes which form a group area.
     *
     * @param group the group with items
     * @return a set of (adjacent) hexes
     */
    public Set<MapTile> getGroupArea(@NonNull final Group group) {
        return Optional.ofNullable(groupAreas.get(group.getFullyQualifiedIdentifier()))
                .orElseThrow(() -> new NoSuchElementException(
                                String.format("Group area for group %s not found", group.getFullyQualifiedIdentifier())
                        )
                );
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
