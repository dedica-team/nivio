package de.bonndan.nivio.output.map.hex;

import de.bonndan.nivio.model.Item;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Internal state of the map.
 */
public class MapState {

    private final Map<Object, Hex> itemToHex = new HashMap<>();
    private final Map<Hex, Hex> keyMap = new HashMap<>();

    boolean contains(Hex hex) {
        return keyMap.containsKey(hex);
    }

    boolean hasItem(Hex hex) {
        if (!contains(hex)) {
            return false;
        }
        return StringUtils.hasLength(keyMap.get(hex).item);
    }

    Hex add(Hex hex, Object o) {
        Hex existing = keyMap.get(hex);
        if (existing != null) {
            //merge
            if (hex.item != null) {
                existing.item = hex.item;
            }

            if (hex.group != null) {
                existing.group = hex.group;
            }

            if (hex.getPathDirection() != null) {
                existing.setPathDirection(hex.getPathDirection());
            }
            itemToHex.put(o,hex); //may now contain more than one equal hex
            return existing;
        }

        keyMap.put(hex, hex);
        itemToHex.put(o, hex);
        return hex;
    }

    Optional<Hex> getHexForItem(Item item) {
        return Optional.ofNullable(itemToHex.get(item));
    }

    /**
     * Returns the stored equivalent for the given hex or stores it.
     *
     * @param hex hex to look up
     * @return stored hex
     */
     Hex getOrAdd(Hex hex) {
        if (!keyMap.containsKey(hex)) {
            return add(hex, UUID.randomUUID());
        }

        return keyMap.get(hex);
    }
}
