package de.bonndan.nivio.input;

import de.bonndan.nivio.GraphTestSupport;
import de.bonndan.nivio.assessment.Assessment;
import de.bonndan.nivio.model.Index;
import de.bonndan.nivio.model.RelationType;
import de.bonndan.nivio.search.LuceneSearchIndex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class HintFactoryTest {

    private HintFactory hintFactory;
    private GraphTestSupport graph;

    @BeforeEach
    void setUp() {
        graph = new GraphTestSupport(new Index<>(LuceneSearchIndex.createVolatile()));
        hintFactory = new HintFactory();
    }

    @Test
    void createWithMysqlURI() {

        //given
        graph.itemAA.setLabel("foo", "mysql://somehost/abc");

        var target = graph.getTestItemBuilder(graph.groupA.getIdentifier(), "someId")
                .withAddress(URI.create("mysql://somehost/abc"))
                .withParent(graph.groupA)
                .build();
        graph.landscape.getIndexWriteAccess().addOrReplaceChild(target);
        graph.landscape.getIndexReadAccess().indexForSearch(Assessment.empty());

        //when
        Optional<Hint> foo = hintFactory.createForLabel(graph.landscape.getIndexReadAccess(), graph.itemAA, "foo");

        //then
        assertThat(foo).isNotEmpty();
        assertThat(foo.get().getRelationType()).isEqualTo(RelationType.PROVIDER);
        assertThat(foo.get().getTarget()).isEqualTo(target.getFullyQualifiedIdentifier());
        assertThat(foo.get().getTargetType()).isEqualTo(ItemType.DATABASE);
    }

    @Test
    void createWithHttpURI() {

        //given
        graph.itemAA.setLabel("foo", "http://foo.bar.baz");

        var target = graph.getTestItemBuilder(graph.groupA.getIdentifier(), "foo")
                .withAddress(URI.create("http://foo.bar.baz"))
                .withParent(graph.groupA)
                .build();
        graph.landscape.getIndexWriteAccess().addOrReplaceChild(target);
        graph.landscape.getIndexReadAccess().indexForSearch(Assessment.empty());

        //when
        Optional<Hint> foo = hintFactory.createForLabel(graph.landscape.getIndexReadAccess(), graph.itemAA, "foo");

        //then
        assertThat(foo).isNotEmpty();
        assertThat(foo.get().getRelationType()).isEqualTo(RelationType.DATAFLOW);
        assertThat(foo.get().getTarget()).isEqualTo(target.getFullyQualifiedIdentifier());
    }

    @Test
    @DisplayName("links with identifier")
    void linksByIdentifier() {

        //given
        var target = graph.getTestItemBuilder(graph.groupA.getIdentifier(), "foo")
                .withParent(graph.groupA)
                .build();
        graph.landscape.getIndexWriteAccess().addOrReplaceChild(target);
        graph.itemAA.setLabel("BASE_URL", target.getIdentifier());
        graph.landscape.getIndexReadAccess().indexForSearch(Assessment.empty());

        //when
        Optional<Hint> foo = hintFactory.createForLabel(graph.landscape.getIndexReadAccess(), graph.itemAA, "BASE_URL");

        //then
        assertThat(foo).isNotEmpty();
        assertThat(foo.get().getTarget()).isEqualTo(target.getFullyQualifiedIdentifier());
    }

    @Test
    @DisplayName("label points to a name")
    void linksByName() {
        //given
        var target = graph.getTestItemBuilder(graph.groupA.getIdentifier(), "foo")
                .withParent(graph.groupA)
                .withName("aName")
                .build();
        graph.landscape.getIndexWriteAccess().addOrReplaceChild(target);
        graph.itemAA.setLabel("BASE_URL", target.getName());
        graph.landscape.getIndexReadAccess().indexForSearch(Assessment.empty());

        //when
        Optional<Hint> foo = hintFactory.createForLabel(graph.landscape.getIndexReadAccess(), graph.itemAA, "BASE_URL");

        //then
        assertThat(foo).isNotEmpty();
        assertThat(foo.get().getTarget()).isEqualTo(target.getFullyQualifiedIdentifier());
    }

    @Test
    @DisplayName("label points to a name but key contains no special word")
    void linksNotByName() {
        //given
        var target = graph.getTestItemBuilder(graph.groupA.getIdentifier(), "foo")
                .withName("aName")
                .withParent(graph.groupA)
                .build();
        graph.landscape.getIndexWriteAccess().addOrReplaceChild(target);
        graph.itemAA.setLabel("FOO", target.getName());
        graph.landscape.getIndexReadAccess().indexForSearch(Assessment.empty());

        //when
        Optional<Hint> foo = hintFactory.createForLabel(graph.landscape.getIndexReadAccess(), graph.itemAA, "foo");

        //then
        assertThat(foo).isEmpty();
    }

    @Test
    @DisplayName("does not link same item to itself")
    void doesNotLinkSame() {
        //given
        graph.itemAA.setLabel("BASE_URL", graph.itemAA.getName());
        graph.landscape.getIndexReadAccess().indexForSearch(Assessment.empty());

        //when
        Optional<Hint> foo = hintFactory.createForLabel(graph.landscape.getIndexReadAccess(), graph.itemAA, "foo");

        //then
        assertThat(foo).isEmpty();
    }

    @Test
    @DisplayName("does nothing with more than one match")
    void ifUncertainDoesNotLink() {
        //given
        String aName = "aName";
        var target1 = graph.getTestItemBuilder(graph.groupA.getIdentifier(), "foo1")
                .withParent(graph.groupA)
                .withName(aName)
                .build();
        graph.landscape.getIndexWriteAccess().addOrReplaceChild(target1);

        var target2 = graph.getTestItemBuilder(graph.groupA.getIdentifier(), "foo2")
                .withParent(graph.groupA)
                .withName(aName)
                .build();
        graph.landscape.getIndexWriteAccess().addOrReplaceChild(target2);

        graph.itemAA.setLabel("BASE_URL", aName);
        graph.landscape.getIndexReadAccess().indexForSearch(Assessment.empty());

        //when
        Optional<Hint> foo = hintFactory.createForLabel(graph.landscape.getIndexReadAccess(), graph.itemAA, "BASE_URL");

        //then
        assertThat(foo).isEmpty();
    }
}