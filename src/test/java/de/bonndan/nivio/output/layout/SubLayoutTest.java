package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.GraphTestSupport;
import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.LayoutConfig;
import de.bonndan.nivio.model.RelationFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SubLayoutTest {

    private Group foo;
    private Item bar;
    private Item baz;

    @BeforeEach
    void setup() {
        GraphTestSupport graph = new GraphTestSupport();

        foo = graph.getTestGroup("foo");
        bar = graph.getTestItem(foo.getIdentifier(), "bar");
        baz = graph.getTestItem(foo.getIdentifier(), "baz");

        graph.landscape.getIndexWriteAccess().addOrReplaceRelation(RelationFactory.createForTesting(baz, bar));
        graph.landscape.getIndexWriteAccess().addOrReplaceRelation(RelationFactory.createForTesting(baz, bar));
    }

    @Test
    void testWithARelation() {

        //given

        HashSet<Item> objects = new HashSet<>();
        objects.add(bar);
        objects.add(baz);

        //when
        SubLayout subLayout = new SubLayout(true, new LayoutConfig());
        subLayout.render(foo, objects);

        //then
        LayoutedComponent outerBounds = subLayout.getOuterBounds();
        assertNotNull(outerBounds);
        assertEquals(foo, outerBounds.getComponent());
        LayoutedComponent one = outerBounds.getChildren().get(0);
        assertNotNull(one);
        assertEquals(bar, one.getComponent());
        assertEquals(21, Math.round(one.getX()));
        assertEquals(0, Math.round(one.getY()));

        LayoutedComponent two = outerBounds.getChildren().get(1);
        assertNotNull(two);
        assertEquals(baz, two.getComponent());
        assertEquals(-550, Math.round(two.getX()));
        assertEquals(-1, Math.round(two.getY()));
    }

    @Test
    @DisplayName("ensures that two opposite relations do not have double effect")
    void ignoresRedundantRelations() {

        //given
        HashSet<Item> objects = new HashSet<>();
        objects.add(bar);
        objects.add(baz);

        //when
        SubLayout subLayout = new SubLayout(true, new LayoutConfig());
        subLayout.render(foo, objects);

        //then
        LayoutedComponent outerBounds = subLayout.getOuterBounds();
        assertNotNull(outerBounds);
        assertEquals(foo, outerBounds.getComponent());
        LayoutedComponent one = outerBounds.getChildren().get(0);
        assertNotNull(one);
        assertEquals(bar, one.getComponent());
        assertEquals(21, Math.round(one.getX()));
        assertEquals(0, Math.round(one.getY()));

        LayoutedComponent two = outerBounds.getChildren().get(1);
        assertNotNull(two);
        assertEquals(baz, two.getComponent());
        assertEquals(-550, Math.round(two.getX()));
        assertThat(two.getY()).isEqualTo(-1);
    }
}