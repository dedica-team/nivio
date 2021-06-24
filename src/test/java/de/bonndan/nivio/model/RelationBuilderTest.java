package de.bonndan.nivio.model;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.RelationDescription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RelationBuilderTest {

    private Landscape landscape;

    @BeforeEach
    void setUp() {
        landscape = LandscapeFactory.createForTesting("test", "test").build();
    }

    @Test
    void createProviderDescription() {
        //when
        RelationDescription providerDescription = RelationBuilder.createProviderDescription("foo", "bar");

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
        RelationDescription providerDescription = RelationBuilder.createDataflowDescription(desc, "bar");

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
        Relation relation = RelationBuilder.createProviderRelation(foo, bar);

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
        Relation relation = RelationBuilder.createProviderRelation(foo, bar);
        foo.setRelations(Set.of(relation));
        bar.setRelations(Set.of(relation));
        landscape.setItems(Set.of(foo, bar)); //landscape contains fooCopy instead of foo!

        RelationDescription providerDescription = RelationBuilder.createProviderDescription("foo", "bar");
        providerDescription.setFormat("json");
        providerDescription.setDescription("huhu");

        //when
        Relation newRelation = RelationBuilder.update(relation, providerDescription, landscape);

        //then
        assertThat(newRelation.getSource()).isEqualTo(foo);
        assertThat(newRelation.getTarget()).isEqualTo(bar);
        assertThat(newRelation.getFormat()).isEqualTo("json");
        assertThat(newRelation.getDescription()).isEqualTo("huhu");
    }

    @Test
    void updateThrows() {

        //given
        Item foo = ItemFactory.getTestItem("a", "foo");
        Item bar = ItemFactory.getTestItem("b", "bar");
        Relation relation = RelationBuilder.createProviderRelation(foo, bar);
        foo.setRelations(Set.of(relation));
        bar.setRelations(Set.of(relation));
        landscape.setItems(Set.of(foo, bar)); //landscape contains fooCopy instead of foo!

        RelationDescription providerDescription = RelationBuilder.createProviderDescription("foo", "oops");

        //when
        assertThrows(NoSuchElementException.class, () -> RelationBuilder.update(relation, providerDescription, landscape));
    }

    @Test
    void updateRegardsReference() {

        //given
        Item foo = ItemFactory.getTestItem("a", "foo");
        Item fooCopy = ItemFactory.getTestItem("a", "foo");
        Item bar = ItemFactory.getTestItem("b", "bar");
        Relation relation = RelationBuilder.createProviderRelation(foo, bar);
        foo.setRelations(Set.of(relation));
        bar.setRelations(Set.of(relation));
        landscape.setItems(Set.of(fooCopy, bar)); //landscape contains fooCopy instead of foo!

        RelationDescription providerDescription = RelationBuilder.createProviderDescription("foo", "bar");

        //when
        Relation newRelation = RelationBuilder.update(relation, providerDescription, landscape);

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
        RelationDescription relationDescription = RelationBuilder.createProviderDescription("foo", "bar");

        //when
        Relation newRelation = RelationBuilder.create(foo, relationDescription, landscape);

        //then
        assertThat(newRelation.getSource()).isEqualTo(foo);
        assertThat(newRelation.getTarget()).isEqualTo(bar);
    }

    @Test
    void createThrows() {

        //given
        Item foo = ItemFactory.getTestItem("a", "foo");
        Item bar = ItemFactory.getTestItem("b", "bar");
        landscape.setItems(Set.of(foo, bar));
        RelationDescription relationDescription = RelationBuilder.createProviderDescription("foo", "oops");

        //when
        assertThrows(NoSuchElementException.class, () -> RelationBuilder.create(foo, relationDescription, landscape));
    }
}