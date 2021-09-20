package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.model.*;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static de.bonndan.nivio.model.ItemFactory.getTestItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SubLayoutTest {

    @Test
    public void testWithARelation() {

        //given
        Group foo = new Group("foo", "landscapeIdentifier");

        Item bar = getTestItem(foo.getIdentifier(), "bar");
        ;
        foo.addOrReplaceItem(bar);

        Item baz = getTestItem(foo.getIdentifier(), "baz");
        foo.addOrReplaceItem(baz);
        baz.addOrReplace(RelationFactory.createForTesting(baz, bar));

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
        assertEquals(-36, Math.round(one.getX()));
        assertEquals(-37, Math.round(one.getY()));

        LayoutedComponent two = outerBounds.getChildren().get(1);
        assertNotNull(two);
        assertEquals(baz, two.getComponent());
        assertEquals(87, Math.round(two.getX()));
        assertEquals(86, Math.round(two.getY()));
    }
}