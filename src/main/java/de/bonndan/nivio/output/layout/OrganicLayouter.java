package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.output.map.hex.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import java.awt.geom.Point2D;
import java.util.*;

/**
 * Applies {@link FastOrganicLayout} to landscape components and writes the rendered data to component labels.
 */
public class OrganicLayouter implements Layouter {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrganicLayouter.class);

    private final boolean debug;

    public OrganicLayouter() {
        debug = false;
    }

    public OrganicLayouter(boolean debug) {
        this.debug = debug;
    }

    @Override
    public LayoutedComponent layout(@NonNull final Landscape landscape) {

        Map<String, SubLayout> subGraphs = new LinkedHashMap<>();
        Objects.requireNonNull(landscape).getGroups().forEach((name, group) -> {
            Set<Item> items = landscape.getItems().retrieve(group.getItems());
            if (items.isEmpty()) return;
            SubLayout subLayout = new SubLayout(debug);
            subLayout.render(group, items);
            subGraphs.put(name, subLayout);
        });

        AllGroupsLayout allGroupsLayout = new AllGroupsLayout(debug);
        LayoutedComponent layoutedComponent = allGroupsLayout.getRendered(landscape, new LinkedHashMap<>(landscape.getGroups()), subGraphs);
        shiftGroupsAndItems(layoutedComponent);
        return layoutedComponent;
    }

    /**
     * @param layoutedLandscape layouted landscape
     */
    private void shiftGroupsAndItems(LayoutedComponent layoutedLandscape) {

        Point2D.Double margin = getMargins(layoutedLandscape);
        layoutedLandscape.getChildren().forEach(groupBounds -> {

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("group {} offset {} {}", groupBounds.getComponent().getIdentifier(), groupBounds.getX(), groupBounds.getY());
            }
            groupBounds.setX((long) (groupBounds.getX() + margin.x));
            groupBounds.setY((long) (groupBounds.getY() + margin.y));
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("corrected group {} offset {} {}", groupBounds.getComponent().getIdentifier(), groupBounds.getX(), groupBounds.getY());
            }

            groupBounds.getChildren().forEach(itemBounds -> {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("original item pos {} {}", itemBounds.getX(), itemBounds.getY());
                }
                itemBounds.setX(itemBounds.getX() + groupBounds.getX());
                itemBounds.setY(itemBounds.getY() + groupBounds.getY());
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("item {} pos with group offset: {} {}", itemBounds, itemBounds.getX(), itemBounds.getY());
                }
            });
        });
    }

    /**
     * @return the left/top extra margin to shift all items into positive coordinates
     */
    private Point2D.Double getMargins(LayoutedComponent layoutedLandscape) {

        List<Point2D.Double> minMaxBoundaries = layoutedLandscape.getMinMaxBoundaries();
        var min = minMaxBoundaries.get(0);
        var max = minMaxBoundaries.get(1);

        layoutedLandscape.setWidth(max.x - min.x);
        layoutedLandscape.setHeight(max.y - min.y);

        int marginX = 2 * Hex.HEX_SIZE;
        int marginY = 2 * Hex.HEX_SIZE;

        if (min.x < 0) {
            if (LOGGER.isDebugEnabled()) LOGGER.debug("fixing minX by {}", min.x * -1);
            marginX += min.x * -1;
        }

        if (min.y < 0) {
            if (LOGGER.isDebugEnabled()) LOGGER.debug("fixing minY by {}", min.y * -1);
            marginY += min.y * -1;
        }

        if (LOGGER.isDebugEnabled()) LOGGER.debug("Map shift x {} y {} ", marginX, marginY);
        return new Point2D.Double(marginX, marginY);
    }


}
