package de.bonndan.nivio.output.map.hex;

import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.LandscapeItem;
import de.bonndan.nivio.output.map.svg.HexPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Collects all hexes close to group item hexes to create an area.
 */
public class GroupAreaFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupAreaFactory.class);

    /**
     * Builds an areas of hex tiles belonging to a group.
     *
     * @param occupied      tiles occupied by items
     * @param group         the group
     * @param vertexHexes   a mapping from item to its hex
     * @param relationPaths existing paths
     * @return
     */
    public static Set<Hex> getGroup(Set<Hex> occupied, Group group, Map<LandscapeItem, Hex> vertexHexes, List<HexPath> relationPaths) {

        Set<Item> items = group.getItems();
        Set<Hex> inArea = new HashSet<>();

        //surround each item
        items.forEach(item -> {
            Hex hex = vertexHexes.get(item);
            inArea.add(hex);
            hex.neighbours().forEach(neigh -> {
                if (!occupied.contains(neigh))
                    inArea.add(neigh);
            });

            //add all "inner" relations (paths)
            relationPaths.stream()
                    .filter(rel -> rel.getSource().equals(item))
                    .filter(rel -> rel.getTarget().getGroup() != null)
                    .filter(rel -> rel.getTarget().getGroup().equals(group.getIdentifier()))
                    .forEach(rel -> inArea.addAll(rel.getHexes()));
        });

        Set<Hex> bridges = getBridges(inArea);
        inArea.addAll(bridges);

        //2nd pass fills gaps
        bridges = getBridges(inArea);
        inArea.addAll(bridges);

        return inArea;
    }

    /**
     * Finds neighbours of in-area tiles which have in-area neighbours at adjacent sides (gaps).
     *
     * @param inArea all hexes in area
     * @return all hexes which fill gaps
     */
    static Set<Hex> getBridges(Set<Hex> inArea) {

        Set<Hex> bridges = new HashSet<>();
        inArea.forEach(hex -> {
            hex.neighbours().forEach(neighbour -> {
                if (inArea.contains(neighbour))
                    return;

                int i = 0;
                List<Integer> sides = new ArrayList<>();
                for (Hex nn : neighbour.neighbours()) {
                    if (sides.size() > 2)
                        break;

                    //check on in-area tiles
                    if (inArea.contains(nn)) {
                        sides.add(i);
                    }
                    i++;
                }

                if (sides.size() < 2)
                    return;
                if (sides.size() > 2) {
                    bridges.add(neighbour);
                    return;
                }

                //find out if the two neighbours are adjacent
                int diff = sides.get(0) - sides.get(1);

                //-1 if any two are adjacent
                //-5 if first (0) and last (5) are adjacent
                if (diff != -1 && diff != -5) {
                    bridges.add(neighbour);
                    LOGGER.debug("Adding bridge tile {}", neighbour);
                }

            });
        });
        return bridges;
    }
}
