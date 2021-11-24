package de.bonndan.nivio.model;

import de.bonndan.nivio.assessment.Assessable;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static de.bonndan.nivio.model.ItemFactory.getTestItem;
import static de.bonndan.nivio.model.ItemFactory.getTestItemBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ItemTest {

    @Test
    void equalsWithGroup() {

        Landscape landscape = LandscapeFactory.createForTesting("l1", "l1Landscape").build();


        Item s1 = getTestItem("g1", "a", landscape);

        Item s2 = getTestItem("g1", "a", landscape);

        Item s3 = getTestItem("g2", "a", landscape);

        Item s4 = getTestItem(Layer.domain.name(), "a", landscape);

        assertEquals(s1, s2);
        assertEquals(s2, s1);
        assertNotEquals(s3, s1);
        assertNotEquals(s3, s2);
        assertNotEquals(s4, s1);
        assertNotEquals(s4, s2);
    }

    @Test
    void equalsWithLandscape() {

        Landscape landscape = LandscapeFactory.createForTesting("l1", "l1Landscape").build();

        Item s1 = getTestItem("g1", "a", landscape);

        Item s2 = getTestItem("g1", "a", landscape);

        assertEquals(s1, s2);

        Item s3 = getTestItem("g1", "a");

        assertNotEquals(s1, s3);
    }


    @Test
    void getChangesInLabels() {
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
    void getChangesInName() {

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
    void getChangesInDescription() {

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
    void getChangesInOwner() {

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
    void getChangesInLinks() throws MalformedURLException {

        Item s1 = getTestItemBuilder("g1", "a")
                .withLinks(Map.of("foo", new Link(new URL("https://acme.com"))))
                .build();

        Item s2 = getTestItemBuilder("g1", "a")
                .withLinks(Map.of("bar", new Link(new URL("https://acme.com"))))
                .build();

        List<String> changes = s1.getChanges(s2);
        assertThat(changes).isNotNull().hasSize(1);
        assertThat(changes.get(0)).contains("Links");
    }

    @Test
    void setRelations() {

        Item s1 = getTestItemBuilder("g1", "a").build();
        Item s2 = getTestItemBuilder("g1", "b").build();
        Item s3 = getTestItemBuilder("g1", "c").build();

        s1.setRelations(Set.of(RelationFactory.createForTesting(s1, s2)));
        assertThat(s1.getRelations()).hasSize(1);

        //when
        s1.setRelations(Set.of(RelationFactory.createForTesting(s1, s3)));
        assertThat(s1.getRelations()).hasSize(1);
    }

    @Test
    void relationsAsAssessmentChildren() {

        Item s1 = getTestItemBuilder("g1", "a").build();
        Item s2 = getTestItemBuilder("g2", "b").build();
        Relation forTesting = RelationFactory.createForTesting(s1, s2);
        s1.addOrReplace(forTesting);

        //when
        List<? extends Assessable> children = s1.getChildren();
        assertThat(children).hasSize(1);
        assertThat(children.get(0)).isEqualTo(forTesting);

    }
}
