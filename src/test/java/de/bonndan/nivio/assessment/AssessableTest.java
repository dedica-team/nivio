package de.bonndan.nivio.assessment;

import de.bonndan.nivio.assessment.kpi.AbstractKPI;
import de.bonndan.nivio.assessment.kpi.KPI;
import de.bonndan.nivio.model.Component;
import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Label;
import org.junit.jupiter.api.Test;
import org.springframework.lang.NonNull;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class AssessableTest {

    @Test
    void getSummaryHighestFromOwn() {
        var child1 = new TestAssessable(null);
        child1.setStatusValue(new StatusValue("test1", Status.GREEN));
        var child2 = new TestAssessable(null);
        child2.setStatusValue(new StatusValue("test2", Status.GREEN));
        var parent = new TestAssessable(List.of(child1, child2));
        parent.setStatusValue(new StatusValue("test3", Status.RED, "worst"));

        StatusValue summary = parent.getOverallStatus();
        assertNotNull(summary);
        assertEquals(Status.RED, summary.getStatus());
        assertEquals("worst", summary.getMessage());
        assertEquals("test3", summary.getMaxField());
    }

    @Test
    void getSummaryHighestFromChildren() {
        var child1 = new TestAssessable(null);
        child1.setStatusValue(new StatusValue("test", Status.GREEN));
        var child2 = new TestAssessable(null);
        child2.setStatusValue(new StatusValue("test", Status.RED));
        var parent = new TestAssessable(List.of(child1, child2));

        StatusValue summary = parent.getOverallStatus();
        assertNotNull(summary);
        assertEquals(Status.RED, summary.getStatus());
    }

    @Test
    void getSummaryHighestFromChildren2() {
        var child1 = new TestAssessable(null);
        child1.setStatusValue(new StatusValue("test", Status.GREEN));
        child1.setStatusValue(new StatusValue("test2", Status.YELLOW));
        var child2 = new TestAssessable(null);
        child2.setStatusValue(new StatusValue("test", Status.GREEN));
        var parent = new TestAssessable(List.of(child1, child2));

        StatusValue summary = parent.getOverallStatus();
        assertNotNull(summary);
        assertEquals("summary.test", summary.getMaxField());
        assertEquals(Status.YELLOW, summary.getStatus());
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