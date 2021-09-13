package de.bonndan.nivio.output.map.hex;

import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.output.map.svg.HexPath;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
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
     * @param itemsToHexes tiles occupied by items
     * @param group        the group
     * @return all hexes the group consists of (an area)
     */
    public static Set<Hex> getGroup(Map<Object, Hex> itemsToHexes, Group group) {

        Set<Item> items = group.getItems();
        Set<Hex> inArea = new HashSet<>();

        if (!items.iterator().hasNext()) {
            LOGGER.warn("Could not determine group area for group {} because of missing items.", group);
            return inArea;
        }

        //simple occupations per item
        addItemsAndNeighbours(itemsToHexes, items, inArea);

        //build the area by adding paths
        addPathsBetweenClosestItems(itemsToHexes, items, inArea);

        // enlarge area by adding hexes with many sides adjacent to group area until no more can be added
        Set<Hex> bridges = getBridges(inArea, 2);
        while (!bridges.isEmpty()) {
            inArea.addAll(bridges);
            bridges = getBridges(inArea, 3); // 2 might be too aggressive and collide with other group areas
        }

        //set group identifier to all
        inArea.forEach(hex -> hex.group = group.getFullyQualifiedIdentifier().toString());
        return inArea;
    }

    /**
     * Every item itself and its neighbours are added
     */
    private static void addItemsAndNeighbours(Map<Object, Hex> itemsToHexes,
                                              Set<Item> items,
                                              Set<Hex> inArea
    ) {
        items.forEach(next -> {
            LOGGER.debug("adding {} to group area", next);
            Hex hex = itemsToHexes.get(next);
            inArea.add(hex);
            inArea.addAll(hex.neighbours());
        });
    }

    /**
     * Generates paths between each item and its closest neighbour and added tiles of the paths to the group area.
     *
     * @param itemsToHexes hex tiles occupied by items
     * @param items        group items
     * @param inArea       area hex tiles
     */
    private static void addPathsBetweenClosestItems(Map<Object, Hex> itemsToHexes,
                                                    Set<Item> items,
                                                    Set<Hex> inArea
    ) {
        List<Item> connected = new ArrayList<>();
        Item next = items.iterator().next();
        while (next != null) {

            LOGGER.debug("adding {} to group area", next);
            Hex hex = itemsToHexes.get(next);

            Optional<Item> closest = getClosestItem(next, items, itemsToHexes, connected);
            if (closest.isEmpty()) {
                LOGGER.debug("no closest item found for {}", next);
                break;
            }

            // we dont care for occupied tiles here, since we just want the closest item within group, and non-group
            // items cannot be anywhere nearby (other types of obstacles do not exist yet)
            PathFinder pathFinder = new PathFinder(new DualHashBidiMap<>());

            Hex destination = itemsToHexes.get(closest.get());
            Optional<HexPath> path = pathFinder.getPath(hex, destination);
            if (path.isPresent()) {
                Set<Hex> padded = new HashSet<>(); //pad to avoid thin bridges, also workaround for svg outline issue
                path.get().getHexes().forEach(pathTile -> {
                    padded.add(pathTile);
                    padded.addAll(pathTile.neighbours());
                });
                padded.stream().filter(hex1 -> !itemsToHexes.containsKey(hex1)).forEach(inArea::add);
            }

            connected.add(next);
            // stop if the next one has been connected already
            next = connected.contains(closest.get()) ? null : closest.get();
        }
    }

    /**
     * Returns the closest item of the group items.
     * <p>
     * Also regards that connections will not be reversed (a->b will prevent b->a).
     *
     * @param item           the current group item
     * @param items          all group items
     * @param allVertexHexes item hex mapping
     * @param connected      the items which have been connected previously
     * @return the closest neighbours
     */
    private static Optional<Item> getClosestItem(Item item,
                                                 Set<Item> items,
                                                 Map<Object, Hex> allVertexHexes,
                                                 List<Item> connected
    ) {
        Hex start = allVertexHexes.get(item);
        AtomicInteger minDist = new AtomicInteger(Integer.MAX_VALUE);
        AtomicReference<Item> min = new AtomicReference<>(null);
        for (Item otherGroupItem : items) {
            if (item.equals(otherGroupItem) || connected.contains(otherGroupItem)) {
                continue;
            }
            Hex dest = allVertexHexes.get(otherGroupItem);
            int distance = start.distance(dest);
            if (distance < minDist.get()) {
                minDist.set(distance);
                min.set(otherGroupItem);
            }
        }

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
                List<Integer> sidesWithNeighbours = new ArrayList<>();
                for (Hex nn : neighbour.neighbours()) {
                    if (sidesWithNeighbours.size() > minSides)
                        break;

                    //check on in-area tiles
                    if (inArea.contains(nn)) {
                        sidesWithNeighbours.add(i);
                    }
                    i++;
                }

                if (sidesWithNeighbours.size() < 2)
                    return;
                if (sidesWithNeighbours.size() > minSides) {
                    bridges.add(neighbour);
                    return;
                }

                if (hasOppositeNeighbours(sidesWithNeighbours)) {
                    bridges.add(neighbour);
                }
            });
        });
        return bridges;
    }

    /**
     * Find out if any sides having a neighbour are not adjacent.
     *
     * @param sidesWithNeighbours numbers of sides having a same-group neighbour (0..5)
     */
    private static boolean hasOppositeNeighbours(List<Integer> sidesWithNeighbours) {

        for (int i = 0; i < sidesWithNeighbours.size(); i++) {
            Integer integer = sidesWithNeighbours.get(i);
            Integer next = sidesWithNeighbours.get(i == sidesWithNeighbours.size() - 1 ? 0 : i + 1);
            int diff = Math.abs(integer - next);
            if (diff != 1 && diff != 5) {
                return true;
            }
        }
        return false;
    }
}
