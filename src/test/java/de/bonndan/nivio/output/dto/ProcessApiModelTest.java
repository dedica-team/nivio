package de.bonndan.nivio.output.dto;

import de.bonndan.nivio.GraphTestSupport;
import de.bonndan.nivio.model.Branch;
import de.bonndan.nivio.model.Process;
import de.bonndan.nivio.model.Relation;
import de.bonndan.nivio.model.RelationFactory;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProcessApiModelTest {

    @Test
    void getBranches() {

        //given
        var graph = new GraphTestSupport();
        List<Branch> branches = new ArrayList<>();
        Relation relation1 = RelationFactory.createForTesting(graph.itemAA, graph.itemAB);
        Relation relation2 = RelationFactory.createForTesting(graph.itemAB, graph.itemAC);
        branches.add(new Branch(List.of(relation1, relation2)));
        Process process = new Process("foo", "bar", "baz", null, null, null, branches, graph.landscape);

        //when
        ProcessApiModel processApiModel = new ProcessApiModel(process);

        //then
        List<List<URI>> apiBranches = processApiModel.getBranches();
        assertThat(apiBranches).hasSize(process.getBranches().size());

        List<URI> branch = apiBranches.get(0);
        assertThat(branch).isEqualTo(List.of(relation1.getFullyQualifiedIdentifier(), relation2.getFullyQualifiedIdentifier()));
    }
}