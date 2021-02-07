package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.model.LandscapeConfig;
import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Relation;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SubLayoutTest {

    @Test
    public void testWithARelation() {

        //given
        Group foo = new Group("foo", "landscapeIdentifier");

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
        assertEquals(-54, Math.round(one.getX()));
        assertEquals(-14, Math.round(one.getY()));

        LayoutedComponent two = outerBounds.getChildren().get(1);
        assertNotNull(two);
        assertEquals(baz, two.getComponent());
        assertEquals(104, Math.round(two.getX()));
        assertEquals(64, Math.round(two.getY()));
    }
}