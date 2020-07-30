package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.LandscapeConfig;
import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.LandscapeImpl;
import de.bonndan.nivio.model.Relation;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AllGroupsLayoutTest {

    @Test
    public void testWithARelation() {

        Group a = new Group("a");
        Group b = new Group("b");
        Group c = new Group("c");

        LandscapeImpl landscape = new LandscapeImpl();
        landscape.setIdentifier("test");
        landscape.getGroups().put("a", a);
        landscape.getGroups().put("b", b);
        landscape.getGroups().put("c", c);

        Map<String, SubLayout> map = Map.of("a", getSubLayout(a),
                "b", getSubLayout(b),
                "c", getSubLayout(c)
        );

        //add some inter-group relations
        Item item1 = a.getItems().get(0);
        Item item2 = b.getItems().get(0);
        Item item3 = c.getItems().get(0);
        item1.getRelations().add(new Relation(item1, item2));
        item2.getRelations().add(new Relation(item2, item3));

        Map<String, Group> groupMap = new LinkedHashMap<>();
        landscape.getGroups().forEach((s, groupItem) -> groupMap.put(s, (Group)groupItem));

        //when
        AllGroupsLayout allGroupsLayout = new AllGroupsLayout(landscape, groupMap, map);

        //then
        assertNotNull(allGroupsLayout);
        LayoutedComponent layoutedLandscape = allGroupsLayout.getRendered();
        assertNotNull(layoutedLandscape);
        assertEquals(landscape, layoutedLandscape.getComponent());
        assertEquals(3, layoutedLandscape.getChildren().size());

        //assert postition is always the game
        assertEquals(240, Math.round(layoutedLandscape.getChildren().get(0).getX()));
        assertEquals(271, Math.round(layoutedLandscape.getChildren().get(0).getY()));
    }

    private SubLayout getSubLayout(Group group) {

        Item bar = new Item();
        bar.setIdentifier("bar" + group.getIdentifier());
        bar.setGroup(group.getIdentifier());
        group.getItems().add(bar);

        Item baz = new Item();
        baz.setIdentifier("baz" + group.getIdentifier());
        baz.setGroup(group.getIdentifier());
        group.getItems().add(baz);
        baz.getRelations().add(new Relation(baz, bar));

        return new SubLayout(group, List.of(bar, baz), new LandscapeConfig.LayoutConfig());
    }
}