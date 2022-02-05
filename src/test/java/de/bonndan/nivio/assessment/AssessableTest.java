package de.bonndan.nivio.assessment;

import de.bonndan.nivio.GraphTestSupport;
import de.bonndan.nivio.assessment.kpi.AbstractKPI;
import de.bonndan.nivio.assessment.kpi.KPI;
import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.output.dto.RangeApiModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.lang.NonNull;

import java.net.URI;
import java.util.*;
import java.util.function.Function;

import static de.bonndan.nivio.model.ItemFactory.getTestItem;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AssessableTest {

    Group assessableGroup;
    private GraphTestSupport graph;


    @BeforeEach
    void setup() {
        graph = new GraphTestSupport();
        assessableGroup = graph.getTestGroup("foo");
    }

    @Test
    void getSummaryHighest() {

        List<StatusValue> statusValues = List.of(
                new StatusValue(URI.create("foo"), "test1", Status.GREEN, ""),
                new StatusValue(URI.create("foo"), "test2", Status.GREEN, ""),
                new StatusValue(URI.create("foo"), "test2", Status.ORANGE, ""),
                new StatusValue(URI.create("foo"), "test3", Status.RED, "broken"),
                new StatusValue(URI.create("foo"), "test4", Status.RED, "out of order")
        );

        StatusValue summary = StatusValue.summary(URI.create("foo"), new ArrayList<>(statusValues));
        assertNotNull(summary);
        assertEquals(Status.RED, summary.getStatus());
        assertEquals("foo test3: broken; foo test4: out of order", summary.getMessage());
    }

    @Test
    void skipsDisabledKPIs() {
        var child1 = new TestAssessable(null);
        child1.setStatusValue(new StatusValue(assessableGroup.getFullyQualifiedIdentifier(), "test", Status.GREEN, ""));
        child1.setStatusValue(new StatusValue(assessableGroup.getFullyQualifiedIdentifier(), "test2", Status.YELLOW, ""));
        var child2 = new TestAssessable(null);
        child2.setStatusValue(new StatusValue(assessableGroup.getFullyQualifiedIdentifier(), "test", Status.GREEN, ""));
        var parent = new TestAssessable(Set.of(child1, child2));

        Map<String, KPI> kpis = new HashMap<>();
        kpis.put("on", new TestKPI(component -> null, null) {
            @Override
            protected List<StatusValue> getStatusValues(@NonNull Assessable assessable, String value, String message) {
                return new ArrayList<>();
            }
        });
        var disabled = new TestKPI(component -> null, null) {
            @Override
            protected List<StatusValue> getStatusValues(@NonNull Assessable assessable, String value, String message) {
                throw new RuntimeException("This should never happen.");
            }
        };
        disabled.setEnabled(false);
        kpis.put("off", disabled);

        assertDoesNotThrow(() -> parent.applyKPIs(kpis));
    }

    @Test
    void withItem() {
        Item item = graph.getTestItem("foo", "bar");
        item.setLabel(Label.withPrefix(Label.status, "something", StatusValue.LABEL_SUFFIX_STATUS), Status.BROWN.getName());
        item.setLabel(Label.withPrefix(Label.status, "something", StatusValue.LABEL_SUFFIX_MESSAGE), "very bad");

        Map<String, KPI> kpis = new HashMap<>();
        kpis.put("on", new TestKPI(component -> null, null) {
            @Override
            protected List<StatusValue> getStatusValues(Assessable assessable, String value, String message) {
                return new ArrayList<>();
            }
        });

        //when
        Map<URI, List<StatusValue>> assessmentMap = item.applyKPIs(kpis);

        //then
        assertNotNull(assessmentMap);
        List<StatusValue> itemStatuses = assessmentMap.get(item.getFullyQualifiedIdentifier());
        assertNotNull(itemStatuses);

        StatusValue statusValue = itemStatuses.stream().filter(statusValue1 -> statusValue1.getField().equals(StatusValue.SUMMARY_FIELD_VALUE)).findFirst().orElse(null);
        assertNotNull(statusValue);

        StatusValue something = itemStatuses.stream().filter(statusValue1 -> statusValue1.getField().equals("something")).findFirst().orElse(null);
        assertNotNull(something);
        assertEquals("something", something.getField());
        assertEquals(Status.BROWN, something.getStatus());
        assertEquals("very bad", something.getMessage());
    }

    @Test
    void uniqueStatusLists() {
        Item item = graph.getTestItem("foo", "bar");
        item.setLabel(Label.withPrefix(Label.status, "something", StatusValue.LABEL_SUFFIX_STATUS), Status.BROWN.getName());
        item.setLabel(Label.withPrefix(Label.status, "something", StatusValue.LABEL_SUFFIX_MESSAGE), "very bad");

        Map<String, KPI> kpis = new HashMap<>();
        kpis.put("on", new TestKPI(component -> null, null) {
            @Override
            protected List<StatusValue> getStatusValues(@NonNull Assessable assessable, String value, String message) {
                return List.of(
                        new StatusValue(item.getFullyQualifiedIdentifier(), "something", Status.RED, "newer value")
                );
            }
        });

        //when
        Map<URI, List<StatusValue>> assessmentMap = item.applyKPIs(kpis);

        //then
        assertNotNull(assessmentMap);
        List<StatusValue> itemStatuses = assessmentMap.get(item.getFullyQualifiedIdentifier());
        assertNotNull(itemStatuses);

        assertThat(itemStatuses).hasSize(2);
        StatusValue something = itemStatuses.stream().filter(statusValue1 -> statusValue1.getField().equals("something")).findFirst().orElse(null);
        assertNotNull(something);
        assertEquals("newer value", something.getMessage());
    }

    @Test
    void groupSummary() {
        Item item = graph.getTestItem("foo", "bar");
        item.setLabel(Label.withPrefix(Label.status, "someKPI", StatusValue.LABEL_SUFFIX_STATUS), Status.BROWN.getName());
        item.setLabel(Label.withPrefix(Label.status, "someKPI", StatusValue.LABEL_SUFFIX_MESSAGE), "very bad");

        Item item2 = graph.getTestItem("foo", "baz");
        item2.setLabel(Label.withPrefix(Label.status, "someKPI", StatusValue.LABEL_SUFFIX_STATUS), Status.RED.getName());
        item2.setLabel(Label.withPrefix(Label.status, "someKPI", StatusValue.LABEL_SUFFIX_MESSAGE), "not so bad");

        Map<String, KPI> kpis = new HashMap<>();
        kpis.put("on", new TestKPI(component -> null, null) {
            @Override
            protected List<StatusValue> getStatusValues(@NonNull final Assessable assessable, String value, String message) {
                return new ArrayList<>();
            }
        });

        //when
        Map<URI, List<StatusValue>> groupStatuses = assessableGroup.applyKPIs(kpis);

        //then
        List<StatusValue> statusValues = groupStatuses.get(assessableGroup.getFullyQualifiedIdentifier());
        assertNotNull(statusValues);

        //group summary
        StatusValue statusValue = statusValues.stream().filter(statusValue1 -> statusValue1.getField().equals(StatusValue.SUMMARY_FIELD_VALUE)).findFirst().orElse(null);
        assertNotNull(statusValue);
        assertEquals(Status.BROWN, statusValue.getStatus());
        assertEquals(item.getFullyQualifiedIdentifier() + " somekpi: very bad", statusValue.getMessage());
    }

    @Test
    void isSorted() {
        Item item = graph.getTestItem("foo", "bar");
        item.setLabel(Label.withPrefix(Label.status, "foo", StatusValue.LABEL_SUFFIX_STATUS), Status.BROWN.getName());
        item.setLabel(Label.withPrefix(Label.status, "foo", StatusValue.LABEL_SUFFIX_MESSAGE), "very bad");

        item.setLabel(Label.withPrefix(Label.status, "bar", StatusValue.LABEL_SUFFIX_STATUS), Status.RED.getName());
        item.setLabel(Label.withPrefix(Label.status, "bar", StatusValue.LABEL_SUFFIX_MESSAGE), "not so bad");

        item.setLabel(Label.withPrefix(Label.status, "baz", StatusValue.LABEL_SUFFIX_STATUS), Status.ORANGE.getName());
        item.setLabel(Label.withPrefix(Label.status, "baz", StatusValue.LABEL_SUFFIX_MESSAGE), "not so bad");

        Map<String, KPI> kpis = new HashMap<>();
        kpis.put("on", new TestKPI(component -> null, null) {
            @Override
            protected List<StatusValue> getStatusValues(Assessable assessable, String value, String message) {
                return new ArrayList<>();
            }
        });

        //when
        Map<URI, List<StatusValue>> statuses = item.applyKPIs(kpis);

        //then
        List<StatusValue> statusValues = statuses.get(item.getFullyQualifiedIdentifier());
        assertNotNull(statusValues);
        assertEquals(4, statusValues.size());
        StatusValue summary = statusValues.get(0);
        StatusValue s1 = statusValues.get(1);
        StatusValue s2 = statusValues.get(2);
        StatusValue s3 = statusValues.get(3);

        assertThat(summary.getStatus().compareTo(s1.getStatus())).isEqualTo(0);
        assertThat(s1.getStatus().compareTo(s2.getStatus())).isEqualTo(1);
        assertThat(s2.getStatus().compareTo(s3.getStatus())).isEqualTo(1);
    }

    static class TestAssessable implements Assessable {

        private final Set<StatusValue> statusValues = new HashSet<>();
        private final Set<Assessable> children;

        TestAssessable(Set<Assessable> children) {
            this.children = children != null ? children : new HashSet<>();
        }

        @Override
        public URI getFullyQualifiedIdentifier() {
            return FullyQualifiedIdentifier.build(Item.class, "test", "default", "default", "group", "foo");
        }

        @NonNull
        @Override
        public Set<StatusValue> getAdditionalStatusValues() {
            return statusValues;
        }

        @NonNull
        @Override
        public Set<Assessable> getAssessables() {
            return children;
        }

        void setStatusValue(@NonNull StatusValue statusValue) {
            getAdditionalStatusValues().add(statusValue);
        }

        @NonNull
        public String getIdentifier() {
            return "test";
        }
    }

    private abstract static class TestKPI extends AbstractKPI {
        public TestKPI(Function<Assessable, String> valueFunction, Function<Assessable, String> msgFunction) {
            super(valueFunction, msgFunction);
        }

        @Override
        public Map<Status, RangeApiModel> getRanges() {
            return null;
        }

        @Override
        public Map<Status, List<String>> getMatches() {
            return null;
        }
    }
}