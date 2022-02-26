package de.bonndan.nivio.model;

import de.bonndan.nivio.GraphTestSupport;
import de.bonndan.nivio.search.LuceneSearchIndex;
import de.bonndan.nivio.search.NullSearchIndex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class IndexTest {

    private Index<GraphComponent> index;
    private GraphTestSupport graph;
    private Group foo;
    private Group bar;

    @BeforeEach
    void setUp() {
        index = new Index<>(mock(LuceneSearchIndex.class));
        graph = new GraphTestSupport(index);

        foo = graph.getTestGroup("foo");
        bar = graph.getTestGroup("bar");
    }

    @Test
    void getIsEmpty() {

        //when
        Optional<GraphComponent> graphComponent = index.get(URI.create("foo://bar"));

        //then
        assertThat(graphComponent).isEmpty();
    }

    @Test
    void get() {

        //given
        var component = LandscapeFactory.createForTesting("foo", "name").build();
        index.addOrReplace(component);

        //when
        Optional<GraphComponent> graphComponent = index.get(component.getFullyQualifiedIdentifier());

        //then
        assertThat(graphComponent).isPresent().get().isEqualTo(component);
    }

    @Test
    void getRoot() {
        //given
        var component = LandscapeFactory.createForTesting("foo", "name").build();
        index = new Index<>(new NullSearchIndex());
        index.addOrReplace(component);

        //when
        var root = index.getRoot();

        //then
        assertThat(root).isSameAs(component);
    }

    @Test
    void getRootThrows() {

        //given
        index = new Index<>(new NullSearchIndex());

        //when
        assertThrows(NoSuchElementException.class, () -> index.getRoot());
    }

    @Test
    void getChildrenIsEmpty() {

        //given
        var component = LandscapeFactory.createForTesting("foo", "name").build();
        index.addOrReplace(component);

        //when
        List<GraphComponent> children = index.getChildren(component.getFullyQualifiedIdentifier());

        //then
        assertThat(children).isEmpty();
    }

    @Test
    void addReplaces() {
        var foo2 = GroupBuilder.aGroup().withIdentifier("foo").withParent(graph.context).build();

        //when
        index.addOrReplace(foo2);

        //then
        Optional<GraphComponent> actual = index.get(foo.getFullyQualifiedIdentifier());
        assertThat(actual).isNotEmpty();
        assertThat(actual.get() == foo2).isTrue();
    }

    @Test
    void addChildForbidden() {

        var foo = GroupBuilder.aTestGroup("foo").withParent(graph.context).build();
        index.addOrReplace(foo);

        //when
        // landscape is not added

        //then
        assertThrows(IllegalArgumentException.class, () -> index.addOrReplace(RelationFactory.createChild(graph.landscape, foo)));
    }

    @Test
    void getChildren() {

        //given
        var notLinkedAsChild = GroupBuilder.aTestGroup("baz").withParent(graph.context).build();
        index.addOrReplace(notLinkedAsChild);

        //index.addOrReplace(RelationFactory.createChild(graph.context, foo));
        //index.addOrReplace(RelationFactory.createChild(graph.context, bar));

        //when
        List<GraphComponent> children = index.getChildren(graph.context.getFullyQualifiedIdentifier());

        //then
        assertThat(children).hasSize(5).contains(foo).contains(bar).doesNotContain(notLinkedAsChild);
    }

    @Test
    void getRelationsIsEmpty() {
        //when
        Set<Relation> relations = index.getRelations(graph.landscape.getFullyQualifiedIdentifier());

        //then
        assertThat(relations).isEmpty();
    }

    @Test
    void addRelationChecksEnds() {

        //given
        var baz = GroupBuilder.aGroup().withIdentifier("baz").withParent(graph.context).build();

        //when
        //baz is not added
        Relation relation = new Relation(foo, baz, "", "", RelationType.DATAFLOW);

        //then
        assertThrows(IllegalArgumentException.class, () -> index.addOrReplace(relation));
    }

    @Test
    void addRelationReplaces() {

        Relation relation = new Relation(foo, bar, "", "", RelationType.DATAFLOW);
        index.addOrReplace(relation);

        //when
        Relation relation2 = new Relation(foo, bar, "aDescription", "foo", RelationType.DATAFLOW);
        index.addOrReplace(relation2);

        //then
        Set<Relation> relations = index.getRelations(foo.getFullyQualifiedIdentifier());
        assertThat(relations).hasSize(1);
        assertThat(relations.iterator().next()).isEqualTo(relation2);
    }

    @Test
    void getRelations() {

        //when
        Relation relation = new Relation(foo, bar, "", "", RelationType.DATAFLOW);
        Optional<Relation> existing = index.addOrReplace(relation);
        Set<Relation> relationsFoo = index.getRelations(foo.getFullyQualifiedIdentifier());
        Set<Relation> relationsBar = index.getRelations(bar.getFullyQualifiedIdentifier());

        //then
        assertThat(existing).isEmpty();
        assertThat(relationsFoo).isNotEmpty().contains(relation);
        assertThat(relationsBar).isNotEmpty().contains(relation);
    }

    @Test
    void removeNode() {

        Relation relation = new Relation(foo, bar, "", "", RelationType.DATAFLOW);
        index.addOrReplace(relation);

        //should not be affected
        var baz = graph.getTestGroup("baz");

        Relation relation2 = new Relation(bar, baz, "", "", RelationType.DATAFLOW);
        index.addOrReplace(relation2);

        //when foo is removed
        boolean remove = index.removeChild(graph.context, foo);

        //then
        assertThat(remove).isTrue();
        assertThat(index.getRelations(foo.getFullyQualifiedIdentifier())).isEmpty();
        assertThat(index.getRelations(bar.getFullyQualifiedIdentifier())).isNotEmpty().hasSize(1).contains(relation2);
        assertThat(index.getChildren(graph.context.getFullyQualifiedIdentifier()))
                .hasSize(5)
                .contains(bar)
                .contains(baz);
    }


    @Test
    void removeRelation() {

        //given
        Relation relation = new Relation(foo, bar, "", "", RelationType.DATAFLOW);
        index.addOrReplace(relation);

        //should not be affected
        var baz = graph.getTestGroup("baz");

        //when
        index.removeRelation(relation);

        assertThat(index.getRelations(foo.getFullyQualifiedIdentifier())).isEmpty();
        assertThat(index.getRelations(bar.getFullyQualifiedIdentifier())).isEmpty();
    }

    @Test
    void cannotRemoveChildRelation() {

        //given
        var relation = RelationFactory.createChild(graph.context, foo);
        assertThat(index.getRelations(graph.context.getFullyQualifiedIdentifier())).isEmpty();

        //when
        assertThrows(IllegalArgumentException.class, () -> index.removeRelation(relation));

    }

    @Test
    void cannotRemoveParentIfHasChildren() {

        //given
        var landscape = LandscapeFactory.createForTesting("test", "name").build();
        index.addOrReplace(landscape);
        var foo = GroupBuilder.aGroup().withIdentifier("foo").withParent(graph.context).build();
        graph.landscape.getWriteAccess().addOrReplaceChild(foo);
        graph.getTestItem(foo.getIdentifier(), "aItem");

        //when
        assertThrows(IllegalArgumentException.class, () -> index.removeChild(landscape, landscape));
        assertThrows(IllegalArgumentException.class, () -> index.removeChild(foo.getParent(), foo));

    }
}