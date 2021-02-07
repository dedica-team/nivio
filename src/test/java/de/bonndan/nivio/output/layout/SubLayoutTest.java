package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.model.LandscapeConfig;
import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Relation;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static de.bonndan.nivio.model.ItemFactory.getTestItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SubLayoutTest {

    @Test
    public void testWithARelation() {

        //given
        Item bar = getTestItem("foo", "bar");
        Item baz = getTestItem("foo", "baz");
        baz.getRelations().add(new Relation(baz, bar));

        Group foo = new Group("foo", "landscapeIdentifier", Set.of(bar, baz));

        HashSet<Item> objects = new HashSet<>();
        objects.add(bar);
        objects.add(baz);
        //when
        SubLayout subLayout = new SubLayout(foo, objects, new LandscapeConfig.LayoutConfig());

        //then
        LayoutedComponent outerBounds = subLayout.getOuterBounds();
        assertNotNull(outerBounds);
        assertEquals(foo, outerBounds.getComponent());
        LayoutedComponent one = outerBounds.getChildren().get(0);
        assertNotNull(one);
        assertEquals(bar, one.getComponent());
        assertEquals(-54, Math.round(one.getX()));
        assertEquals(-14, Math.round(one.getY()));

        LayoutedComponent two = outerBounds.getChildren().get(1);
        assertNotNull(two);
        assertEquals(baz, two.getComponent());
        assertEquals(104, Math.round(two.getX()));
        assertEquals(64, Math.round(two.getY()));
    }
}