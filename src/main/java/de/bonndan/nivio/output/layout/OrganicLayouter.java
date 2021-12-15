package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.output.map.hex.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

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

        AtomicInteger minX = new AtomicInteger(Integer.MAX_VALUE);
        AtomicInteger minY = new AtomicInteger(Integer.MAX_VALUE);
        layoutedLandscape.getChildren().forEach(groupBounds -> {
            if (groupBounds.getX() < minX.get()) {
                minX.set((int) groupBounds.getX());
            }

            if (groupBounds.getY() < minY.get()) {
                minY.set((int) groupBounds.getY());
            }
        });

        final int groupPadding = Hex.HEX_SIZE;

        layoutedLandscape.getChildren().forEach(groupBounds -> {

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("group {} offset {} {}", groupBounds.getComponent().getIdentifier(), groupBounds.getX(), groupBounds.getY());
            }
            groupBounds.setX((groupBounds.getX() - minX.get()) + groupPadding);
            groupBounds.setY(groupBounds.getY() - minY.get() + groupPadding);

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


}
