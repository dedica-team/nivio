package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.model.LandscapeConfig;
import de.bonndan.nivio.model.*;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AllGroupsLayoutTest {

    @Test
    public void testWithARelation() {

        Group a = new Group("a", null);
        Group b = new Group("b", null);
        Group c = new Group("c", null);

        Landscape landscape = LandscapeFactory.createForTesting("test", "testLandscape").build();

        landscape.getGroups().put("a", a);
        landscape.getGroups().put("b", b);
        landscape.getGroups().put("c", c);

        Map<String, SubLayout> map = Map.of("a", getSubLayout(a),
                "b", getSubLayout(b),
                "c", getSubLayout(c)
        );

        //add some inter-group relations
        Item item1 = a.getItems().iterator().next();
        Item item2 = b.getItems().iterator().next();
        Item item3 = c.getItems().iterator().next();
        item1.getRelations().add(new Relation(item1, item2));
        item2.getRelations().add(new Relation(item2, item3));

        Map<String, Group> groupMap = new LinkedHashMap<>();
        landscape.getGroups().forEach((s, groupItem) -> groupMap.put(s, (Group) groupItem));

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

        Item bar = new Item(group.getIdentifier(), "bar" + group.getIdentifier());
        group.addItem(bar);

        Item baz = new Item(group.getIdentifier(), "baz" + group.getIdentifier());
        group.addItem(baz);
        baz.getRelations().add(new Relation(baz, bar));

        return new SubLayout(group, Set.of(bar, baz), new LandscapeConfig.LayoutConfig());
    }
}