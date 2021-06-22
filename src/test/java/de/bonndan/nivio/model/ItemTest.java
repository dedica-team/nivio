package de.bonndan.nivio.model;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static de.bonndan.nivio.model.ItemFactory.getTestItem;
import static de.bonndan.nivio.model.ItemFactory.getTestItemBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class ItemTest {

    @Test
    public void equalsWithGroup() {

        Landscape landscape = LandscapeFactory.createForTesting("l1", "l1Landscape").build();


        Item s1 = getTestItem("g1", "a", landscape);

        Item s2 = getTestItem("g1", "a", landscape);

        Item s3 = getTestItem("g2", "a", landscape);

        Item s4 = getTestItem(Group.COMMON, "a", landscape);

        assertEquals(s1, s2);
        assertEquals(s2, s1);
        assertNotEquals(s3, s1);
        assertNotEquals(s3, s2);
        assertNotEquals(s4, s1);
        assertNotEquals(s4, s2);
    }

    @Test
    public void equalsWithLandscape() {

        Landscape landscape = LandscapeFactory.createForTesting("l1", "l1Landscape").build();

        Item s1 = getTestItem("g1", "a", landscape);

        Item s2 = getTestItem("g1", "a", landscape);

        assertEquals(s1, s2);

        Item s3 = getTestItem("g1", "a");

        assertNotEquals(s1, s3);
    }

    @Test
    public void labelsAreNotGroupedInApi() {

        Landscape landscape = LandscapeFactory.createForTesting("l1", "l1Landscape").build();

        Item s1 = getTestItem("g1", "a", landscape);
        s1.getLabels().put("foo.one", "one");
        s1.getLabels().put("foo.two", "two");

        Map<String, String> labels = s1.getJSONLabels();
        assertThat(labels).containsKey("foo.one");
        assertThat(labels).containsKey("foo.two");
    }

    @Test
    public void getChangesInLabels() {
        Landscape landscape = LandscapeFactory.createForTesting("l1", "l1Landscape").build();

        Item s1 = getTestItem("g1", "a", landscape);
        s1.getLabels().put("foo.one", "one");

        Item s2 = getTestItem("g1", "a", landscape);
        s2.getLabels().put("foo.one", "two");

        List<String> changes = s1.getChanges(s2);
        assertThat(changes).isNotNull().hasSize(1);
        assertThat(changes.get(0)).contains("two");
    }

    @Test
    public void getChangesInName() {

        Item s1 = getTestItemBuilder("g1", "a")
                .withName("foo")
                .build();

        Item s2 = getTestItemBuilder("g1", "a")
                .withName("bar")
                .build();

        List<String> changes = s1.getChanges(s2);
        assertThat(changes).isNotNull().hasSize(1);
        assertThat(changes.get(0)).contains("Name");
    }

    @Test
    public void getChangesInDescription() {

        Item s1 = getTestItemBuilder("g1", "a")
                .withDescription("foo")
                .build();

        Item s2 = getTestItemBuilder("g1", "a")
                .withDescription("bar")
                .build();

        List<String> changes = s1.getChanges(s2);
        assertThat(changes).isNotNull().hasSize(1);
        assertThat(changes.get(0)).contains("Description");
    }

    @Test
    public void getChangesInOwner() {

        Item s1 = getTestItemBuilder("g1", "a")
                .withOwner("foo")
                .build();

        Item s2 = getTestItemBuilder("g1", "a")
                .withOwner("bar")
                .build();

        List<String> changes = s1.getChanges(s2);
        assertThat(changes).isNotNull().hasSize(1);
        assertThat(changes.get(0)).contains("Owner");
    }

    @Test
    public void getChangesInLinks() {

        Item s1 = getTestItemBuilder("g1", "a")
                .withLinks(Map.of("foo", new Link("https://acme.com")))
                .build();

        Item s2 = getTestItemBuilder("g1", "a")
                .withLinks(Map.of("bar", new Link("https://acme.com")))
                .build();

        List<String> changes = s1.getChanges(s2);
        assertThat(changes).isNotNull().hasSize(1);
        assertThat(changes.get(0)).contains("Links");
    }

    @Test
    public void setRelations() {

        Item s1 = getTestItemBuilder("g1", "a").build();
        Item s2 = getTestItemBuilder("g1", "b").build();
        Item s3 = getTestItemBuilder("g1", "c").build();

        s1.setRelations(Set.of(RelationFactory.createForTesting(s1, s2)));
        assertThat(s1.getRelations()).hasSize(1);

        //when
        s1.setRelations(Set.of(RelationFactory.createForTesting(s1, s3)));
        assertThat(s1.getRelations()).hasSize(1);
    }
}
