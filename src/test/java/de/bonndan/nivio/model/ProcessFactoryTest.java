package de.bonndan.nivio.model;

import de.bonndan.nivio.GraphTestSupport;
import de.bonndan.nivio.assessment.Assessment;
import de.bonndan.nivio.input.ProcessingException;
import de.bonndan.nivio.input.dto.BranchDescription;
import de.bonndan.nivio.input.dto.ProcessDescription;
import de.bonndan.nivio.search.LuceneSearchIndex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class ProcessFactoryTest {

    private GraphTestSupport graph;
    private GraphComponent bOne;
    private GraphComponent bTwo;
    private GraphComponent bThree;
    private GraphComponent bFour;
    private GraphComponent bFive;
    private GraphComponent cThree;
    private GraphComponent cOne;
    private GraphComponent cTwo;

    @BeforeEach
    void setUp() {
        graph = new GraphTestSupport(new Index<>(LuceneSearchIndex.createVolatile()));

        bOne = graph.getTestItem(graph.groupB.getIdentifier(), "b-one");
        bTwo = graph.getTestItem(graph.groupB.getIdentifier(), "b-two");
        bThree = graph.getTestItem(graph.groupB.getIdentifier(), "b-three");
        bFour = graph.getTestItem(graph.groupB.getIdentifier(), "b-four");
        bFive = graph.getTestItem(graph.groupB.getIdentifier(), "b-five");

        cOne = graph.getTestItem(graph.groupC.getIdentifier(), "c-one");
        cTwo = graph.getTestItem(graph.groupC.getIdentifier(), "c-two");
        cThree = graph.getTestItem(graph.groupC.getIdentifier(), "c-three");

        graph.landscape.getReadAccess().indexForSearch(Assessment.empty());

    }

    @Test
    void createFromDescription() {

        var dto = new ProcessDescription();
        dto.setIdentifier("fooproc");
        dto.getBranches().add(
                new BranchDescription(List.of(
                        bOne.getFullyQualifiedIdentifier().toString(),
                        bTwo.getFullyQualifiedIdentifier().toString(),
                        bThree.getFullyQualifiedIdentifier().toString()
                ))
        );

        dto.getBranches().add(
                new BranchDescription(List.of(
                        graph.itemAA.getFullyQualifiedIdentifier().toString(),
                        graph.itemAB.getFullyQualifiedIdentifier().toString(),
                        bThree.getFullyQualifiedIdentifier().toString()
                ))
        );
        dto.getBranches().add(
                new BranchDescription(List.of(
                        bThree.getFullyQualifiedIdentifier().toString(),
                        cOne.getFullyQualifiedIdentifier().toString(),
                        cTwo.getFullyQualifiedIdentifier().toString(),
                        cThree.getFullyQualifiedIdentifier().toString()
                ))
        );

        //when
        Process fromDescription = ProcessFactory.INSTANCE.createFromDescription(dto.getIdentifier(), graph.landscape, dto);

        //then
        assertThat(fromDescription).isNotNull();
        assertThat(fromDescription.getIdentifier()).isEqualTo(dto.getIdentifier());
        assertThat(fromDescription.getBranches()).hasSize(dto.getBranches().size());

        long edges = fromDescription.getBranches().get(0).getEdges().size();
        long dtoNodes = dto.getBranches().get(0).getItems().size();
        assertThat(edges).isEqualTo(dtoNodes-1);

        //assert all relations are attached
        fromDescription.getBranches().get(0).getEdges().forEach(relation -> {
            assertThatCode(relation::getSource).doesNotThrowAnyException();
        });
    }

    @Test
    void gap() {

        var dto = new ProcessDescription();
        dto.setIdentifier("fooproc");
        dto.getBranches().add(
                new BranchDescription(List.of(
                        bOne.getFullyQualifiedIdentifier().toString(),
                        bTwo.getFullyQualifiedIdentifier().toString(),
                        bThree.getFullyQualifiedIdentifier().toString()
                ))
        );

        dto.getBranches().add(
                new BranchDescription(List.of(
                        cOne.getFullyQualifiedIdentifier().toString(),
                        cTwo.getFullyQualifiedIdentifier().toString(),
                        cThree.getFullyQualifiedIdentifier().toString()
                ))
        );

        //when
        assertThatThrownBy(() -> ProcessFactory.INSTANCE.createFromDescription(dto.getIdentifier(), graph.landscape, dto))
                .isInstanceOf(ProcessingException.class);

    }
}