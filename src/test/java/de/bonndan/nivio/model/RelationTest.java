package de.bonndan.nivio.model;

import de.bonndan.nivio.GraphTestSupport;
import de.bonndan.nivio.output.dto.RelationApiModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RelationTest {

    private GraphTestSupport graph;
    private Item one;
    private Item two;

    @BeforeEach
    void setup() {
        graph = new GraphTestSupport();

        graph.getTestGroup("foo");
        one = graph.getTestItem("foo", "bar");
        two = graph.getTestItem("foo", "baz");
    }

    @Test
    void toApiModel() {

        Relation relation = new Relation(one, two, null, null, RelationType.PROVIDER);
        graph.landscape.getWriteAccess().addOrReplaceRelation(relation);

        RelationApiModel apiModel = new RelationApiModel(relation, one);

        assertThat(apiModel).isNotNull();
        assertThat(apiModel.getDirection()).isEqualTo(RelationApiModel.OUTBOUND);
        assertThat(apiModel.getName()).isEqualTo("baz");
        assertThat(apiModel.getType()).isEqualTo(relation.getType());
        assertThat(apiModel.source).isEqualTo(relation.getSource().getFullyQualifiedIdentifier());
        assertThat(apiModel.getTarget()).isEqualTo(relation.getTarget().getFullyQualifiedIdentifier());
        assertThat(apiModel.getDescription()).isEqualTo(relation.getDescription());
        assertThat(apiModel.getFormat()).isEqualTo(relation.getFormat());
    }

    @Test
    void inbound() {

        Relation relation = RelationFactory.createForTesting(one, two);
        graph.landscape.getWriteAccess().addOrReplaceRelation(relation);

        RelationApiModel apiModel = new RelationApiModel(relation, two);

        assertThat(apiModel).isNotNull();
        assertThat(apiModel.getDirection()).isEqualTo(RelationApiModel.INBOUND);
        assertThat(apiModel.getName()).isEqualTo("bar");
    }

    @Test
    void inboundName() {
        Item one = graph.getTestItemBuilder("foo", "bar").withName("huhu").build();
        graph.landscape.getWriteAccess().addOrReplaceChild(one);
        Item two = graph.getTestItem("foo", "baz");
        Relation relation = RelationFactory.createForTesting(one, two);
        graph.landscape.getWriteAccess().addOrReplaceRelation(relation);

        RelationApiModel apiModel = new RelationApiModel(relation, two);

        assertThat(apiModel).isNotNull();
        assertThat(apiModel.getDirection()).isEqualTo(RelationApiModel.INBOUND);
        assertThat(apiModel.getName()).isEqualTo("huhu");
    }

    @Test
    void getChangesInType() {
        Item b = graph.itemAB;
        Item c = graph.itemAC;
        Relation before = new Relation(b, c, "foo", "JSON", null);
        graph.landscape.getWriteAccess().addOrReplaceRelation(before);
        Relation after = new Relation(b, c, "foo", "JSON", RelationType.PROVIDER);

        //when
        List<String> changes = before.getChanges(after);

        //then
        assertThat(changes).isNotNull().hasSize(1);
        assertThat(changes.get(0)).contains("Type changed ");
    }

    @Test
    void getChangesInFormat() {
        Item b = graph.itemAB;
        Item c = graph.itemAC;
        Relation before = new Relation(b, c, "foo", "JSON", RelationType.PROVIDER);
        Relation after = new Relation(b, c, "foo", "XML", RelationType.PROVIDER);

        //when
        List<String> changes = before.getChanges(after);

        //then
        assertThat(changes).isNotNull().hasSize(1);
        assertThat(changes.get(0)).contains("Format changed ");
    }

    @Test
    void getChangesInDescription() {
        Item b = graph.itemAB;
        Item c = graph.itemAC;
        Relation before = new Relation(b, c, "foo", "JSON", RelationType.PROVIDER);
        Relation after = new Relation(b, c, "bar", "JSON", RelationType.PROVIDER);

        //when
        List<String> changes = before.getChanges(after);

        //then
        assertThat(changes).isNotNull().hasSize(1);
        assertThat(changes.get(0)).contains("Description changed ");
    }

    @Test
    void hasNoChange() {
        Item b = graph.itemAB;
        Item c = graph.itemAC;
        Relation before = new Relation(b, c, "foo", "JSON", RelationType.PROVIDER);
        Relation after = new Relation(b, c, "foo", "JSON", RelationType.PROVIDER);

        //when
        List<String> changes = before.getChanges(after);

        //then
        assertThat(changes).isNotNull().hasSize(0);
    }
}