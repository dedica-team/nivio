package de.bonndan.nivio.model;

import de.bonndan.nivio.GraphTestSupport;
import de.bonndan.nivio.assessment.Assessable;
import de.bonndan.nivio.search.LuceneSearchIndex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ProcessTest {

    private GraphTestSupport graph;

    @BeforeEach
    void setUp() {
        graph = new GraphTestSupport(new Index<>(LuceneSearchIndex.createVolatile()));
    }

    @Test
    void getAssessables() {

        //given

        List<Branch> branches = new ArrayList<>();
        Relation relation1 = RelationFactory.createForTesting(graph.itemAA, graph.itemAB);
        Relation relation2 = RelationFactory.createForTesting(graph.itemAB, graph.itemAC);
        branches.add(new Branch(List.of(relation1, relation2)));

        Relation relation3 = RelationFactory.createForTesting(graph.getTestItem(graph.groupB.getIdentifier(), "foo"), graph.itemAC);
        branches.add(new Branch(List.of(relation3)));

        Process process = new Process("foo", "bar", "baz", null, null, null, branches, graph.landscape);

        //when
        Set<Assessable> assessables = process.getAssessables();

        //then
        assertThat(assessables).containsAll(List.of(relation1, relation2,relation3));
    }
}