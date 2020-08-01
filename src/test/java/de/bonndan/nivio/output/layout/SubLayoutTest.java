package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.LandscapeConfig;
import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Relation;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SubLayoutTest {

    @Test
    public void testWithARelation() {

        //given
        Group foo = new Group("foo");

        Item bar = new Item();
        bar.setIdentifier("bar");
        bar.setGroup(foo.getIdentifier());
        foo.getItems().add(bar);

        Item baz = new Item();
        baz.setIdentifier("baz");
        baz.setGroup(foo.getIdentifier());
        foo.getItems().add(baz);
        baz.getRelations().add(new Relation(baz, bar));

        //when
        SubLayout subLayout = new SubLayout(foo, Set.of(bar, baz), new LandscapeConfig.LayoutConfig());

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