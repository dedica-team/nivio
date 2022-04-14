package de.bonndan.nivio.model;

import de.bonndan.nivio.GraphTestSupport;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.RelationDescription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RelationFactoryTest {

    private GraphTestSupport graph;

    @BeforeEach
    void setUp() {
        graph = new GraphTestSupport();
    }

    @Test
    void createProviderDescription() {
        //when
        RelationDescription providerDescription = RelationFactory.createProviderDescription("foo", "bar");

        //then
        assertThat(providerDescription).isNotNull();
        assertThat(providerDescription.getType()).isEqualTo(RelationType.PROVIDER);
        assertThat(providerDescription.getSource()).isEqualTo("foo");
        assertThat(providerDescription.getTarget()).isEqualTo("bar");
    }

    @Test
    void createDataflowDescription() {
        //given
        ItemDescription desc = new ItemDescription("foo");

        //when
        RelationDescription providerDescription = RelationFactory.createDataflowDescription(desc, "bar");

        //then
        assertThat(providerDescription).isNotNull();
        assertThat(providerDescription.getType()).isEqualTo(RelationType.DATAFLOW);
        assertThat(providerDescription.getSource()).isEqualTo("foo");
        assertThat(providerDescription.getTarget()).isEqualTo("bar");
    }

    @Test
    void createProviderRelation() {

        //given
        Item foo = ItemFactory.getTestItem("a", "foo");
        Item bar = ItemFactory.getTestItem("b", "bar");

        //when
        Relation relation = RelationFactory.createProviderRelation(foo, bar);

        //then
        assertThat(relation).isNotNull();
        assertThat(relation.getType()).isEqualTo(RelationType.PROVIDER.name());
        assertThat(relation.getSourceURI()).isEqualTo(foo.getFullyQualifiedIdentifier());
        assertThat(relation.getTargetURI()).isEqualTo(bar.getFullyQualifiedIdentifier());
    }

    @Test
    void update() {

        //given
        Item foo = graph.getTestItem("a", "foo");
        Item bar = graph.getTestItem("b", "bar");
        Relation relation = RelationFactory.createProviderRelation(foo, bar);
        relation.setLabel("foo1", "bar1");
        graph.landscape.getWriteAccess().addOrReplaceRelation(relation);

        RelationDescription providerDescription = RelationFactory.createProviderDescription("foo", "bar");
        providerDescription.setFormat("json");
        providerDescription.setDescription("huhu");
        providerDescription.setType(RelationType.DATAFLOW);
        providerDescription.setLabel("foo1", "bar2");
        providerDescription.setLabel("foo2", "bar2");

        //when
        Relation newRelation = RelationFactory.update(relation, providerDescription, foo, bar);

        //then
        assertThat(newRelation.getSourceURI()).isEqualTo(foo.getFullyQualifiedIdentifier());
        assertThat(newRelation.getTargetURI()).isEqualTo(bar.getFullyQualifiedIdentifier());
        assertThat(newRelation.getFormat()).isEqualTo("json");
        assertThat(newRelation.getType()).isEqualTo(RelationType.DATAFLOW.name());
        assertThat(newRelation.getDescription()).isEqualTo("huhu");
        assertThat(newRelation.getLabel("foo1")).isEqualTo("bar2");
        assertThat(newRelation.getLabel("foo2")).isEqualTo("bar2");
    }

    @Test
    void updateRegardsReference() {

        //given
        Item foo = graph.getTestItem("a", "foo");

        Item bar = graph.getTestItem("b", "bar");
        Relation relation = RelationFactory.createProviderRelation(foo, bar);
        graph.landscape.getWriteAccess().addOrReplaceRelation(relation);

        RelationDescription providerDescription = RelationFactory.createProviderDescription("foo", "bar");

        //when
        Item fooCopy = graph.getTestItem("a", "foo"); //now foo is detached
        assertThrows(IllegalArgumentException.class, () -> RelationFactory.update(relation, providerDescription, foo, bar));
    }

    @Test
    void create() {

        //given
        Item foo = graph.getTestItem("a", "foo");
        Item bar = graph.getTestItem("b", "bar");
        RelationDescription relationDescription = RelationFactory.createProviderDescription("foo", "bar");
        relationDescription.setLabel("foo1", "bar1");

        //when
        Relation newRelation = RelationFactory.create(foo, bar, relationDescription);

        //then
        assertThat(newRelation.getSourceURI()).isEqualTo(foo.getFullyQualifiedIdentifier());
        assertThat(newRelation.getTargetURI()).isEqualTo(bar.getFullyQualifiedIdentifier());
        assertThat(newRelation.getLabel("foo1")).isEqualTo("bar1");
    }
}