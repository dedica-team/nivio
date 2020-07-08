package de.bonndan.nivio.assessment;

import de.bonndan.nivio.assessment.kpi.AbstractKPI;
import de.bonndan.nivio.assessment.kpi.KPI;
import de.bonndan.nivio.model.Component;
import de.bonndan.nivio.model.FullyQualifiedIdentifier;
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
        parent.setStatusValue(new StatusValue("test3", Status.RED));

        StatusValue summary = parent.getOverallStatus();
        assertNotNull(summary);
        assertEquals(Status.RED, summary.getStatus());
        assertEquals("test3", summary.getMessage());
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
        assertEquals("summary.test", summary.getMessage());
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
        public List<? extends Assessable> getChildren() {
            return children;
        }
    }
}