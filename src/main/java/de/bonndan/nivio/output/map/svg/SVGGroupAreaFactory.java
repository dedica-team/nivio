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

    public static SVGGroupArea getGroup(Set<Hex> occupied, Group group, Map<LandscapeItem, Hex> vertexHexes) {

        List<Item> items = group.getItems();
        Set<Hex> inArea = new HashSet<>();
        items.forEach(item -> {
            Hex hex = vertexHexes.get(item);
            inArea.add(hex);
            hex.neighbours().forEach(neigh -> {
                if (!occupied.contains(neigh))
                    inArea.add(neigh);
            });

        });

        return new SVGGroupArea(group, inArea);
    }
}
