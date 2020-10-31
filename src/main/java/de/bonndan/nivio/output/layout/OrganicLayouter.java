package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Landscape;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Applies {@link FastOrganicLayout} to landscape components and writes the rendered data to component labels.
 *
 *
 */
public class OrganicLayouter implements Layouter<LayoutedComponent> {

    public OrganicLayouter() {
    }

    @Override
    public LayoutedComponent layout(Landscape landscape) {

        Map<String, SubLayout> subgraphs = new LinkedHashMap<>();
        landscape.getGroups().forEach((name, groupItem) ->  {
            SubLayout subLayout = new SubLayout(groupItem, ((Group)groupItem).getItems(), landscape.getConfig().getItemLayoutConfig());
            subgraphs.put(name, subLayout);
        });

        Map<String, Group> groupMap = new LinkedHashMap<>();
        landscape.getGroups().forEach((s, groupItem) -> groupMap.put(s, (Group)groupItem));

        AllGroupsLayout allGroupsLayout = new AllGroupsLayout(landscape, groupMap, subgraphs);
        return allGroupsLayout.getRendered();
    }
}
