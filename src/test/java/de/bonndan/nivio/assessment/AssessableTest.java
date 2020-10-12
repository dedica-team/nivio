package de.bonndan.nivio.assessment;

import de.bonndan.nivio.assessment.kpi.AbstractKPI;
import de.bonndan.nivio.assessment.kpi.KPI;
import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Label;
import org.junit.jupiter.api.Test;
import org.springframework.lang.NonNull;

import java.util.*;

import static de.bonndan.nivio.assessment.StatusValue.SUMMARY_LABEL;
import static org.junit.jupiter.api.Assertions.*;

class AssessableTest {

    @Test
    void getSummaryHighest() {

        var child1 = new TestAssessable(null);

        List<StatusValue> statusValues = List.of(
                new StatusValue("test1", Status.GREEN),
                new StatusValue("test2", Status.GREEN),
                new StatusValue("test3", Status.RED, "worst")
        );

        StatusValue max = statusValues.stream()
                .filter(Objects::nonNull)
                .max(new StatusValue.Comparator())
                .orElse(new StatusValue(SUMMARY_LABEL, Status.UNKNOWN));

        StatusValue summary = StatusValue.summary(SUMMARY_LABEL + "." + child1.getIdentifier(), max);
        assertNotNull(summary);
        assertEquals(Status.RED, summary.getStatus());
        assertEquals("worst", summary.getMessage());
        assertEquals("test3", summary.getMaxField());
    }

    @Test
    void skipsDisabledKPIs() {
        var child1 = new TestAssessable(null);
        child1.setStatusValue(new StatusValue("test", Status.GREEN));
        child1.setStatusValue(new StatusValue("test2", Status.YELLOW));
        var child2 = new TestAssessable(null);
        child2.setStatusValue(new StatusValue("test", Status.GREEN));
        var parent = new TestAssessable(List.of(child1, child2));

        Map<String, KPI> kpis = new HashMap<>();
        kpis.put("on", new AbstractKPI(component -> null, null) {
            @Override
            protected List<StatusValue> getStatusValues(String value, String message) {
                return new ArrayList<>();
            }
        });
        var disabled = new AbstractKPI(component -> null, null) {
            @Override
            protected List<StatusValue> getStatusValues(String value, String message) {
                throw new RuntimeException("This should never happen.");
            }
        };
        disabled.setEnabled(false);
        kpis.put("off", disabled);

        assertDoesNotThrow(() -> parent.applyKPIs(kpis));
    }

    @Test
    public void withItem() {
        Item item = new Item("foo", "bar");
        item.setLabel(Label.key(Label.status, "something", StatusValue.LABEL_SUFFIX_STATUS), Status.BROWN.getName());
        item.setLabel(Label.key(Label.status, "something", StatusValue.LABEL_SUFFIX_MESSAGE), "very bad");

        Map<String, KPI> kpis = new HashMap<>();
        kpis.put("on", new AbstractKPI(component -> null, null) {
            @Override
            protected List<StatusValue> getStatusValues(String value, String message) {
                return new ArrayList<>();
            }
        });

        //when
        Map<FullyQualifiedIdentifier, List<StatusValue>> assessmentMap = item.applyKPIs(kpis);

        //then
        assertNotNull(assessmentMap);
        List<StatusValue> itemStatuses = assessmentMap.get(item.getFullyQualifiedIdentifier());
        assertNotNull(itemStatuses);

        StatusValue statusValue = itemStatuses.stream().filter(statusValue1 -> statusValue1.getField().equals("summary.bar")).findFirst().orElse(null);
        assertNotNull(statusValue);
        assertEquals("summary.bar", statusValue.getField());

        StatusValue something = itemStatuses.stream().filter(statusValue1 -> statusValue1.getField().equals("something")).findFirst().orElse(null);
        assertNotNull(something);
        assertEquals("something", something.getField());
        assertEquals(Status.BROWN, something.getStatus());
        assertEquals("very bad", something.getMessage());
    }

    @Test
    public void groupSummary() {
        Item item = new Item("foo", "bar");
        item.setLabel(Label.key(Label.status, "something", StatusValue.LABEL_SUFFIX_STATUS), Status.BROWN.getName());
        item.setLabel(Label.key(Label.status, "something", StatusValue.LABEL_SUFFIX_MESSAGE), "very bad");

        Item item2 = new Item("foo", "baz");
        item2.setLabel(Label.key(Label.status, "something", StatusValue.LABEL_SUFFIX_STATUS), Status.RED.getName());
        item2.setLabel(Label.key(Label.status, "something", StatusValue.LABEL_SUFFIX_MESSAGE), "not so bad");

        Group foo = new Group("foo");
        foo.addItem(item);
        foo.addItem(item2);

        Map<String, KPI> kpis = new HashMap<>();
        kpis.put("on", new AbstractKPI(component -> null, null) {
            @Override
            protected List<StatusValue> getStatusValues(String value, String message) {
                return new ArrayList<>();
            }
        });

        //when
        Map<FullyQualifiedIdentifier, List<StatusValue>> groupStatuses = foo.applyKPIs(kpis);

        //then
        List<StatusValue> statusValues = groupStatuses.get(foo.getFullyQualifiedIdentifier());
        assertNotNull(statusValues);

        //group summary
        StatusValue statusValue = statusValues.stream().filter(statusValue1 -> statusValue1.getField().equals("summary.foo")).findFirst().orElse(null);
        assertNotNull(statusValue);
        assertEquals("summary.foo", statusValue.getField());
        assertEquals(Status.BROWN, statusValue.getStatus());
        assertEquals("very bad", statusValue.getMessage());
        assertEquals("something", statusValue.getMaxField());
    }


    class TestAssessable implements Assessable {

        private Set<StatusValue> statusValues = new HashSet<>();
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

        @Override
        public String getIdentifier() {
            return "test";
        }

        @Override
        public FullyQualifiedIdentifier getFullyQualifiedIdentifier() {
            return FullyQualifiedIdentifier.build("test", null, null);
        }

        @Override
        public String getName() {
            return "test";
        }

        @Override
        public String getContact() {
            return "test";
        }

        @Override
        public String getDescription() {
            return "test";
        }

        @Override
        public String getOwner() {
            return null;
        }

        @Override
        public String getIcon() {
            return null;
        }

        @Override
        public String getColor() {
            return null;
        }

        @Override
        public List<? extends Assessable> getChildren() {
            return children;
        }
    }
}