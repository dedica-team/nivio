package de.bonndan.nivio.output.map.hex;

import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.LandscapeItem;
import de.bonndan.nivio.output.map.svg.HexPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Collects all hexes close to group item hexes to create an area.
 */
public class GroupAreaFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupAreaFactory.class);

    /**
     * Builds an areas of hex tiles belonging to a group.
     * <p>
     * It works as follows: first we circumnavigate all hexes of items and add their neighbours immediately. Then we
     * iterate over all one-hex gaps and add them. This iteration is repeated, so that effectively a few two-hex gaps are
     * filled.
     * <p>
     * There is clearly much room for improvement here. It's only that I haven't found a better approach so far.
     *
     * @param occupied       tiles occupied by items
     * @param group          the group
     * @param allVertexHexes a mapping from item to its hex (all, unfiltered)
     * @return all hexes the group consists of (an area)
     */
    public static Set<Hex> getGroup(Set<Hex> occupied, Group group, Map<LandscapeItem, Hex> allVertexHexes) {

        Set<Item> items = group.getItems();
        Set<Hex> inArea = new HashSet<>();

        //surround each item
        items.forEach(item -> {
            Hex hex = allVertexHexes.get(item);
            inArea.add(hex);
            hex.neighbours().forEach(neigh -> {
                if (!occupied.contains(neigh))
                    inArea.add(neigh);
            });

            Set<Hex> closestNeighbours = getClosestItemsHexes(item, items, allVertexHexes);
            // we dont care for occupied tiles here, since we just want the closest item within group, and non-group
            // items cannot be anywhere nearby (other types of obstacles do not exist yet)
            PathFinder pathFinder = new PathFinder(Set.of());
            closestNeighbours.forEach(neighbour -> {
                HexPath path = pathFinder.getPath(hex, neighbour);
                Set<Hex> padded = new HashSet<>(); //pad to avoid thin bridges, also workaround for svh outline issue
                path.getHexes().forEach(pathTile -> {
                    padded.add(pathTile);
                    padded.addAll(pathTile.neighbours());
                });
                padded.stream().filter(hex1 -> !occupied.contains(hex1)).forEach(inArea::add);
            });

        });

        Set<Hex> bridges = getBridges(inArea);
        inArea.addAll(bridges);

        return inArea;
    }

    /**
     * Returns all neighbours in group which are the have same (minimum) distance.
     *
     * @param item           the current group item
     * @param items          all group items
     * @param allVertexHexes item hex mapping
     * @return the closest neighbours
     */
    private static Set<Hex> getClosestItemsHexes(Item item, Set<Item> items, Map<LandscapeItem, Hex> allVertexHexes) {
        Hex start = allVertexHexes.get(item);
        AtomicInteger minDist = new AtomicInteger(Integer.MAX_VALUE);
        final Set<Hex> min = new HashSet<>();
        items.stream()
                .filter(otherGroupItem -> !item.equals(otherGroupItem))
                .forEach(otherGroupItem -> {
                    Hex dest = allVertexHexes.get(otherGroupItem);
                    int distance = start.distance(dest);
                    if (distance > minDist.get()) {
                        return;
                    }
                    if (distance == minDist.get()) {
                        min.add(dest);
                        return;
                    }
                    if (distance < minDist.get()) {
                        minDist.set(distance);
                        min.clear();
                        min.add(dest);
                    }
                });

        return min;
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
