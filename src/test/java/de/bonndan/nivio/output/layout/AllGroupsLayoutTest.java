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
    public void testWithARelation() {

        Group groupA = new Group("a", null, getTestItems("a"));
        Group groupB = new Group("b", null, getTestItems("b"));
        Group groupC = new Group("c", null, getTestItems("c"));

        Landscape landscape = LandscapeFactory.createForTesting("test", "testLandscape").build();

        landscape.getGroups().put("a", groupA);
        landscape.getGroups().put("b", groupB);
        landscape.getGroups().put("c", groupC);

        Map<String, SubLayout> map = Map.of("a", getSubLayout(groupA),
                "b", getSubLayout(groupB),
                "c", getSubLayout(groupC)
        );

        //add some inter-group relations
        Item item1 = groupA.getItems().iterator().next();
        Item item2 = groupB.getItems().iterator().next();
        Item item3 = groupC.getItems().iterator().next();
        item1.getRelations().add(new Relation(item1, item2));
        item2.getRelations().add(new Relation(item2, item3));

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

        //assert postition is always the same
        assertEquals(615, Math.round(layoutedLandscape.getChildren().get(0).getX()));
        assertEquals(-343, Math.round(layoutedLandscape.getChildren().get(0).getY()));
    }

    private SubLayout getSubLayout(Group group) {
        return new SubLayout(group, group.getItems(), new LandscapeConfig.LayoutConfig());
    }

    private Set<Item> getTestItems(String groupIdentifier) {
        Item bar = getTestItem(groupIdentifier, "bar" + groupIdentifier);
        Item baz = getTestItem(groupIdentifier, "baz" + groupIdentifier);
        baz.getRelations().add(new Relation(baz, bar));
        return Set.of(bar, baz);
    }

}