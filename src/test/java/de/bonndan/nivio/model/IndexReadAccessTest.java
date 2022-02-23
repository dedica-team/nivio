package de.bonndan.nivio.model;

import de.bonndan.nivio.GraphTestSupport;
import de.bonndan.nivio.assessment.Assessment;
import de.bonndan.nivio.search.LuceneSearchIndex;
import de.bonndan.nivio.search.SearchField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IndexReadAccessTest {

    private GraphTestSupport graph;
    private IndexReadAccess<GraphComponent> access;

    @BeforeEach
    void setUp() {
        graph = new GraphTestSupport(new Index<>(LuceneSearchIndex.createVolatile()));
        graph.indexForSearch(Assessment.empty());
        access = graph.landscape.getIndexReadAccess();
    }

    @Test
    @DisplayName("findOne group param is not taken into concern when match is clear")
    void findOneWithoutGroup() {

        //when
        Item foo1 = access.matchOneByIdentifiers(graph.itemAA.getIdentifier(), null, Item.class).orElseThrow();

        //then
        assertThat(foo1).isNotNull();
    }

    @Test
    @DisplayName("findOne group param is not taken into concern when match is clear")
    void findOneWithGroup() {

        //when
        Optional<Item> foo1 = access.matchOneByIdentifiers(graph.itemAA.getIdentifier(), graph.itemAA.getParentIdentifier(), Item.class);

        //then
        assertThat(foo1).isPresent();
    }

    @Test
    @DisplayName("findOne group param is taken into concern when match is unclear")
    void findOneWithGroupWithSimilarIdItems() {

        Group foo = graph.getTestGroup("foo");
        graph.getTestItem(foo.getIdentifier(), graph.itemAA.getIdentifier());

        //when
        Optional<Item> foo1 = access.matchOneByIdentifiers(graph.itemAA.getIdentifier(), graph.groupA.getIdentifier(), Item.class);

        //then
        assertThat(foo1).isPresent();
        assertThat(foo1.get().getParentIdentifier()).isEqualTo(graph.groupA.getIdentifier());
    }

    @Test
    @DisplayName("findOne picks item from group a when ambiguous")
    void findOneAmbiguousA() {

        //given
        Item inOtherGroup = graph.getTestItemBuilder(graph.groupB.getIdentifier(), graph.itemAA.getIdentifier())
                .withParent(graph.groupB)
                .build();
        graph.landscape.getIndexWriteAccess().addOrReplaceChild(inOtherGroup);

        //when
        Item resultA = access.matchOneByIdentifiers(graph.itemAA.getIdentifier(), "a", Item.class).orElseThrow();
        Item resultB = access.matchOneByIdentifiers(graph.itemAA.getIdentifier(), "b", Item.class).orElseThrow();

        //then
        assertThat(resultA).isNotNull();
        assertThat(resultA.getParent().getIdentifier()).isEqualTo("a");

        assertThat(resultB).isNotNull().isEqualTo(inOtherGroup);
        assertThat(resultB.getParent().getIdentifier()).isEqualTo("b");
    }

    @Test
    void findOneIsEmpty() {

        //when
        assertThat(access.matchOneByIdentifiers("bar", "b", Item.class)).isEmpty();
    }

    @Test
    @DisplayName("findOne throw if ambiguous and group is not matching")
    void findOneThrowsIfUnclear() {

        //given
        Item inOtherGroup = graph.getTestItemBuilder(graph.groupB.getIdentifier(), graph.itemAA.getIdentifier())
                .withParent(graph.groupB)
                .build();
        graph.landscape.getIndexWriteAccess().addOrReplaceChild(inOtherGroup);

        //when
        assertThrows(NoSuchElementException.class, () -> access.matchOneByIdentifiers(graph.itemAA.getIdentifier(), null, Item.class));
    }

    @Test
    void searchIdentifierStartingWithWildcard() {

        //given
        graph.landscape.getIndexWriteAccess().addOrReplaceChild(
                graph.getTestItemBuilder(graph.groupA.getIdentifier(), "hihi")
                        .withName("foo.bar")
                        .build()
        );
        graph.indexForSearch(Assessment.empty());

        //when
        List<Item> search = access.search("*oo", Item.class);

        //then
        assertEquals(1, search.size());
    }

    @Test
    @DisplayName("identifier are indexed in string fields")
    void searchIdentifier() {

        //given
        var item = graph.getTestItem(graph.groupA.getIdentifier(), "some-thing-1234");
        graph.indexForSearch(Assessment.empty());

        //when
        List<Item> search = access.search("some-thing-1234", Item.class);
        assertThat(search).hasSize(1).contains(item);

        search = access.search("some-thing-12*", Item.class);
        assertThat(search).hasSize(1).contains(item);

        search = access.search("some-thing-*", Item.class);
        assertThat(search).hasSize(1).contains(item);

        search = access.search(SearchField.IDENTIFIER.getValue() + ":some-thing-*", Item.class);
        assertThat(search).hasSize(1).contains(item);

        search = access.search(SearchField.IDENTIFIER.getValue() + ":some-thing-12*", Item.class);
        assertThat(search).hasSize(1).contains(item);

        search = access.search(SearchField.IDENTIFIER.getValue() + ":some-thing-1234", Item.class);
        assertThat(search).hasSize(1).contains(item);
    }

    @Test
    @DisplayName("parent identifier are indexed in string fields")
    void searchParentIdentifier() {

        //given
        var group = graph.getTestGroup("test-123");
        var item = graph.getTestItem(group.getIdentifier(), "foo");
        graph.indexForSearch(Assessment.empty());

        //when
        List<Item> search = access.search(SearchField.PARENT_IDENTIFIER.getValue() + ":test-123", Item.class);
        assertThat(search).hasSize(1).contains(item);

        //when
        search = access.search(SearchField.PARENT_IDENTIFIER.getValue() + ":test-12*", Item.class);
        assertThat(search).hasSize(1).contains(item);
    }

    @Test
    void queryUrl() {
        //given
        graph.landscape.getIndexWriteAccess().addOrReplaceChild(
                graph.getTestItemBuilder(graph.groupA.getIdentifier(), "hasaddress")
                        .withParent(graph.groupA)
                        .withAddress(URI.create("https://foo.bar/"))
                        .build()
        );
        graph.indexForSearch(Assessment.empty());

        //when
        Collection<Item> search = access.searchAddress("https://foo.bar/", Item.class);

        //then
        assertThat(search).isNotEmpty();
        Item next = search.iterator().next();
        assertThat(next.getIdentifier()).isEqualTo("hasaddress");
    }

    @Test
    void matchOrSearchUsesSearch() {

        //given
        graph.indexForSearch(Assessment.empty());
        //when
        Collection<Item> items = access.matchOrSearchByIdentifierOrName(graph.itemAA.getIdentifier(), Item.class);

        //then
        assertThat(items).isNotNull().hasSize(1).contains(graph.itemAA);
    }

    @Test
    void searchOnFields() {

        //given
        Item newItem = graph.getTestItemBuilder(graph.groupA.getIdentifier(), "hasaddress")
                .withParent(graph.groupA)
                .withDescription("foobar")
                .build();
        graph.landscape.getIndexWriteAccess().addOrReplaceChild(newItem);
        graph.indexForSearch(Assessment.empty());

        //when
        Collection<Item> items = access.search("foobar", Item.class);

        //then
        assertThat(items).isNotNull().hasSize(1).contains(newItem);
    }

    @Test
    void matchOrSearchByPath() {

        //when
        Collection<Item> items = access.matchOrSearchByIdentifierOrName(graph.itemAA.getParentIdentifier() + "/" + graph.itemAA.getIdentifier(), Item.class);

        //then
        assertThat(items).isNotNull().hasSize(1).contains(graph.itemAA);
    }
}