package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.LandscapeImpl;
import de.bonndan.nivio.output.Color;
import de.bonndan.nivio.output.LocalServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Applies {@link FastOrganicLayout} to landscape components and writes the rendered data to component labels.
 *
 *
 */
public class OrganicLayouter implements Layouter<LayoutedComponent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrganicLayouter.class);

    private final LocalServer localServer;

    public OrganicLayouter( LocalServer localServer) {
        this.localServer = localServer;
    }

    @Override
    public LayoutedComponent layout(LandscapeImpl landscape) {

        Map<String, SubGraph> subgraphs = new LinkedHashMap<>();
        landscape.getGroups().forEach((name, groupItem) ->  {
            SubGraph subGraph = new SubGraph(groupItem, ((Group)groupItem).getItems());
            subgraphs.put(name, subGraph);
        });

        Map<String, Group> groupMap = new LinkedHashMap<>();
        landscape.getGroups().forEach((s, groupItem) -> groupMap.put(s, (Group)groupItem));

        AllGroupsGraph allGroupsGraph = new AllGroupsGraph(landscape, groupMap, subgraphs);

        applyArtifactValues(allGroupsGraph.getRendered());

        return allGroupsGraph.getRendered();
    }

    public void applyArtifactValues(LayoutedComponent layoutedComponent) {

        AtomicLong minX = new AtomicLong(0);
        AtomicLong maxX = new AtomicLong(0);
        AtomicLong minY = new AtomicLong(0);
        AtomicLong maxY = new AtomicLong(0);
        layoutedComponent.getChildren().forEach(groupBounds -> {
                if (groupBounds.getX() < minX.get())
                    minX.set((long) groupBounds.getX());
                if (groupBounds.getX() > maxX.get())
                    maxX.set((long) groupBounds.getX());
                if (groupBounds.getY() < minY.get())
                    minY.set((long) groupBounds.getY());
                if (groupBounds.getY() > maxY.get())
                    maxY.set((long) groupBounds.getY());
        });

        layoutedComponent.setWidth(maxX.get() - minX.get());
        layoutedComponent.setHeight(maxY.get() - minY.get());

        int marginX = 50;
        if (minX.get() < 0) {
            marginX += minX.get()*-1;
        }

        int marginY = 50;
        if (minY.get() < 0) {
            marginY += minY.get()*-1;
        }

        LOGGER.debug("Shifting elements into positive coords by x {} and y {}", marginX, marginY);
        applyValues(layoutedComponent, marginX, marginY);
    }

    /**
     *
     * @param layoutedComponent layouted landscape
     * @param marginX shift all groups in x direction
     * @param marginY shift all groups in y direction
     */
    private void applyValues(LayoutedComponent layoutedComponent, int marginX, int marginY) {

        layoutedComponent.getChildren().forEach(groupBounds -> {

            LOGGER.debug("group offset {} {}", groupBounds.getX(), groupBounds.getY());
            Group group = (Group) groupBounds.getComponent();
            if (StringUtils.isEmpty(group.getColor())) {
                group.setColor(Color.getGroupColor(group));
            }
            groupBounds.setX(groupBounds.getX() + marginX);
            groupBounds.setY(groupBounds.getY() + marginY);

            groupBounds.getChildren().forEach(itemBounds-> {
                LOGGER.debug("original item pos {} {}", itemBounds.getX(), itemBounds.getY());
                itemBounds.setX(itemBounds.getX() + groupBounds.getX());
                itemBounds.setY(itemBounds.getY() + groupBounds.getY());
                LOGGER.debug("item pos with group offset: {} {}", itemBounds.getX(), itemBounds.getY());

                Item item = (Item) itemBounds.getComponent();
                itemBounds.setColor(group.getColor());
                itemBounds.setIcon(localServer.getIconUrl(item).toString());
            });
        });
    }
}
