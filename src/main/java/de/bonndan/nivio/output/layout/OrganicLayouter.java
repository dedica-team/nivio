package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.output.map.hex.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import java.awt.geom.Point2D;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Applies {@link FastOrganicLayout} to landscape components and writes the rendered data to component labels.
 */
public class OrganicLayouter implements Layouter {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrganicLayouter.class);

    @Override
    public LayoutedComponent layout(@NonNull final Landscape landscape) {

        Map<String, SubLayout> subGraphs = new LinkedHashMap<>();
        Objects.requireNonNull(landscape).getGroups().forEach((name, groupItem) -> {
            SubLayout subLayout = new SubLayout(groupItem, groupItem.getItems(), landscape.getConfig().getItemLayoutConfig());
            subGraphs.put(name, subLayout);
        });

        Map<String, Group> groupMap = new LinkedHashMap<>();
        landscape.getGroups().forEach(groupMap::put);

        AllGroupsLayout allGroupsLayout = new AllGroupsLayout(landscape, groupMap, subGraphs);
        LayoutedComponent layoutedComponent = allGroupsLayout.getRendered();
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
            groupBounds.setX(groupBounds.getX() + margin.x);
            groupBounds.setY(groupBounds.getY() + margin.y);
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
                    LOGGER.debug("item pos with group offset: {} {}", itemBounds.getX(), itemBounds.getY());
                }
            });
        });
    }

    /**
     * @return the left/top extra margin to shift all items into positive coordinates
     */
    private Point2D.Double getMargins(LayoutedComponent layoutedLandscape) {
        List<Point2D.Double> minMaxBoundaries = getMinMaxBoundaries(layoutedLandscape);
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

    /**
     * @param layoutedComponent parent
     * @return top-left and bottom-right as points
     */
    static List<Point2D.Double> getMinMaxBoundaries(LayoutedComponent layoutedComponent) {
        AtomicLong minX = new AtomicLong(Integer.MAX_VALUE);
        AtomicLong maxX = new AtomicLong(Integer.MIN_VALUE);
        AtomicLong minY = new AtomicLong(Integer.MAX_VALUE);
        AtomicLong maxY = new AtomicLong(Integer.MIN_VALUE);

        layoutedComponent.getChildren().forEach(c -> {
            double x = c.getX();
            double y = c.getY();

            if (x < minX.get())
                minX.set((long) x);
            if (x > maxX.get())
                maxX.set((long) x);

            if (y < minY.get())
                minY.set((long) y);
            if (y > maxY.get())
                maxY.set((long) y);
        });

        return List.of(
                new Point2D.Double(minX.get(), minY.get()),
                new Point2D.Double(maxX.get(), maxY.get())
        );
    }
}
