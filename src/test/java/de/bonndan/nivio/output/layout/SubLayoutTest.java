package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.GraphTestSupport;
import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.LayoutConfig;
import de.bonndan.nivio.model.RelationFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.LinkedHashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SubLayoutTest {

    private Group groupFoo;
    private Item bar;
    private Item baz;

    @BeforeEach
    void setup() {
        GraphTestSupport graph = new GraphTestSupport();

        groupFoo = graph.getTestGroup("foo");
        bar = graph.getTestItem(groupFoo.getIdentifier(), "bar");
        baz = graph.getTestItem(groupFoo.getIdentifier(), "baz");

        graph.landscape.getWriteAccess().addOrReplaceRelation(RelationFactory.create(baz, bar));
        graph.landscape.getWriteAccess().addOrReplaceRelation(RelationFactory.create(baz, bar));
    }

    @Test
    void testWithARelation() {

        //given

        HashSet<Item> objects = new LinkedHashSet<>();
        objects.add(bar);
        objects.add(baz);

        //when
        SubLayout subLayout = new SubLayout(true, new LayoutConfig());
        subLayout.render(groupFoo, objects);

        //then
        LayoutedComponent outerBounds = subLayout.getOuterBounds();
        assertNotNull(outerBounds);
        assertEquals(groupFoo, outerBounds.getComponent());

        LayoutedComponent one = outerBounds.getChildren().get(0);
        assertNotNull(one);
        assertEquals(baz, one.getComponent());
        assertEquals(21, Math.round(one.getCenterX()));
        assertEquals(0, Math.round(one.getCenterY()));

        LayoutedComponent two = outerBounds.getChildren().get(1);
        assertNotNull(two);
        assertEquals(bar, two.getComponent());
        assertEquals(-550, Math.round(two.getCenterX()));
        assertEquals(-1, Math.round(two.getCenterY()));
    }
}