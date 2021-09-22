package de.bonndan.nivio.output.map.hex;

import de.bonndan.nivio.model.Item;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Internal state of the map.
 */
class MapState {

    private final Map<Object, MapTile> itemToTile = new ConcurrentHashMap<>();
    private final Map<Hex, MapTile> keyMap = new ConcurrentHashMap<>();

    boolean contains(Hex hex) {
        return keyMap.containsKey(hex);
    }

    boolean hasItem(Hex hex) {
        if (!contains(hex)) {
            return false;
        }
        return keyMap.get(hex).getItem() != null;
    }

    MapTile add(MapTile mapTile, Object o) {
        MapTile existing = keyMap.get(mapTile.getHex());
        if (existing != null) {
            //merge
            if (mapTile.getItem() != null) {
                existing.setItem(mapTile.getItem());
            }

            if (mapTile.getGroup() != null) {
                existing.setGroup(mapTile.getGroup());
            }

            if (mapTile.getPathDirection() != null) {
                existing.setPathDirection(mapTile.getPathDirection());
            }
            itemToTile.put(o, mapTile); //may now contain more than one equal hex
            return existing;
        }

        keyMap.put(mapTile.getHex(), mapTile);
        itemToTile.put(o, mapTile);
        return mapTile;
    }

    Optional<MapTile> getHexForItem(Item item) {
        return Optional.ofNullable(itemToTile.get(item));
    }

    /**
     * Returns the stored equivalent for the given hex or stores it.
     *
     * @param hex hex to look up
     * @return stored hex
     */
    MapTile getOrAdd(Hex hex) {
        if (!keyMap.containsKey(hex)) {
            return add(new MapTile(hex), UUID.randomUUID());
        }

        return keyMap.get(hex);
    }
}
