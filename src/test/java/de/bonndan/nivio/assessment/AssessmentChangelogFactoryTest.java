package de.bonndan.nivio.assessment;

import de.bonndan.nivio.GraphTestSupport;
import de.bonndan.nivio.input.ProcessingChangelog;
import de.bonndan.nivio.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class AssessmentChangelogFactoryTest {

    private URI TEST_FQI;
    private Assessment assessment;
    private GraphTestSupport graph;

    @BeforeEach
    void setUp() {
        graph = new GraphTestSupport();
        TEST_FQI = graph.itemAB.getFullyQualifiedIdentifier();

        List<StatusValue> security = Collections.singletonList(new StatusValue(TEST_FQI, "security", Status.GREEN, null));
        assessment = new Assessment(Map.of(TEST_FQI, List.of(StatusValue.summary(TEST_FQI, Collections.singletonList(StatusValue.summary(TEST_FQI, security))))));
    }

    @Test
    void entryCreated() {

        //when
        ProcessingChangelog changes = AssessmentChangelogFactory.getChanges(graph.landscape, assessment);

        //then
        assertThat(changes).isNotNull();
        assertThat(changes.getChanges()).isNotNull().isNotEmpty().containsKey(TEST_FQI);
        ProcessingChangelog.Entry entry = changes.getChanges().get(TEST_FQI);
        assertThat(entry).isNotNull();
        assertThat(entry.getChangeType()).isEqualTo(ProcessingChangelog.ChangeType.CREATED.name());
    }

    @Test
    @DisplayName("Change from green to red")
    void entryUpdated() {

        List<StatusValue> security = Collections.singletonList(new StatusValue(TEST_FQI, "security", Status.RED, null));
        var update = new Assessment(Map.of(TEST_FQI, List.of(StatusValue.summary(TEST_FQI,
                Collections.singletonList(StatusValue.summary(TEST_FQI, security))))));

        //when
        ProcessingChangelog changes = AssessmentChangelogFactory.getChanges(graph.landscape, assessment, update);

        //then
        assertThat(changes).isNotNull();
        assertThat(changes.getChanges()).isNotNull().isNotEmpty().containsKey(TEST_FQI);
        ProcessingChangelog.Entry entry = changes.getChanges().get(TEST_FQI);
        assertThat(entry).isNotNull();
        assertThat(entry.getChangeType()).isEqualTo(ProcessingChangelog.ChangeType.UPDATED.name());
        assertThat(entry.getMessages()).isNotEmpty().contains("summary status has changed to red");
    }

    @Test
    @DisplayName("new field assessed")
    void entryUpdatedWithField() {

        List<StatusValue> list = new ArrayList<>();
        list.add(new StatusValue(TEST_FQI, "security", Status.RED, null));
        list.add(new StatusValue(TEST_FQI, "stability", Status.ORANGE, null));
        var update = new Assessment(Map.of(TEST_FQI, List.of(StatusValue.summary(TEST_FQI,
                Collections.singletonList(StatusValue.summary(TEST_FQI, list)))))
        );

        //when
        ProcessingChangelog changes = AssessmentChangelogFactory.getChanges(graph.landscape, assessment, update);

        //then
        assertThat(changes).isNotNull();
        assertThat(changes.getChanges()).isNotNull().isNotEmpty().containsKey(TEST_FQI);
        ProcessingChangelog.Entry entry = changes.getChanges().get(TEST_FQI);
        assertThat(entry).isNotNull();
        assertThat(entry.getChangeType()).isEqualTo(ProcessingChangelog.ChangeType.UPDATED.name());
        assertThat(entry.getMessages()).isNotEmpty().contains("summary status has changed to red");
    }

    @Test
    void entryDeleted() {

        URI identifier = FullyQualifiedIdentifier.build(ComponentClass.item, "something", null, null, "different", "baz");
        var update = new Assessment(Map.of(identifier, List.of(StatusValue.summary(identifier,
                Collections.singletonList(StatusValue.summary(identifier, new ArrayList<>())))))
        );

        //when
        ProcessingChangelog changes = AssessmentChangelogFactory.getChanges(graph.landscape, assessment, update);

        //then
        assertThat(changes).isNotNull();
        assertThat(changes.getChanges()).isNotNull().isNotEmpty().containsKey(TEST_FQI);
        ProcessingChangelog.Entry entry = changes.getChanges().get(TEST_FQI);
        assertThat(entry).isNotNull();
        assertThat(entry.getChangeType()).isEqualTo(ProcessingChangelog.ChangeType.DELETED.name());
    }
}