package de.bonndan.nivio.assessment;

import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

    class TestAssessable implements Assessable {

        private Set<StatusValue> statusValues = new HashSet<>();
        private final List<? extends Assessable> children;

        TestAssessable(List<? extends Assessable> children) {
            this.children = children != null ? children : new ArrayList<>();
        }

        @Override
        public Set<StatusValue> getStatusValues() {
            return statusValues;
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
        public List<? extends Assessable> getChildren() {
            return children;
        }
    }
}