package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.model.LandscapeConfig;
import de.bonndan.nivio.model.*;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static de.bonndan.nivio.model.ItemFactory.getTestItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AllGroupsLayoutTest {

    @Test
    void testWithARelation() {

        Group a = new Group("a", "test");
        Item bara = getTestItem("a", "bar" + "a");
        Item fooa = getTestItem("a", "foo" + "a");
        a.addOrReplaceItem(bara);
        a.addOrReplaceItem(fooa);

        Group b = new Group("b", "test");
        Item barb = getTestItem("b", "bar" + "b");
        Item foob = getTestItem("b", "foo" + "b");
        b.addOrReplaceItem(barb);
        b.addOrReplaceItem(foob);

        Group c = new Group("c", "test");
        Item barc = getTestItem("c", "bar" + "c");
        Item fooc = getTestItem("c", "foo" + "c");
        c.addOrReplaceItem(barc);
        c.addOrReplaceItem(fooc);

        Landscape landscape = LandscapeFactory.createForTesting("test", "testLandscape").build();

        landscape.addGroup(a);
        landscape.addGroup(b);
        landscape.addGroup(c);

        Map<String, SubLayout> map = Map.of(
                "a", getSubLayout(a, Set.of(bara, fooa)),
                "b", getSubLayout(b, Set.of(barb, foob)),
                "c", getSubLayout(c, Set.of(barc, fooc))
        );

        //add some inter-group relations
        bara.addOrReplace(RelationFactory.createForTesting(bara, barb));
        barb.addOrReplace(RelationFactory.createForTesting(barb, barc));

        Map<String, Group> groupMap = new LinkedHashMap<>();
        landscape.getGroups().forEach(groupMap::put);

        //when
        AllGroupsLayout allGroupsLayout = new AllGroupsLayout(landscape, groupMap, map);

        //then
        assertNotNull(allGroupsLayout);
        LayoutedComponent layoutedLandscape = allGroupsLayout.getRendered();
        assertNotNull(layoutedLandscape);
        assertEquals(landscape, layoutedLandscape.getComponent());
        assertEquals(3, layoutedLandscape.getChildren().size());

        //assert position is always the same
        assertEquals(1517, Math.round(layoutedLandscape.getChildren().get(0).getX()));
        assertEquals(1449, Math.round(layoutedLandscape.getChildren().get(0).getY()));
    }

    private SubLayout getSubLayout(Group group, Set<Item> items) {
        return new SubLayout(group, items, new LandscapeConfig.LayoutConfig());
    }
}