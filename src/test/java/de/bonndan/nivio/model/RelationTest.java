package de.bonndan.nivio.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static de.bonndan.nivio.model.ItemFactory.getTestItem;
import static de.bonndan.nivio.model.ItemFactory.getTestItemBuilder;
import static org.assertj.core.api.Assertions.assertThat;

class RelationTest {

    @Test
    void toApiModel() {
        Item one = getTestItem("foo", "bar");
        Item two = getTestItem("foo", "baz");
        Relation relation = new Relation(one, two, null, null, RelationType.PROVIDER);

        Relation.ApiModel apiModel = new Relation.ApiModel(relation, one);

        assertThat(apiModel).isNotNull();
        assertThat(apiModel.direction).isEqualTo(Relation.ApiModel.OUTBOUND);
        assertThat(apiModel.name).isEqualTo("baz");
        assertThat(apiModel.type).isEqualTo(relation.getType());
        assertThat(apiModel.source).isEqualTo(relation.getSource());
        assertThat(apiModel.target).isEqualTo(relation.getTarget());
        assertThat(apiModel.description).isEqualTo(relation.getDescription());
        assertThat(apiModel.format).isEqualTo(relation.getFormat());
    }

    @Test
    void inbound() {
        Item one = getTestItem("foo", "bar");
        Item two = getTestItem("foo", "baz");
        Relation relation = RelationFactory.createForTesting(one, two);

        Relation.ApiModel apiModel = new Relation.ApiModel(relation, two);

        assertThat(apiModel).isNotNull();
        assertThat(apiModel.direction).isEqualTo(Relation.ApiModel.INBOUND);
        assertThat(apiModel.name).isEqualTo("bar");
    }

    @Test
    void inboundName() {
        Item one = getTestItemBuilder("foo", "bar").withName("huhu").build();
        Item two = getTestItem("foo", "baz");
        Relation relation = RelationFactory.createForTesting(one, two);

        Relation.ApiModel apiModel = new Relation.ApiModel(relation, two);

        assertThat(apiModel).isNotNull();
        assertThat(apiModel.direction).isEqualTo(Relation.ApiModel.INBOUND);
        assertThat(apiModel.name).isEqualTo("huhu");
    }

    @Test
    void getChangesInType() {
        Item b = getTestItem("a", "b");
        Item c = getTestItem("a", "c");
        Relation before = new Relation(b, c, "foo", "JSON", null);
        Relation after = new Relation(b, c, "foo", "JSON", RelationType.PROVIDER);

        //when
        List<String> changes = before.getChanges(after);

        //then
        assertThat(changes).isNotNull().hasSize(1);
        assertThat(changes.get(0)).contains("Type changed ");
    }

    @Test
    void getChangesInFormat() {
        Item b = getTestItem("a", "b");
        Item c = getTestItem("a", "c");
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
        Item b = getTestItem("a", "b");
        Item c = getTestItem("a", "c");
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
        Item b = getTestItem("a", "b");
        Item c = getTestItem("a", "c");
        Relation before = new Relation(b, c, "foo", "JSON", RelationType.PROVIDER);
        Relation after = new Relation(b, c, "foo", "JSON", RelationType.PROVIDER);

        //when
        List<String> changes = before.getChanges(after);

        //then
        assertThat(changes).isNotNull().hasSize(0);
    }
}