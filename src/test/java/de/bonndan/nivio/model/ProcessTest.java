package de.bonndan.nivio.model;

import de.bonndan.nivio.GraphTestSupport;
import de.bonndan.nivio.assessment.Assessable;
import de.bonndan.nivio.input.ProcessMerger;
import de.bonndan.nivio.search.LuceneSearchIndex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

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
        Relation relation1 = RelationFactory.create(graph.itemAA, graph.itemAB);
        Relation relation2 = RelationFactory.create(graph.itemAB, graph.itemAC);
        branches.add(new Branch(List.of(relation1.getFullyQualifiedIdentifier(), relation2.getFullyQualifiedIdentifier())));

        Relation relation3 = RelationFactory.create(graph.getTestItem(graph.groupB.getIdentifier(), "foo"), graph.itemAC);
        branches.add(new Branch(List.of(relation3.getFullyQualifiedIdentifier())));
        var process = getProcess(branches);

        //when
        Set<Assessable> assessables = process.getAssessables();

        //then
        assertThat(assessables).containsAll(List.of(relation1, relation2, relation3));
    }

    @Test
    void containsRelation() {
        //given
        List<Branch> branches = new ArrayList<>();
        Relation relation1 = RelationFactory.create(graph.itemAA, graph.itemAB);
        Relation relation2 = RelationFactory.create(graph.itemAB, graph.itemAC);
        branches.add(new Branch(List.of(relation1.getFullyQualifiedIdentifier(), relation2.getFullyQualifiedIdentifier())));
        var process = getProcess(branches);

        Relation relation3 = RelationFactory.create(graph.getTestItem(graph.groupB.getIdentifier(), "foo"), graph.itemAC);

        //then
        assertThat(process.contains(relation1)).isTrue();
        assertThat(process.contains(relation2)).isTrue();
        assertThat(process.contains(relation3)).isFalse();
    }

    @Test
    void mergerAssigns() {
        //given
        List<Branch> branches = new ArrayList<>();
        Relation relation1 = RelationFactory.create(graph.itemAA, graph.itemAB);
        graph.landscape.getWriteAccess().addOrReplaceRelation(relation1);
        Relation relation2 = RelationFactory.create(graph.itemAB, graph.itemAC);
        graph.landscape.getWriteAccess().addOrReplaceRelation(relation2);
        branches.add(new Branch(List.of(relation1.getFullyQualifiedIdentifier(), relation2.getFullyQualifiedIdentifier())));
        var process = getProcess(branches);

        Relation relation3 = RelationFactory.create(graph.getTestItem(graph.groupB.getIdentifier(), "foo"), graph.itemAC);
        graph.landscape.getWriteAccess().addOrReplaceRelation(relation3);

        //when
        //ProcessMerger.assignRelations

        //then
        assertThat(relation1.getProcesses()).contains(process.getFullyQualifiedIdentifier());
        assertThat(relation2.getProcesses()).contains(process.getFullyQualifiedIdentifier());
        assertThat(relation3.getProcesses()).doesNotContain(process.getFullyQualifiedIdentifier());
    }

    Process getProcess(List<Branch> branches) {
        Process process = new Process("foo", "bar", "baz", null, null, null, branches, graph.landscape);
        graph.landscape.getWriteAccess().addOrReplaceChild(process);
        ProcessMerger.assignRelations(process, graph.landscape);
        return process;
    }
}