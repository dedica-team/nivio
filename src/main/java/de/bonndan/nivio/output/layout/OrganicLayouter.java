package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.LandscapeImpl;
import de.bonndan.nivio.output.Color;
import de.bonndan.nivio.output.LayoutedArtifact;
import de.bonndan.nivio.output.LocalServer;
import de.bonndan.nivio.output.Rendered;
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
public class OrganicLayouter implements Layouter<ComponentBounds> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrganicLayouter.class);

    private final LocalServer localServer;

    public OrganicLayouter( LocalServer localServer) {
        this.localServer = localServer;
    }

    @Override
    public LayoutedArtifact<ComponentBounds> layout(LandscapeImpl landscape) {

        Map<String, SubGraph> subgraphs = new LinkedHashMap<>();
        landscape.getGroups().forEach((name, groupItem) ->  {
            SubGraph subGraph = new SubGraph(groupItem, ((Group)groupItem).getItems());
            subgraphs.put(name, subGraph);
        });

        Map<String, Group> groupMap = new LinkedHashMap<>();
        landscape.getGroups().forEach((s, groupItem) -> groupMap.put(s, (Group)groupItem));

        AllGroupsGraph allGroupsGraph = new AllGroupsGraph(landscape, groupMap, subgraphs);

        // TODO remove, this just stores in the landscape items what is already stored in the ComponentBounds objects
        // TODO SVGRender could read directly from ComponentBounds
        applyArtifactValues(landscape, allGroupsGraph);

        return allGroupsGraph;
    }

    public void applyArtifactValues(LandscapeImpl landscape, LayoutedArtifact<ComponentBounds> layoutedArtifact) {

        AtomicLong minX = new AtomicLong(0);
        AtomicLong maxX = new AtomicLong(0);
        AtomicLong minY = new AtomicLong(0);
        AtomicLong maxY = new AtomicLong(0);
        layoutedArtifact.getRendered().getChildren().forEach(groupBounds -> {
            groupBounds.getChildren().forEach(item -> {
                if (item.getX() < minX.get())
                    minX.set((long) item.getX());
                if (item.getX() > maxX.get())
                    maxX.set((long) item.getX());
                if (item.getY() < minY.get())
                    minY.set((long) item.getY());
                if (item.getY() > maxY.get())
                    maxY.set((long) item.getY());
            });
        });

        landscape.setWidth(maxX.get() - minX.get());
        landscape.setHeight(maxY.get() - minY.get());

        int marginX = 50;
        if (minX.get() < 0) {
            marginX += minX.get()*-1;
        }

        int marginY = 50;
        if (minY.get() < 0) {
            marginY += minY.get()*-1;
        }

        applyValues(layoutedArtifact, marginX, marginY);
    }

    /**
     *
     * @param layoutedArtifact layouted landscape
     * @param marginX shift all groups in x direction
     * @param marginY shift all groups in y direction
     */
    private void applyValues(LayoutedArtifact<ComponentBounds> layoutedArtifact, int marginX, int marginY) {

        layoutedArtifact.getRendered().getChildren().forEach(groupBounds -> {

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
                item.setColor(group.getColor());
                setRenderedLabels(item, itemBounds);
            });
            setRenderedLabels(group, groupBounds);

        });

    }

    private void setRenderedLabels(Rendered item, ComponentBounds dim) {
        item.setLabel(Rendered.LABEL_RENDERED_ICON, localServer.getIconUrl(item).toString());
        item.setLabel(Rendered.LX, String.valueOf(dim.getX()));
        item.setLabel(Rendered.LY, String.valueOf(dim.getY()));
        item.setLabel(Rendered.LABEL_RENDERED_WIDTH, String.valueOf(dim.getWidth()));
        item.setLabel(Rendered.LABEL_RENDERED_HEIGHT, String.valueOf(dim.getHeight()));
    }
}
