package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static de.bonndan.nivio.model.ItemFactory.getTestItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SubLayoutTest {

    @Test
    void testWithARelation() {

        //given
        Group foo = new Group("foo", "landscapeIdentifier");

        Item bar = getTestItem(foo.getIdentifier(), "bar");
        foo.addOrReplaceItem(bar);

        Item baz = getTestItem(foo.getIdentifier(), "baz");
        foo.addOrReplaceItem(baz);
        baz.addOrReplace(RelationFactory.createForTesting(baz, bar));

        HashSet<Item> objects = new HashSet<>();
        objects.add(bar);
        objects.add(baz);

        //when
        SubLayout subLayout = new SubLayout(true);
        subLayout.render(foo, objects);

        //then
        LayoutedComponent outerBounds = subLayout.getOuterBounds();
        assertNotNull(outerBounds);
        assertEquals(foo, outerBounds.getComponent());
        LayoutedComponent one = outerBounds.getChildren().get(0);
        assertNotNull(one);
        assertEquals(bar, one.getComponent());
        var pos = 10;
        assertEquals(pos, Math.round(one.getX()));
        assertEquals(pos, Math.round(one.getY()));

        LayoutedComponent two = outerBounds.getChildren().get(1);
        assertNotNull(two);
        assertEquals(baz, two.getComponent());
        assertEquals(-pos, Math.round(two.getX()));
        assertEquals(-pos, Math.round(two.getY()));
    }

    @Test
    @DisplayName("ensures that two opposite relations do not have double effect")
    void ignoresRedundantRelations() {

        //given
        Group foo = new Group("foo", "landscapeIdentifier");
        Item bar = getTestItem(foo.getIdentifier(), "bar");
        foo.addOrReplaceItem(bar);

        Item baz = getTestItem(foo.getIdentifier(), "baz");
        foo.addOrReplaceItem(baz);
        baz.addOrReplace(RelationFactory.createForTesting(baz, bar));
        bar.addOrReplace(RelationFactory.createForTesting(bar, baz));

        HashSet<Item> objects = new HashSet<>();
        objects.add(bar);
        objects.add(baz);

        //when
        SubLayout subLayout = new SubLayout(true);
        subLayout.render(foo, objects);

        //then
        LayoutedComponent outerBounds = subLayout.getOuterBounds();
        assertNotNull(outerBounds);
        assertEquals(foo, outerBounds.getComponent());
        LayoutedComponent one = outerBounds.getChildren().get(0);
        assertNotNull(one);
        assertEquals(bar, one.getComponent());
        assertEquals(-7, Math.round(one.getX()));
        assertEquals(-7, Math.round(one.getY()));

        LayoutedComponent two = outerBounds.getChildren().get(1);
        assertNotNull(two);
        assertEquals(baz, two.getComponent());
        assertEquals(7, Math.round(two.getX()));
        assertEquals(7, Math.round(two.getY()));
    }
}