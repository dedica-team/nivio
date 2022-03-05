package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.GraphTestSupport;
import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.RelationFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ItemSorterTest {

    private GraphTestSupport graph;

    @BeforeEach
    void setup() {
        graph = new GraphTestSupport();
    }

    @Test
    void createsChains() {

        //given
        Group test = graph.getTestGroup("test");
        Item one = graph.getTestItem(test.getIdentifier(), "one");
        Item two = graph.getTestItem(test.getIdentifier(), "two");
        Item three = graph.getTestItem(test.getIdentifier(), "three");
        Item four = graph.getTestItem(test.getIdentifier(), "four");
        Item five = graph.getTestItem(test.getIdentifier(), "five");
        Item six = graph.getTestItem(test.getIdentifier(), "six");
        Item seven = graph.getTestItem(test.getIdentifier(), "seven");

        graph.landscape.getWriteAccess().addOrReplaceRelation(RelationFactory.createForTesting(one, two));
        graph.landscape.getWriteAccess().addOrReplaceRelation(RelationFactory.createForTesting(two, three));
        graph.landscape.getWriteAccess().addOrReplaceRelation(RelationFactory.createForTesting(three, four));

        graph.landscape.getWriteAccess().addOrReplaceRelation(RelationFactory.createForTesting(five, six));

        var groupItems = List.of(one, two, three, four, five, six, seven);

        //when
        List<Item> sorted = ItemSorter.sort(groupItems);

        //then
        assertThat(sorted).isNotEmpty().hasSize(groupItems.size());

        assertThat(sorted.get(0)).isSameAs(one);
        assertThat(sorted.get(1)).isSameAs(two);
        assertThat(sorted.get(2)).isSameAs(three);
        assertThat(sorted.get(3)).isSameAs(four);
        assertThat(sorted.get(4)).isSameAs(five);
        assertThat(sorted.get(5)).isSameAs(six);
        assertThat(sorted.get(6)).isSameAs(seven);
    }

    @Test
    void sorts() {

        //given
        Group test = graph.getTestGroup("test");
        Item one = graph.getTestItem(test.getIdentifier(), "one");
        Item two = graph.getTestItem(test.getIdentifier(), "two");
        Item three = graph.getTestItem(test.getIdentifier(), "three");
        Item four = graph.getTestItem(test.getIdentifier(), "four");
        Item five = graph.getTestItem(test.getIdentifier(), "five");
        Item six = graph.getTestItem(test.getIdentifier(), "six");
        Item seven = graph.getTestItem(test.getIdentifier(), "seven");

        graph.landscape.getWriteAccess().addOrReplaceRelation(RelationFactory.createForTesting(one, three));
        graph.landscape.getWriteAccess().addOrReplaceRelation(RelationFactory.createForTesting(three, four));
        graph.landscape.getWriteAccess().addOrReplaceRelation(RelationFactory.createForTesting(four, seven));

        var groupItems = List.of(one, two, three, four, five, six, seven);

        //when
        List<Item> sorted = ItemSorter.sort(groupItems);

        //then
        assertThat(sorted).isNotEmpty().hasSize(groupItems.size());

        assertThat(sorted.get(0)).isSameAs(one);
        assertThat(sorted.get(1)).isSameAs(three);
        assertThat(sorted.get(2)).isSameAs(four);
        assertThat(sorted.get(3)).isSameAs(seven);
    }

    @Test
    void exitEndLessLoop() {

        //given
        Group test = graph.getTestGroup("test");
        Item one = graph.getTestItem(test.getIdentifier(), "one");
        Item two = graph.getTestItem(test.getIdentifier(), "two");
        Item three = graph.getTestItem(test.getIdentifier(), "three");
        Item four = graph.getTestItem(test.getIdentifier(), "four");
        Item five = graph.getTestItem(test.getIdentifier(), "five");
        Item six = graph.getTestItem(test.getIdentifier(), "six");
        Item seven = graph.getTestItem(test.getIdentifier(), "seven");

        graph.landscape.getWriteAccess().addOrReplaceRelation(RelationFactory.createForTesting(three, one));
        graph.landscape.getWriteAccess().addOrReplaceRelation(RelationFactory.createForTesting(one, two));
        graph.landscape.getWriteAccess().addOrReplaceRelation(RelationFactory.createForTesting(two, three));

        var groupItems = List.of(one, two, three, four, five, six, seven);

        //when
        List<Item> sorted = ItemSorter.sort(groupItems);

        //then
        assertThat(sorted).isNotEmpty().hasSize(groupItems.size());

        assertThat(sorted.get(0)).isSameAs(three);
    }
}