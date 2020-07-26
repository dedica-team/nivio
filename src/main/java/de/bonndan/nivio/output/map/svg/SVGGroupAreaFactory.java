package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.LandscapeItem;
import de.bonndan.nivio.output.map.hex.Hex;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Collects all hexes close to group item hexes to create an area.
 *
 *
 */
public class SVGGroupAreaFactory {

    /**
     * Builds an areas of hex tiles belonging to a group.
     *
     * @param occupied tiles occupied by items
     * @param group the group
     * @param vertexHexes a mapping from item to its hex
     */
    public static SVGGroupArea getGroup(Set<Hex> occupied, Group group, Map<LandscapeItem, Hex> vertexHexes) {

        List<Item> items = group.getItems();
        Set<Hex> inArea = new HashSet<>();

        //surround each item
        items.forEach(item -> {
            Hex hex = vertexHexes.get(item);
            inArea.add(hex);
            hex.neighbours().forEach(neigh -> {
                if (!occupied.contains(neigh))
                    inArea.add(neigh);
            });
        });

        //find neighbours of in-area tiles which have in-area neighbours at adjacent sides

        return new SVGGroupArea(group, inArea);
    }
}
