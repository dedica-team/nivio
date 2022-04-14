package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.output.layout.LayoutedComponent;
import de.bonndan.nivio.output.map.hex.HexMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

public class HexMapDataProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(HexMapDataProvider.class);

    private HexMapDataProvider() {
    }

    /**
     * Invokes placement of items, relations (paths) and groups areas on the map.
     *
     * @param hexMap    the map to use
     * @param landscape landscape with data
     */
    public static void fillMap(@NonNull final HexMap hexMap, @NonNull final LayoutedComponent landscape) {

        //add items firsat
        landscape.getChildren().forEach(group -> group.getChildren().forEach(layoutedItem ->
                hexMap.add((Item) layoutedItem.getComponent(), hexMap.findFreeSpot(layoutedItem))
        ));

        // Iterates over all items and invokes pathfinding for their relations, requires that ALL items are present
        landscape.getChildren().forEach(group -> group.getChildren().forEach(layoutedItem -> {
            Item item = (Item) layoutedItem.getComponent();
            LOGGER.debug("Adding {} relations for {}", item.getRelations().size(), item.getFullyQualifiedIdentifier());
            item.getRelations().stream()
                    .filter(rel -> rel.getSource().equals(item)) //do not set twice / incoming (inverse) relations
                    .forEach(rel -> hexMap.addPath(rel, false));
        }));

        // group area last, because items AND relations need to bet set
        landscape.getChildren().forEach(group -> {
            hexMap.addGroupArea((Group) group.getComponent());
        });

    }

}
