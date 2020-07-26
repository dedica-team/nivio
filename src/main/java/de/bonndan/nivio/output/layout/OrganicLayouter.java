package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.LandscapeImpl;

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
    public LayoutedComponent layout(LandscapeImpl landscape) {

        Map<String, SubGraph> subgraphs = new LinkedHashMap<>();
        landscape.getGroups().forEach((name, groupItem) ->  {
            SubGraph subGraph = new SubGraph(groupItem, ((Group)groupItem).getItems());
            subgraphs.put(name, subGraph);
        });

        Map<String, Group> groupMap = new LinkedHashMap<>();
        landscape.getGroups().forEach((s, groupItem) -> groupMap.put(s, (Group)groupItem));

        AllGroupsGraph allGroupsGraph = new AllGroupsGraph(landscape, groupMap, subgraphs);
        return allGroupsGraph.getRendered();
    }
}
