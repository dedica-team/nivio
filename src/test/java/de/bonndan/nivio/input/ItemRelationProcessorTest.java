package de.bonndan.nivio.input;

import de.bonndan.nivio.GraphTestSupport;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.RelationDescription;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.RelationFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


class ItemRelationProcessorTest {

    private LandscapeDescription input;
    private ItemRelationProcessor processor;
    private GraphTestSupport graph;

    @BeforeEach
    void setUp() {

        graph = new GraphTestSupport();
        graph.landscape.getIndexWriteAccess().addOrReplaceRelation(RelationFactory.createForTesting(graph.itemAA, graph.itemAB));
        graph.landscape.getIndexWriteAccess().addOrReplaceRelation(RelationFactory.createForTesting(graph.itemAA, graph.itemAC));

        input = new LandscapeDescription("test");
        processor = new ItemRelationProcessor(new ProcessLog(LoggerFactory.getLogger(ItemRelationProcessorTest.class), graph.landscape.getIdentifier()));
    }

    @Test
    void processAddsRelation() {

        ItemDescription description = new ItemDescription("foo");
        description.setGroup("a");
        description.addOrReplaceRelation(new RelationDescription("foo", "bar"));
        description.addOrReplaceRelation(new RelationDescription("foo", "baz"));
        input.mergeItems(List.of(description));
        //new
        ItemDescription bar = new ItemDescription("bar");
        bar.setGroup("a");
        bar.addOrReplaceRelation(new RelationDescription("bar", "baz"));
        input.mergeItems(List.of(bar));

        //when
        ProcessingChangelog process = processor.process(input, graph.landscape);

        //then
        assertThat(process.getChanges()).hasSize(1); //no updates, one created
    }

    //only changes are counted as updates
    @Test
    void processAddsUpdates() {

        ItemDescription description = new ItemDescription("foo");
        description.setGroup("a");
        RelationDescription relationItem = new RelationDescription("foo", "bar");
        relationItem.setFormat("JSON");
        description.addOrReplaceRelation(relationItem);
        description.addOrReplaceRelation(new RelationDescription("foo", "baz"));
        input.mergeItems(List.of(description));

        //when
        ProcessingChangelog process = processor.process(input, graph.landscape);

        //then
        assertThat(process.getChanges()).hasSize(1); //one update
    }

    @Test
    void processRemovesRelation() {

        ItemDescription description = new ItemDescription("foo");
        description.setGroup("a");
        description.addOrReplaceRelation(new RelationDescription("foo", "bar"));
        input.mergeItems(List.of(description));

        //when
        ProcessingChangelog process = processor.process(input, graph.landscape);

        //then
        assertThat(process.getChanges()).hasSize(1); //no update, one delete
        URI s = URI.create("test/a/foo;test/a/baz");
        assertThat(process.getChanges()).containsKey(s);
        assertThat(process.getChanges().get(s).getChangeType()).isEqualTo(ProcessingChangelog.ChangeType.DELETED.name());

        Item foo = graph.landscape.getIndexReadAccess().findOneByIdentifiers("foo", "a", Item.class).orElseThrow();
        assertThat(foo.getRelations()).hasSize(1);

        Item bar = graph.landscape.getIndexReadAccess().findOneByIdentifiers("bar", "a", Item.class).orElseThrow();
        assertThat(bar.getRelations()).hasSize(1);

        Item baz = graph.landscape.getIndexReadAccess().findOneByIdentifiers("baz", "a", Item.class).orElseThrow();
        assertThat(baz.getRelations()).hasSize(0);
    }

    @DisplayName("removes relation even if ends cannot be found")
    @Test
    void regression647() {

        ItemDescription description = new ItemDescription("foo");
        description.setGroup("a");
        description.addOrReplaceRelation(new RelationDescription("foo", "bar"));
        description.addOrReplaceRelation(new RelationDescription("foo", "somethingToDelete"));
        input.mergeItems(List.of(description));

        //when
        ProcessingChangelog process = processor.process(input, graph.landscape);

        //then
        assertThat(process.getChanges()).hasSize(1); //no update, one delete
        URI s = URI.create("test/a/foo;test/a/baz");
        assertThat(process.getChanges()).containsKey(s);
        assertThat(process.getChanges().get(s).getChangeType()).isEqualTo(ProcessingChangelog.ChangeType.DELETED.name());

        Item foo = graph.landscape.getIndexReadAccess().findOneByIdentifiers("foo", "a", Item.class).orElseThrow();
        assertThat(foo.getRelations()).hasSize(1);

        Item bar = graph.landscape.getIndexReadAccess().findOneByIdentifiers("bar", "a", Item.class).orElseThrow();
        assertThat(bar.getRelations()).hasSize(1);

        Item baz = graph.landscape.getIndexReadAccess().findOneByIdentifiers("baz", "a", Item.class).orElseThrow();
        assertThat(baz.getRelations()).hasSize(0);
    }
}