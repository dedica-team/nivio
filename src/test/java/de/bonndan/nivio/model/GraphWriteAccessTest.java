package de.bonndan.nivio.model;

import de.bonndan.nivio.GraphTestSupport;
import de.bonndan.nivio.input.dto.RelationDescription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static de.bonndan.nivio.model.ItemFactory.getTestItem;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class GraphWriteAccessTest {

    private Group g;
    private GraphTestSupport graph;
    private GraphWriteAccess<GraphComponent> writeAccess;

    @BeforeEach
    void setup() {
        graph = new GraphTestSupport();
        writeAccess = graph.landscape.getIndexWriteAccess();
        g = graph.getTestGroup("foo");
    }

    @Test
    void addOrReplaceChildAttaches() {

        //given
        Group group = graph.getTestGroup("abc");

        //then
        assertThat(group.isAttached()).isTrue();
        assertDoesNotThrow(group::getParent);
    }


    @Test
    void addOrReplaceChildCheckPresence() {

        //then
        assertThrows(IllegalArgumentException.class, () -> graph.landscape.getIndexWriteAccess().addOrReplaceChild(graph.getTestGroup("a")));
    }

    @Test
    void addItemAllowed() {
        Item item = ItemBuilder.anItem().withIdentifier("hurra").withParent(g).build();

        //when
        writeAccess.addOrReplaceChild(item);
        assertEquals(1, g.getChildren().size());
    }

    @Test
    void addItemForbidden() {
        Group foobar = GroupBuilder.aTestGroup("foobar").build(); //not in graph

        Item item = ItemBuilder.anItem()
                .withIdentifier("test")
                .withParent(foobar)
                .build();

        //when
        assertThrows(NoSuchElementException.class, () -> writeAccess.addOrReplaceChild(item));
    }

    @Test
    void replacesItemFQI() {
        Item one = graph.getTestItem("foo", "one");

        assertThat(g.getChildren()).containsExactly(one);

        Item copy = graph.getTestItem("foo", "one");
        copy.setLabel(Label.version, "1");

        assertThat(g.getChildren()).containsExactly(copy);
    }

    @Test
    void removeItem() {
        Item item = graph.getTestItem("foo", "b");

        assertEquals(1, g.getChildren().size());
        assertThat(item.isAttached()).isTrue();

        //when
        boolean b = writeAccess.removeChild(item);
        assertThat(b).isTrue();
        assertThat(g.getChildren()).hasSize(0);
    }

    @Test
    void sameObjectCheck() {
        Item item = graph.getTestItem("foo", "b");

        //re-add of same object
        assertThrows(IllegalArgumentException.class, () -> writeAccess.addOrReplaceChild(item));
    }

    @Test
    void removeItemFails() {
        Item item = graph.getTestItem("foo", "b");

        assertThrows(NullPointerException.class, () -> writeAccess.removeChild(getTestItem("foo", "c")));
    }

    @Test
    void addOrReplaceRelationAttaches() {

        //given
        Relation relation = RelationFactory.create(graph.itemAA, graph.itemAC, new RelationDescription());

        //when
        writeAccess.addOrReplaceRelation(relation);

        //then
        assertThat(relation.isAttached()).isTrue();
    }

    @Test
    void addOrReplaceRelationDetaches() {

        //given
        Relation relation = RelationFactory.create(graph.itemAA, graph.itemAC, new RelationDescription());
        writeAccess.addOrReplaceRelation(relation);

        //when
        Relation relation2 = RelationFactory.create(graph.itemAA, graph.itemAC, new RelationDescription());
        writeAccess.addOrReplaceRelation(relation2);

        //then
        assertThat(relation.isAttached()).isFalse();
    }
}