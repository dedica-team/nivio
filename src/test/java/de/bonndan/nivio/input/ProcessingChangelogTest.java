package de.bonndan.nivio.input;

import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.ItemFactory;
import de.bonndan.nivio.model.Relation;
import de.bonndan.nivio.model.RelationFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ProcessingChangelogTest {

    private ProcessingChangelog changelog;
    private Item testItem;

    @BeforeEach
    void setUp() {
        changelog = new ProcessingChangelog();
        testItem = ItemFactory.getTestItem("a", "b");
    }

    @Test
    void addEntry() {
        //when
        changelog.addEntry(testItem, ProcessingChangelog.ChangeType.CREATED, Collections.singletonList("foo"));

        //then
        Map<String, ProcessingChangelog.Entry> changes = changelog.getChanges();
        assertThat(changes).isNotNull().hasSize(1);

        ProcessingChangelog.Entry actual = changes.get(testItem.getFullyQualifiedIdentifier().jsonValue());
        assertThat(actual).isNotNull();
        assertThat(actual.getChangeType()).isEqualTo(ProcessingChangelog.ChangeType.CREATED.name());
        assertThat(actual.getComponentType()).isEqualTo(testItem.getClass().getSimpleName());
        assertThat(actual.getMessages()).isEqualTo(List.of("foo"));
    }

    @Test
    void testAddEntryWithoutMessage() {
        //when
        changelog.addEntry(testItem, ProcessingChangelog.ChangeType.DELETED);

        //then
        Map<String, ProcessingChangelog.Entry> changes = changelog.getChanges();
        assertThat(changes).isNotNull().hasSize(1);

        //then
        ProcessingChangelog.Entry actual = changes.get(testItem.getFullyQualifiedIdentifier().jsonValue());
        assertThat(actual).isNotNull();
        assertThat(actual.getChangeType()).isEqualTo(ProcessingChangelog.ChangeType.DELETED.name());
        assertThat(actual.getComponentType()).isEqualTo(testItem.getClass().getSimpleName());
        assertThat(actual.getMessages()).isNotNull().isEmpty();
    }

    @Test
    void addRelation() {
        //when
        Item bar = ItemFactory.getTestItem("foo", "bar");
        Relation relation = RelationFactory.createForTesting(this.testItem, bar);
        changelog.addEntry(relation, ProcessingChangelog.ChangeType.DELETED, null);

        //then
        Map<String, ProcessingChangelog.Entry> changes = changelog.getChanges();
        assertThat(changes).isNotNull().hasSize(1);

        //then
        String key = relation.getIdentifier();
        ProcessingChangelog.Entry actual = changes.get(key);
        assertThat(actual).isNotNull();
        assertThat(actual.getChangeType()).isEqualTo(ProcessingChangelog.ChangeType.DELETED.name());
        assertThat(actual.getComponentType()).isEqualTo(relation.getClass().getSimpleName());
        assertThat(actual.getMessages()).isNotNull().isEmpty();
    }

    @Test
    void merge() {
        //given
        changelog.addEntry(testItem, ProcessingChangelog.ChangeType.UPDATED, Collections.singletonList("foo"));

        ProcessingChangelog incoming = new ProcessingChangelog();
        incoming.addEntry(testItem, ProcessingChangelog.ChangeType.UPDATED, Collections.singletonList("bar"));

        //when
        changelog.merge(incoming);

        //then
        Map<String, ProcessingChangelog.Entry> changes = changelog.getChanges();
        assertThat(changes).isNotNull().hasSize(1);
        ProcessingChangelog.Entry actual = changes.get(testItem.getFullyQualifiedIdentifier().jsonValue());
        assertThat(actual).isNotNull();
        assertThat(actual.getMessages()).isEqualTo(List.of("foo", "bar"));

    }

    @Test
    void mergeDifferent() {
        //given
        changelog.addEntry(testItem, ProcessingChangelog.ChangeType.UPDATED, Collections.singletonList("foo"));

        ProcessingChangelog incoming = new ProcessingChangelog();
        incoming.addEntry(ItemFactory.getTestItem("b", "hihi"), ProcessingChangelog.ChangeType.UPDATED, Collections.singletonList("bar"));

        //when
        changelog.merge(incoming);

        //then
        Map<String, ProcessingChangelog.Entry> changes = changelog.getChanges();
        assertThat(changes).isNotNull().hasSize(2);

    }
}