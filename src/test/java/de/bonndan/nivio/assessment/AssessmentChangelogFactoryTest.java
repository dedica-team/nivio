package de.bonndan.nivio.assessment;

import de.bonndan.nivio.input.ProcessingChangelog;
import de.bonndan.nivio.model.ItemFactory;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.model.LandscapeFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class AssessmentChangelogFactoryTest {

    public static final String TEST_BAR_BAZ = "test/bar/baz";
    private Landscape landscape;
    private Assessment assessment;

    @BeforeEach
    void setUp() {
        landscape = LandscapeFactory.createForTesting("test", "test")
                .withItems(Set.of(ItemFactory.getTestItem("bar", "baz")))
                .build();
        List<StatusValue> security = Collections.singletonList(new StatusValue(TEST_BAR_BAZ, "security", Status.GREEN, null));
        assessment = new Assessment(Map.of(TEST_BAR_BAZ, List.of(StatusValue.summary(TEST_BAR_BAZ, Collections.singletonList(StatusValue.summary("test/bar/baz", security))))));
    }

    @Test
    void entryCreated() {

        //when
        ProcessingChangelog changes = AssessmentChangelogFactory.getChanges(landscape, assessment);

        //then
        assertThat(changes).isNotNull();
        assertThat(changes.getChanges()).isNotNull().isNotEmpty().containsKey(TEST_BAR_BAZ);
        ProcessingChangelog.Entry entry = changes.getChanges().get(TEST_BAR_BAZ);
        assertThat(entry).isNotNull();
        assertThat(entry.getChangeType()).isEqualTo(ProcessingChangelog.ChangeType.CREATED.name());
    }

    @Test
    @DisplayName("Change from green to red")
    void entryUpdated() {

        List<StatusValue> security = Collections.singletonList(new StatusValue(TEST_BAR_BAZ, "security", Status.RED, null));
        var update = new Assessment(Map.of(TEST_BAR_BAZ, List.of(StatusValue.summary(TEST_BAR_BAZ,
                Collections.singletonList(StatusValue.summary("test/bar/baz", security)))))
        );

        //when
        ProcessingChangelog changes = AssessmentChangelogFactory.getChanges(landscape, assessment, update);

        //then
        assertThat(changes).isNotNull();
        assertThat(changes.getChanges()).isNotNull().isNotEmpty().containsKey(TEST_BAR_BAZ);
        ProcessingChangelog.Entry entry = changes.getChanges().get(TEST_BAR_BAZ);
        assertThat(entry).isNotNull();
        assertThat(entry.getChangeType()).isEqualTo(ProcessingChangelog.ChangeType.UPDATED.name());
        assertThat(entry.getMessages()).isNotEmpty().contains("summary status has changed to red");
    }

    @Test
    @DisplayName("new field assessed")
    void entryUpdatedWithField() {

        List<StatusValue> list = new ArrayList<>();
        list.add(new StatusValue(TEST_BAR_BAZ, "security", Status.RED, null));
        list.add(new StatusValue(TEST_BAR_BAZ, "stability", Status.ORANGE, null));
        var update = new Assessment(Map.of(TEST_BAR_BAZ, List.of(StatusValue.summary(TEST_BAR_BAZ,
                Collections.singletonList(StatusValue.summary("test/bar/baz", list)))))
        );

        //when
        ProcessingChangelog changes = AssessmentChangelogFactory.getChanges(landscape, assessment, update);

        //then
        assertThat(changes).isNotNull();
        assertThat(changes.getChanges()).isNotNull().isNotEmpty().containsKey(TEST_BAR_BAZ);
        ProcessingChangelog.Entry entry = changes.getChanges().get(TEST_BAR_BAZ);
        assertThat(entry).isNotNull();
        assertThat(entry.getChangeType()).isEqualTo(ProcessingChangelog.ChangeType.UPDATED.name());
        assertThat(entry.getMessages()).isNotEmpty().contains("summary status has changed to red");
    }

    @Test
    void entryDeleted() {

        String identifier = "something/different/baz";
        var update = new Assessment(Map.of(identifier, List.of(StatusValue.summary(identifier,
                Collections.singletonList(StatusValue.summary(identifier, new ArrayList<>())))))
        );

        //when
        ProcessingChangelog changes = AssessmentChangelogFactory.getChanges(landscape, assessment, update);

        //then
        assertThat(changes).isNotNull();
        assertThat(changes.getChanges()).isNotNull().isNotEmpty().containsKey(TEST_BAR_BAZ);
        ProcessingChangelog.Entry entry = changes.getChanges().get(TEST_BAR_BAZ);
        assertThat(entry).isNotNull();
        assertThat(entry.getChangeType()).isEqualTo(ProcessingChangelog.ChangeType.DELETED.name());
    }
}