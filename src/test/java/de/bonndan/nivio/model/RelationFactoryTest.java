package de.bonndan.nivio.model;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.RelationDescription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RelationFactoryTest {

    private Landscape landscape;

    @BeforeEach
    void setUp() {
        landscape = LandscapeFactory.createForTesting("test", "test").build();
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
        assertThat(relation.getType()).isEqualTo(RelationType.PROVIDER);
        assertThat(relation.getSource()).isEqualTo(foo);
        assertThat(relation.getTarget()).isEqualTo(bar);
    }

    @Test
    void update() {

        //given
        Item foo = ItemFactory.getTestItem("a", "foo");
        Item bar = ItemFactory.getTestItem("b", "bar");
        Relation relation = RelationFactory.createProviderRelation(foo, bar);
        relation.setLabel("foo1", "bar1");
        foo.setRelations(Set.of(relation));
        bar.setRelations(Set.of(relation));
        landscape.setItems(Set.of(foo, bar)); //landscape contains fooCopy instead of foo!

        RelationDescription providerDescription = RelationFactory.createProviderDescription("foo", "bar");
        providerDescription.setFormat("json");
        providerDescription.setDescription("huhu");
        providerDescription.setType(RelationType.DATAFLOW);
        providerDescription.setLabel("foo1", "bar2");
        providerDescription.setLabel("foo2", "bar2");

        //when
        Relation newRelation = RelationFactory.update(relation, providerDescription, landscape);

        //then
        assertThat(newRelation.getSource()).isEqualTo(foo);
        assertThat(newRelation.getTarget()).isEqualTo(bar);
        assertThat(newRelation.getFormat()).isEqualTo("json");
        assertThat(newRelation.getType()).isEqualTo(RelationType.DATAFLOW);
        assertThat(newRelation.getDescription()).isEqualTo("huhu");
        assertThat(newRelation.getLabel("foo1")).isEqualTo("bar2");
        assertThat(newRelation.getLabel("foo2")).isEqualTo("bar2");
    }

    @Test
    void updateThrows() {

        //given
        Item foo = ItemFactory.getTestItem("a", "foo");
        Item bar = ItemFactory.getTestItem("b", "bar");
        Relation relation = RelationFactory.createProviderRelation(foo, bar);
        foo.setRelations(Set.of(relation));
        bar.setRelations(Set.of(relation));
        landscape.setItems(Set.of(foo, bar)); //landscape contains fooCopy instead of foo!

        RelationDescription providerDescription = RelationFactory.createProviderDescription("foo", "oops");

        //when
        assertThrows(NoSuchElementException.class, () -> RelationFactory.update(relation, providerDescription, landscape));
    }

    @Test
    void updateRegardsReference() {

        //given
        Item foo = ItemFactory.getTestItem("a", "foo");
        Item fooCopy = ItemFactory.getTestItem("a", "foo");
        Item bar = ItemFactory.getTestItem("b", "bar");
        Relation relation = RelationFactory.createProviderRelation(foo, bar);
        foo.setRelations(Set.of(relation));
        bar.setRelations(Set.of(relation));
        landscape.setItems(Set.of(fooCopy, bar)); //landscape contains fooCopy instead of foo!

        RelationDescription providerDescription = RelationFactory.createProviderDescription("foo", "bar");

        //when
        Relation newRelation = RelationFactory.update(relation, providerDescription, landscape);

        //then
        assertThat(newRelation.getSource() == fooCopy).isTrue(); //ensuring the item in the landscape is used
        assertThat(newRelation.getTarget()).isEqualTo(bar);
    }

    @Test
    void create() {

        //given
        Item foo = ItemFactory.getTestItem("a", "foo");
        Item bar = ItemFactory.getTestItem("b", "bar");
        landscape.setItems(Set.of(foo, bar));
        RelationDescription relationDescription = RelationFactory.createProviderDescription("foo", "bar");
        relationDescription.setLabel("foo1", "bar1");

        //when
        Relation newRelation = RelationFactory.create(foo, relationDescription, landscape);

        //then
        assertThat(newRelation.getSource()).isEqualTo(foo);
        assertThat(newRelation.getTarget()).isEqualTo(bar);
        assertThat(newRelation.getLabel("foo1")).isEqualTo("bar1");
    }

    @Test
    void createThrows() {

        //given
        Item foo = ItemFactory.getTestItem("a", "foo");
        Item bar = ItemFactory.getTestItem("b", "bar");
        landscape.setItems(Set.of(foo, bar));
        RelationDescription relationDescription = RelationFactory.createProviderDescription("foo", "oops");

        //when
        assertThrows(NoSuchElementException.class, () -> RelationFactory.create(foo, relationDescription, landscape));
    }
}