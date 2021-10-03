package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.model.*;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static de.bonndan.nivio.model.ItemFactory.getTestItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AllGroupsLayoutTest {

    @Test
    void testWithARelation() {

        Group a = new Group("a", "test");
        Group b = new Group("b", "test");
        Group c = new Group("c", "test");

        Landscape landscape = LandscapeFactory.createForTesting("test", "testLandscape").build();

        landscape.addGroup(a);
        landscape.addGroup(b);
        landscape.addGroup(c);

        Map<String, SubLayout> map = Map.of(
                "a", getSubLayout(a),
                "b", getSubLayout(b),
                "c", getSubLayout(c)
        );

        //add some inter-group relations
        Item item1 = a.getItems().iterator().next();
        Item item2 = b.getItems().iterator().next();
        Item item3 = c.getItems().iterator().next();
        item1.addOrReplace(RelationFactory.createForTesting(item1, item2));
        item2.addOrReplace(RelationFactory.createForTesting(item2, item3));


        //when
        AllGroupsLayout allGroupsLayout = new AllGroupsLayout(true);

        //then
        LayoutedComponent layoutedLandscape = allGroupsLayout.getRendered(landscape, landscape.getGroups(), map);
        assertNotNull(layoutedLandscape);
        assertEquals(landscape, layoutedLandscape.getComponent());
        assertEquals(3, layoutedLandscape.getChildren().size());

        //assert position is always the same
        LayoutedComponent child0 = layoutedLandscape.getChildren().get(0);
        assertEquals("test/a", child0.getComponent().getFullyQualifiedIdentifier().toString());
        assertEquals(-658, Math.round(child0.getX()));
        assertEquals(-643, Math.round(child0.getY()));
    }

    private SubLayout getSubLayout(Group group) {

        Item bar = getTestItem(group.getIdentifier(), "bar" + group.getIdentifier());
        group.addOrReplaceItem(bar);

        Item baz = getTestItem(group.getIdentifier(), "baz" + group.getIdentifier());
        group.addOrReplaceItem(baz);
        baz.addOrReplace(RelationFactory.createForTesting(baz, bar));

        return new SubLayout(group, Set.of(bar, baz), new LandscapeConfig.LayoutConfig());
    }
}