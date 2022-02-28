package de.bonndan.nivio.input;

import de.bonndan.nivio.GraphTestSupport;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.Landscape;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.net.URI;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class InputProcessorTest {

    private GraphTestSupport graph;
    private Landscape landscape;
    private InputProcessor processor;
    private ProcessLog log;
    private LandscapeDescription input;

    @BeforeEach
    void setUp() {
        graph = new GraphTestSupport();
        landscape = graph.landscape;
        input = new LandscapeDescription(landscape.getIdentifier());

        //new empty log
        log = new ProcessLog(mock(Logger.class), landscape.getIdentifier());
        input.setProcessLog(log);

        processor = new InputProcessor();
    }

    @Test
    void addedItemWithoutGroup() {

        //given
        ArrayList<ItemDescription> items1 = new ArrayList<>();
        items1.add(new ItemDescription("a")); // -> should match itemAA
        items1.add(new ItemDescription("b")); // -> should match itemAB
        items1.add(new ItemDescription("c")); // -> should match itemAC
        items1.forEach(itemDescription -> input.getWriteAccess().addOrReplaceChild(itemDescription));

        //when
        Landscape out = processor.process(input, landscape);

        assertThat(out).isNotNull();

        Map<URI, ProcessingChangelog.Entry> changed = getChanges(out, ProcessingChangelog.ChangeType.UPDATED);
        assertThat(changed).hasSize(7);
        assertThat(changed).containsKey(graph.itemAA.getFullyQualifiedIdentifier());
        assertThat(changed).containsKey(graph.itemAB.getFullyQualifiedIdentifier());
        assertThat(changed).containsKey(graph.itemAC.getFullyQualifiedIdentifier());
        assertThat(changed).containsKey(graph.groupA.getFullyQualifiedIdentifier());
        assertThat(changed).containsKey(graph.context.getFullyQualifiedIdentifier());
        assertThat(changed).containsKey(graph.unit.getFullyQualifiedIdentifier());
        assertThat(changed).containsKey(graph.landscape.getFullyQualifiedIdentifier());

        Map<URI, ProcessingChangelog.Entry> deleted = getChanges(out, ProcessingChangelog.ChangeType.DELETED);
        assertThat(deleted).hasSize(2); //3 groups, 3 items
        assertThat(deleted).containsKey(graph.groupB.getFullyQualifiedIdentifier());
        assertThat(deleted).containsKey(graph.groupC.getFullyQualifiedIdentifier());
    }

    @Test
    @DisplayName("One item added, one deleted in existing group")
    void addedWithExistingGroup() {

        //given
        ItemDescription a = new ItemDescription("a");
        a.setGroup(graph.groupA.getIdentifier());
        input.getWriteAccess().addOrReplaceChild(a);

        ItemDescription b = new ItemDescription("b");
        b.setGroup(graph.groupA.getIdentifier());
        input.getWriteAccess().addOrReplaceChild(b);

        ItemDescription other = new ItemDescription("other");
        other.setGroup(graph.groupA.getIdentifier());
        input.getWriteAccess().addOrReplaceChild(other);

        //when
        Landscape out = processor.process(input, landscape);

        assertThat(out).isNotNull();
        assertThat(out.getLog().getChangelog().getChanges()).hasSize(10);
        Map<URI, ProcessingChangelog.Entry> deletions = getChanges(out, ProcessingChangelog.ChangeType.DELETED);
        assertThat(deletions).hasSize(3); //2 groups, 1 item
        assertThat(deletions).doesNotContainKey(graph.groupA.getFullyQualifiedIdentifier());
        assertThat(deletions).containsKey(graph.groupB.getFullyQualifiedIdentifier());
        assertThat(deletions).containsKey(graph.groupC.getFullyQualifiedIdentifier());
        assertThat(deletions).containsKey(graph.itemAC.getFullyQualifiedIdentifier());

    }

    @Test
    void addedNone() {

        //given
        ItemDescription a = new ItemDescription("a");
        a.setGroup(graph.groupA.getIdentifier());
        input.getWriteAccess().addOrReplaceChild(a);

        ItemDescription b = new ItemDescription("b");
        b.setGroup(graph.groupA.getIdentifier());
        input.getWriteAccess().addOrReplaceChild(b);

        ItemDescription c = new ItemDescription("c");
        c.setGroup(graph.groupA.getIdentifier());
        input.getWriteAccess().addOrReplaceChild(c);

        //when
        Landscape out = processor.process(input, landscape);

        assertThat(out).isNotNull();
        assertThat(out.getLog().getChangelog().getChanges()).hasSize(9);

        Map<URI, ProcessingChangelog.Entry> updates = getChanges(out, ProcessingChangelog.ChangeType.UPDATED);
        assertThat(updates).hasSize(7); //3 items, all parents
        assertThat(updates).containsKey(graph.itemAA.getFullyQualifiedIdentifier());
        assertThat(updates).containsKey(graph.itemAB.getFullyQualifiedIdentifier());
        assertThat(updates).containsKey(graph.itemAC.getFullyQualifiedIdentifier());

        Map<URI, ProcessingChangelog.Entry> deletions = getChanges(out, ProcessingChangelog.ChangeType.DELETED);
        assertThat(deletions).hasSize(2); //groups b,c
        assertThat(deletions).containsKey(graph.groupB.getFullyQualifiedIdentifier());
        assertThat(deletions).containsKey(graph.groupC.getFullyQualifiedIdentifier());

    }



    @Test
    void keptNone() {

        //given

        //when
        Landscape out = processor.process(input, landscape);

        assertThat(out).isNotNull();
        assertThat(out.getLog().getChangelog().getChanges()).hasSize(8);

        Map<URI, ProcessingChangelog.Entry> updates = getChanges(out, ProcessingChangelog.ChangeType.UPDATED);
        assertThat(updates).hasSize(0);

        Map<URI, ProcessingChangelog.Entry> deletions = getChanges(out, ProcessingChangelog.ChangeType.DELETED);
        assertThat(deletions).hasSize(8); //groups b,c

        assertThat(deletions).doesNotContainKey(landscape.getFullyQualifiedIdentifier());

    }

    @Test
    void removedNoneIfPartial() {

        //given. no items should lead to complete deletions
        input.setIsPartial(true);

        //when
        Landscape out = processor.process(input, landscape);

        assertThat(out).isNotNull();
        assertThat(out.getLog().getChangelog().getChanges()).hasSize(0);
    }

    private  Map<URI, ProcessingChangelog.Entry> getChanges(final Landscape landscape, final ProcessingChangelog.ChangeType changeType) {
        return landscape.getLog().getChangelog().getChanges().entrySet()
                .stream()
                .filter(uriEntryEntry -> changeType.name().equals(uriEntryEntry.getValue().getChangeType()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}