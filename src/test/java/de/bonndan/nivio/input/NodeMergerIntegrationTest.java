package de.bonndan.nivio.input;

import de.bonndan.nivio.GraphTestSupport;
import de.bonndan.nivio.input.dto.GroupDescription;
import de.bonndan.nivio.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class NodeMergerIntegrationTest {

    private GraphTestSupport graph;

    @BeforeEach
    void setUp() {
        graph = new GraphTestSupport();
    }

    @Test
    void forGroups() {
        NodeMerger<Group, GroupDescription, Context> nodeMerger = NodeMergerFactory.forGroups(graph.landscape);

        GroupDescription g1 = new GroupDescription();
        g1.setIdentifier("foo");

        GroupDescription gaUpdate = new GroupDescription();
        gaUpdate.setIdentifier(graph.groupA.getIdentifier());
        gaUpdate.setName("a new name");

        //when
        ProcessingChangelog processingChangelog = nodeMerger.mergeAndDiff(List.of(g1, gaUpdate), new ProcessLog(mock(Logger.class), "x"));

        //then
        assertThat(processingChangelog).isNotNull();
        assertThat(processingChangelog.getChanges()).isNotNull().hasSize(5);

        var gaChange = getChange(processingChangelog, graph.groupA);
        assertThat(gaChange.getChangeType()).isEqualTo(ProcessingChangelog.ChangeType.UPDATED.name());
        GraphComponent newGroupA = graph.index.get(graph.groupA.getFullyQualifiedIdentifier()).orElseThrow();
        assertThat(newGroupA.getName()).isEqualTo(gaUpdate.getName());

        URI foo = FullyQualifiedIdentifier.build(Group.class, graph.landscape.getIdentifier(), "default", "default", "foo", null);
        assertThat(processingChangelog.getChanges()).containsKey(foo);
        var newGroupChange = processingChangelog.getChanges().get(foo);
        assertThat(newGroupChange).isNotNull();
        assertThat(newGroupChange.getChangeType()).isEqualTo(ProcessingChangelog.ChangeType.CREATED.name());
    }

    @DisplayName("marks parents as updated if child changes")
    @Test
    void marksParents() {
        NodeMerger<Group, GroupDescription, Context> nodeMerger = NodeMergerFactory.forGroups(graph.landscape);

        GroupDescription g1 = new GroupDescription();
        g1.setIdentifier("foo");

        GroupDescription gaUpdate = new GroupDescription();
        gaUpdate.setIdentifier(graph.groupA.getIdentifier());
        gaUpdate.setName("a new name");

        //when
        ProcessingChangelog processingChangelog = nodeMerger.mergeAndDiff(List.of(g1, gaUpdate), new ProcessLog(mock(Logger.class), "x"));

        //then
        assertThat(processingChangelog).isNotNull();
        assertThat(processingChangelog.getChanges()).isNotNull().hasSize(5);

        URI fooParent = FullyQualifiedIdentifier.build(Context.class, graph.landscape.getIdentifier(), "default", "default", null, null);
        assertThat(processingChangelog.getChanges()).containsKey(fooParent);
        var fooParentUpdate = processingChangelog.getChanges().get(fooParent);
        assertThat(fooParentUpdate).isNotNull();
        assertThat(fooParentUpdate.getChangeType()).isEqualTo(ProcessingChangelog.ChangeType.UPDATED.name());
    }

    @Test
    void componentsOnlyScheduledForRemoval() {
        NodeMerger<Group, GroupDescription, Context> nodeMerger = NodeMergerFactory.forGroups(graph.landscape);

        GroupDescription g1 = new GroupDescription();
        g1.setIdentifier("foo");

        GroupDescription gaUpdate = new GroupDescription();
        gaUpdate.setIdentifier(graph.groupA.getIdentifier());

        //when
        ProcessingChangelog processingChangelog = nodeMerger.mergeAndDiff(List.of(g1, gaUpdate), new ProcessLog(mock(Logger.class), "x"));

        //then
        assertThat(processingChangelog).isNotNull();
        assertThat(processingChangelog.getChanges()).isNotNull().hasSize(5);

        //assert it is only scheduled for removal, but still in the graph
        assertThat(graph.landscape.getIndexReadAccess().get(graph.groupB.getFullyQualifiedIdentifier())).isNotEmpty();
    }

    @Test
    void forItems() {
        assertThat(false).isTrue();
    }

    @Test
    void forUnits() {
        assertThat(false).isTrue();
    }

    @Test
    void forContexts() {
        assertThat(false).isTrue();
    }

    private ProcessingChangelog.Entry getChange(ProcessingChangelog processingChangelog, Component c) {
        return processingChangelog.getChanges().get(c.getFullyQualifiedIdentifier());
    }
}