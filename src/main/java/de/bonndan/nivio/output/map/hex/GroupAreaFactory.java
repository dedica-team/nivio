package de.bonndan.nivio.output.map.hex;

import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.output.map.svg.HexPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

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
    public static Set<Hex> getGroup(Set<Hex> occupied, Group group, Map<Item, Hex> allVertexHexes) {

        Set<Item> items = group.getItems();
        Set<Hex> inArea = new HashSet<>();
        List<Item> connected = new ArrayList<>();

        if (!items.iterator().hasNext()) {
            LOGGER.warn("Could not determine group area for group {}", group);
            return inArea;
        }
        //surround each item
        Item next = items.iterator().next();
        while (next != null) {

            LOGGER.debug("adding {} to group area", next);
            Hex hex = allVertexHexes.get(next);
            inArea.add(hex);
            hex.neighbours().forEach(neigh -> {
                if (!occupied.contains(neigh))
                    inArea.add(neigh);
            });

            Optional<Item> closest = getClosestItem(next, items, allVertexHexes, connected);
            if (closest.isEmpty()) {
                LOGGER.debug("no closest item found for {}", next);
                break;
            }

            // we dont care for occupied tiles here, since we just want the closest item within group, and non-group
            // items cannot be anywhere nearby (other types of obstacles do not exist yet)
            PathFinder pathFinder = new PathFinder(Set.of());

            Hex destination = allVertexHexes.get(closest.get());
            Optional<HexPath> path = pathFinder.getPath(hex, destination);
            if (path.isPresent()) {
                Set<Hex> padded = new HashSet<>(); //pad to avoid thin bridges, also workaround for svg outline issue
                path.get().getHexes().forEach(pathTile -> {
                    padded.add(pathTile);
                    padded.addAll(pathTile.neighbours());
                });
                padded.stream().filter(hex1 -> !occupied.contains(hex1)).forEach(inArea::add);
            }

            connected.add(next);
            // stop if the next one has been connected already
            next = connected.contains(closest.get()) ? null : closest.get();
        }

        // adding hexes with many sides adjacent to group area until no more can be added
        Set<Hex> bridges = getBridges(inArea, 2);
        while (!bridges.isEmpty()) {
            inArea.addAll(bridges);
            bridges = getBridges(inArea, 3); // 2 might be too aggressive and collide with other group areas
        }

        //set group identifier to all untyped
        inArea.forEach(hex -> hex.group = group.getFullyQualifiedIdentifier().toString());
        return inArea;
    }

    /**
     * Returns all neighbours in group which are the have same (minimum) distance.
     *
     * @param item           the current group item
     * @param items          all group items
     * @param allVertexHexes item hex mapping
     * @param connected
     * @return the closest neighbours
     */
    private static Optional<Item> getClosestItem(Item item, Set<Item> items, Map<Item, Hex> allVertexHexes, List<Item> connected) {
        Hex start = allVertexHexes.get(item);
        AtomicInteger minDist = new AtomicInteger(Integer.MAX_VALUE);
        AtomicReference<Item> min = new AtomicReference<>(null);
        items.stream()
                .filter(otherGroupItem -> !item.equals(otherGroupItem))
                .filter(otherGroupItem -> !connected.contains(otherGroupItem))
                .forEach(otherGroupItem -> {
                    Hex dest = allVertexHexes.get(otherGroupItem);
                    int distance = start.distance(dest);
                    if (distance > minDist.get()) {
                        return;
                    }
                    if (distance == minDist.get()) {
                        return;
                    }
                    if (distance < minDist.get()) {
                        minDist.set(distance);
                        min.set(otherGroupItem);
                    }
                });

        return Optional.ofNullable(min.get());
    }

    /**
     * Finds neighbours of in-area tiles which have in-area neighbours at adjacent sides (gaps).
     *
     * @param inArea   all hexes in area
     * @param minSides min number of sides having in-group neighbours to be added as "bridge"
     * @return all hexes which fill gaps
     */
    static Set<Hex> getBridges(Set<Hex> inArea, int minSides) {

        Set<Hex> bridges = new HashSet<>();
        inArea.forEach(hex -> {
            hex.neighbours().forEach(neighbour -> {
                if (inArea.contains(neighbour))
                    return;

                int i = 0;
                List<Integer> sides = new ArrayList<>();
                for (Hex nn : neighbour.neighbours()) {
                    if (sides.size() > minSides)
                        break;

                    //check on in-area tiles
                    if (inArea.contains(nn)) {
                        sides.add(i);
                    }
                    i++;
                }

                if (sides.size() < 2)
                    return;
                if (sides.size() > minSides) {
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
