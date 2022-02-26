package de.bonndan.nivio.model;

import de.bonndan.nivio.GraphTestSupport;
import de.bonndan.nivio.assessment.Assessable;
import de.bonndan.nivio.input.dto.RelationDescription;
import org.junit.jupiter.api.BeforeEach;
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

    @BeforeEach
    void setup() {

    }

    @Test
    void equalsWithGroup() {
        Item s1 = getTestItem("g1", "a");

        Item s2 = getTestItem("g1", "a");

        Item s3 = getTestItem("g2", "a");

        Item s4 = getTestItem(Layer.domain.name(), "a");

        assertEquals(s1, s2);
        assertEquals(s2, s1);
        assertNotEquals(s3, s1);
        assertNotEquals(s3, s2);
        assertNotEquals(s4, s1);
        assertNotEquals(s4, s2);
    }

    @Test
    void equalsWithLandscape() {

        Item s1 = getTestItem("g1", "a");

        Item s2 = getTestItem("g1", "a");

        assertEquals(s1, s2);
    }


    @Test
    void getChangesInLabels() {

        Item s1 = getTestItem("g1", "a");
        s1.getLabels().put("foo.one", "one");

        Item s2 = getTestItem("g1", "a");
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
    void relationsAsAssessmentChildren() {

        var graph = new GraphTestSupport();

        Relation forTesting = RelationFactory.create(graph.itemAA, graph.itemAC, new RelationDescription());
        graph.landscape.getWriteAccess().addOrReplaceRelation(forTesting);

        //when
        Set<? extends Assessable> children = graph.itemAA.getAssessables();
        assertThat(children).hasSize(1);
        assertThat(children.iterator().next()).isEqualTo(forTesting);

    }
}
