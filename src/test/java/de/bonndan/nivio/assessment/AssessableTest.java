package de.bonndan.nivio.assessment;

import de.bonndan.nivio.assessment.kpi.AbstractKPI;
import de.bonndan.nivio.assessment.kpi.KPI;
import de.bonndan.nivio.output.dto.RangeApiModel;
import de.bonndan.nivio.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.lang.NonNull;

import java.util.*;
import java.util.function.Function;

import static de.bonndan.nivio.assessment.Assessable.getWorst;
import static de.bonndan.nivio.model.ItemFactory.getTestItem;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AssessableTest {

    @Test
    void getSummaryHighest() {

        var child1 = new TestAssessable(null);

        List<StatusValue> statusValues = List.of(
                new StatusValue("foo", "test1", Status.GREEN, ""),
                new StatusValue("foo", "test2", Status.GREEN, ""),
                new StatusValue("foo", "test2", Status.ORANGE, ""),
                new StatusValue("foo", "test3", Status.RED, "broken"),
                new StatusValue("foo", "test4", Status.RED, "out of order")
        );

        List<StatusValue> max = getWorst(new ArrayList<>(statusValues));

        StatusValue summary = StatusValue.summary("foo", max);
        assertNotNull(summary);
        assertEquals(Status.RED, summary.getStatus());
        assertEquals("foo test3: broken; foo test4: out of order", summary.getMessage());
    }

    @Test
    void skipsDisabledKPIs() {
        var child1 = new TestAssessable(null);
        child1.setStatusValue(new StatusValue("foo", "test", Status.GREEN, ""));
        child1.setStatusValue(new StatusValue("foo", "test2", Status.YELLOW, ""));
        var child2 = new TestAssessable(null);
        child2.setStatusValue(new StatusValue("foo", "test", Status.GREEN, ""));
        var parent = new TestAssessable(List.of(child1, child2));

        Map<String, KPI> kpis = new HashMap<>();
        kpis.put("on", new TestKPI(component -> null, null) {
            @Override
            protected List<StatusValue> getStatusValues(Assessable assessable, String value, String message) {
                return new ArrayList<>();
            }
        });
        var disabled = new TestKPI(component -> null, null) {
            @Override
            protected List<StatusValue> getStatusValues(Assessable assessable, String value, String message) {
                throw new RuntimeException("This should never happen.");
            }
        };
        disabled.setEnabled(false);
        kpis.put("off", disabled);

        assertDoesNotThrow(() -> parent.applyKPIs(kpis));
    }

    @Test
    void withItem() {
        Item item = getTestItem("foo", "bar");
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
        Map<String, List<StatusValue>> assessmentMap = item.applyKPIs(kpis);

        //then
        assertNotNull(assessmentMap);
        List<StatusValue> itemStatuses = assessmentMap.get(item.getFullyQualifiedIdentifier().toString());
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
        Item item = getTestItem("foo", "bar");
        item.setLabel(Label.withPrefix(Label.status, "something", StatusValue.LABEL_SUFFIX_STATUS), Status.BROWN.getName());
        item.setLabel(Label.withPrefix(Label.status, "something", StatusValue.LABEL_SUFFIX_MESSAGE), "very bad");

        Map<String, KPI> kpis = new HashMap<>();
        kpis.put("on", new TestKPI(component -> null, null) {
            @Override
            protected List<StatusValue> getStatusValues(Assessable assessable, String value, String message) {
                return List.of(
                        new StatusValue(item.getAssessmentIdentifier(), "something", Status.RED, "newer value")
                );
            }
        });

        //when
        Map<String, List<StatusValue>> assessmentMap = item.applyKPIs(kpis);

        //then
        assertNotNull(assessmentMap);
        List<StatusValue> itemStatuses = assessmentMap.get(item.getFullyQualifiedIdentifier().toString());
        assertNotNull(itemStatuses);

        assertThat(itemStatuses).hasSize(2);
        StatusValue something = itemStatuses.stream().filter(statusValue1 -> statusValue1.getField().equals("something")).findFirst().orElse(null);
        assertNotNull(something);
        assertEquals("newer value", something.getMessage());
    }

    @Test
    void groupSummary() {
        Item item = getTestItem("foo", "bar");
        item.setLabel(Label.withPrefix(Label.status, "someKPI", StatusValue.LABEL_SUFFIX_STATUS), Status.BROWN.getName());
        item.setLabel(Label.withPrefix(Label.status, "someKPI", StatusValue.LABEL_SUFFIX_MESSAGE), "very bad");

        Item item2 = getTestItem("foo", "baz");
        item2.setLabel(Label.withPrefix(Label.status, "someKPI", StatusValue.LABEL_SUFFIX_STATUS), Status.RED.getName());
        item2.setLabel(Label.withPrefix(Label.status, "someKPI", StatusValue.LABEL_SUFFIX_MESSAGE), "not so bad");

        Group foo = new Group("foo", "test");
        foo.addOrReplaceItem(item);
        foo.addOrReplaceItem(item2);

        AssessableGroup assessableGroup = new AssessableGroup(foo, Set.of(item, item2));

        Map<String, KPI> kpis = new HashMap<>();
        kpis.put("on", new TestKPI(component -> null, null) {
            @Override
            protected List<StatusValue> getStatusValues(@NonNull final Assessable assessable, String value, String message) {
                return new ArrayList<>();
            }
        });

        //when
        Map<String, List<StatusValue>> groupStatuses = assessableGroup.applyKPIs(kpis);

        //then
        List<StatusValue> statusValues = groupStatuses.get(assessableGroup.getAssessmentIdentifier());
        assertNotNull(statusValues);

        //group summary
        StatusValue statusValue = statusValues.stream().filter(statusValue1 -> statusValue1.getField().equals(StatusValue.SUMMARY_FIELD_VALUE)).findFirst().orElse(null);
        assertNotNull(statusValue);
        assertEquals(Status.BROWN, statusValue.getStatus());
        assertEquals("test/foo/bar somekpi: very bad", statusValue.getMessage());
    }

    @Test
    void isSorted() {
        Item item = getTestItem("foo", "bar");
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
        Map<String, List<StatusValue>> statuses = item.applyKPIs(kpis);

        //then
        List<StatusValue> statusValues = statuses.get(item.getFullyQualifiedIdentifier().toString());
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
        private final List<? extends Assessable> children;

        TestAssessable(List<? extends Assessable> children) {
            this.children = children != null ? children : new ArrayList<>();
        }

        @Override
        public Set<StatusValue> getAdditionalStatusValues() {
            return statusValues;
        }

        void setStatusValue(@NonNull StatusValue statusValue) {
            getAdditionalStatusValues().add(statusValue);
        }

        @NonNull
        public String getIdentifier() {
            return "test";
        }

        @Override
        public String getAssessmentIdentifier() {
            return "test";
        }

        @Override
        public List<? extends Assessable> getChildren() {
            return children;
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