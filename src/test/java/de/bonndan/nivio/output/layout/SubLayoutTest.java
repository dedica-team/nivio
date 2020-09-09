package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.LandscapeConfig;
import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Relation;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SubLayoutTest {

    @Test
    public void testWithARelation() {

        //given
        Group foo = new Group("foo");

        Item bar = new Item(foo.getIdentifier(), "bar");;
        foo.addItem(bar);

        Item baz = new Item(foo.getIdentifier(), "baz");
        foo.addItem(baz);
        baz.getRelations().add(new Relation(baz, bar));

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
        assertEquals(63, Math.round(one.getX()));
        assertEquals(63, Math.round(one.getY()));

        LayoutedComponent two = outerBounds.getChildren().get(1);
        assertNotNull(two);
        assertEquals(baz, two.getComponent());
        assertEquals(-13, Math.round(two.getX()));
        assertEquals(-13, Math.round(two.getY()));
    }
}