package de.bonndan.nivio.model;

import de.bonndan.nivio.GraphTestSupport;
import de.bonndan.nivio.assessment.Assessment;
import de.bonndan.nivio.search.LuceneSearchIndex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FlexSearchTest {

    private GraphTestSupport graph;
    private FlexSearch<GraphComponent, Item> flexSearch;

    @BeforeEach
    void setUp() {
        graph = new GraphTestSupport(new Index<>(LuceneSearchIndex.createVolatile()));
        graph.indexForSearch(Assessment.empty());

        flexSearch = FlexSearch.forClassOn(Item.class, graph.landscape.getReadAccess());
    }

    @Test
    void matchOrSearchByQueryWithName() {

        //when
        var items = flexSearch.search("name:" + graph.itemAA.getName() );

        //then
        assertThat(items).isNotNull().hasSize(1).contains(graph.itemAA);
    }

    @Test
    void matchOrSearchByName() {

        //when
        var items = flexSearch.search(graph.itemAA.getName());

        //then
        assertThat(items).isNotNull().hasSize(1).contains(graph.itemAA);
    }

    @Test
    void matchOrSearchByIdentifier() {

        //when
        var items = flexSearch.search(graph.itemAA.getIdentifier());

        //then
        assertThat(items).isNotNull().hasSize(1).contains(graph.itemAA);
    }

    @Test
    void matchOrSearchByPath() {

        //when
        var items = flexSearch.search(graph.itemAA.getParentIdentifier() + "/" + graph.itemAA.getIdentifier());

        //then
        assertThat(items).isNotNull().hasSize(1).contains(graph.itemAA);
    }
}