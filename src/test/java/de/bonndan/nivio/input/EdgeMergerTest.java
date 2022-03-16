package de.bonndan.nivio.input;

import de.bonndan.nivio.GraphTestSupport;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.RelationDescription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class EdgeMergerTest {

    private GraphTestSupport graph;
    private LandscapeDescription input;
    private EdgeMerger edgeMerger;

    @BeforeEach
    void setUp() {
        graph = new GraphTestSupport();
        input = new LandscapeDescription("test");

        edgeMerger = new EdgeMerger(graph.landscape.getReadAccess(), graph.landscape.getWriteAccess());
    }

    @Test
    void addsRelation() {

        ItemDescription description = new ItemDescription(graph.itemAA.getIdentifier());
        description.setGroup(graph.groupA.getIdentifier());
        description.addOrReplaceRelation(new RelationDescription(graph.itemAA.getIdentifier(), graph.itemAC.getIdentifier()));
        input.mergeItems(List.of(description));

        //when
        ProcessingChangelog process = edgeMerger.mergeAndDiff(new ArrayList<>(input.getItemDescriptions()), graph.landscape.getLog());

        //then
        assertThat(process.getChanges()).hasSize(3); //no updates, one created
    }

    @Test
    void addRelationChecksSource() {

        ItemDescription description = new ItemDescription(graph.itemAA.getIdentifier());
        description.setGroup(graph.groupA.getIdentifier());
        description.addOrReplaceRelation(new RelationDescription("wrong", graph.itemAC.getIdentifier()));
        input.mergeItems(List.of(description));
        ProcessLog processLog = new ProcessLog(mock(Logger.class), graph.landscape.getIdentifier());

        //when
        ProcessingChangelog process = edgeMerger.mergeAndDiff(new ArrayList<>(input.getItemDescriptions()), processLog);

        //then
        assertThat(process.getChanges()).isEmpty();
        assertThat(processLog.getMessages()).isNotEmpty();
        assertThat(processLog.getMessages().get(0).level).isEqualTo("WARN");
    }

    @Test
    void addRelationChecksTarget() {

        ItemDescription description = new ItemDescription(graph.itemAA.getIdentifier());
        description.setGroup(graph.groupA.getIdentifier());
        description.addOrReplaceRelation(new RelationDescription(graph.itemAA.getIdentifier(), "wrong"));
        input.mergeItems(List.of(description));
        ProcessLog processLog = new ProcessLog(mock(Logger.class), graph.landscape.getIdentifier());

        //when
        ProcessingChangelog process = edgeMerger.mergeAndDiff(new ArrayList<>(input.getItemDescriptions()), processLog);

        //then
        assertThat(process.getChanges()).isEmpty();
        assertThat(processLog.getMessages()).isNotEmpty();
        assertThat(processLog.getMessages().get(0).level).isEqualTo("WARN");
    }

    @Test
    @DisplayName("only changes are counted as updates")
    void processAddsUpdates() {

        // add a -> c
        ItemDescription description = new ItemDescription(graph.itemAA.getIdentifier());
        description.setGroup(graph.groupA.getIdentifier());
        description.addOrReplaceRelation(new RelationDescription(graph.itemAA.getIdentifier(), graph.itemAC.getIdentifier()));
        input.mergeItems(List.of(description));
        edgeMerger.mergeAndDiff(new ArrayList<>(input.getItemDescriptions()), graph.landscape.getLog());

        //add a -> b, keep a -> c
        description = new ItemDescription(graph.itemAA.getIdentifier());
        description.setGroup(graph.groupA.getIdentifier());
        description.addOrReplaceRelation(new RelationDescription(graph.itemAA.getIdentifier(), graph.itemAB.getIdentifier()));
        description.addOrReplaceRelation(new RelationDescription(graph.itemAA.getIdentifier(), graph.itemAC.getIdentifier()));
        input.mergeItems(List.of(description));

        //when
        ProcessLog processLog = new ProcessLog(mock(Logger.class), graph.landscape.getIdentifier());
        ProcessingChangelog changelog = edgeMerger.mergeAndDiff(new ArrayList<>(input.getItemDescriptions()), processLog);

        //then
        assertThat(changelog.getChanges()).hasSize(4); //one create, 3 updates for nodes and relation
        URI rel = URI.create("relation://test/default/default/a/a?to=item://test/default/default/a/b");
        assertThat(changelog.getChanges()).containsKey(rel);
        ProcessingChangelog.Entry entry = changelog.getChanges().get(rel);
        assertThat(entry).isNotNull();
        assertThat(entry.getChangeType()).isEqualTo("CREATED");
    }

    @Test
    void removesRelation() {

        // add a -> c
        ItemDescription description = new ItemDescription(graph.itemAA.getIdentifier());
        description.setGroup(graph.groupA.getIdentifier());
        description.addOrReplaceRelation(new RelationDescription(graph.itemAA.getIdentifier(), graph.itemAC.getIdentifier()));
        input.mergeItems(List.of(description));
        edgeMerger.mergeAndDiff(new ArrayList<>(input.getItemDescriptions()), graph.landscape.getLog());

        //update with independent "fresh" data
        input = new LandscapeDescription("test");
        description = new ItemDescription(graph.itemAA.getIdentifier());
        description.setGroup(graph.groupA.getIdentifier());
        input.mergeItems(List.of(description));

        //when
        ProcessingChangelog changelog = edgeMerger.mergeAndDiff(new ArrayList<>(input.getItemDescriptions()), graph.landscape.getLog());

        //then
        assertThat(graph.itemAA.getRelations()).isEmpty();
        assertThat(changelog.getChanges()).hasSize(1); //no update, one delete
    }
}