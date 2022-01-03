package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.RelationDescription;
import de.bonndan.nivio.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


class ItemRelationProcessorTest {

    private LandscapeDescription input;
    private Landscape landscape;
    private ItemRelationProcessor processor;

    @BeforeEach
    void setUp() {
        input = new LandscapeDescription("test");
        Set<Item> items = new HashSet<>();
        Item foo = ItemFactory.getTestItem("a", "foo");
        items.add(foo);
        Item bar = ItemFactory.getTestItem("a", "bar");
        items.add(bar);
        Item baz = ItemFactory.getTestItem("a", "baz");
        items.add(baz);

        foo.addOrReplace(RelationFactory.createForTesting(foo, bar));
        bar.addOrReplace(RelationFactory.createForTesting(foo, bar));

        foo.addOrReplace(RelationFactory.createForTesting(foo, baz));
        baz.addOrReplace(RelationFactory.createForTesting(foo, baz));

        landscape = LandscapeFactory.createForTesting("test", "test").withItems(items).build();

        processor = new ItemRelationProcessor(new ProcessLog(LoggerFactory.getLogger(ItemRelationProcessorTest.class), landscape.getIdentifier()));
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
        ProcessingChangelog process = processor.process(input, landscape);

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
        ProcessingChangelog process = processor.process(input, landscape);

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
        ProcessingChangelog process = processor.process(input, landscape);

        //then
        assertThat(process.getChanges()).hasSize(1); //no update, one delete
        assertThat(process.getChanges()).containsKey("test/a/foo;test/a/baz");
        assertThat(process.getChanges().get("test/a/foo;test/a/baz").getChangeType()).isEqualTo(ProcessingChangelog.ChangeType.DELETED.name());

        Item foo = landscape.getItems().pick("foo", "a");
        assertThat(foo.getRelations()).hasSize(1);

        Item bar = landscape.getItems().pick("bar", "a");
        assertThat(bar.getRelations()).hasSize(1);

        Item baz = landscape.getItems().pick("baz", "a");
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
        ProcessingChangelog process = processor.process(input, landscape);

        //then
        assertThat(process.getChanges()).hasSize(1); //no update, one delete
        assertThat(process.getChanges()).containsKey("test/a/foo;test/a/baz");
        assertThat(process.getChanges().get("test/a/foo;test/a/baz").getChangeType()).isEqualTo(ProcessingChangelog.ChangeType.DELETED.name());

        Item foo = landscape.getItems().pick("foo", "a");
        assertThat(foo.getRelations()).hasSize(1);

        Item bar = landscape.getItems().pick("bar", "a");
        assertThat(bar.getRelations()).hasSize(1);

        Item baz = landscape.getItems().pick("baz", "a");
        assertThat(baz.getRelations()).hasSize(0);
    }
}