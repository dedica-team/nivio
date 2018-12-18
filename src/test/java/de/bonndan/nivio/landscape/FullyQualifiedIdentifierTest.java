package de.bonndan.nivio.landscape;

import de.bonndan.nivio.input.dto.ServiceDescription;
import org.junit.jupiter.api.Test;

import static junit.framework.TestCase.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FullyQualifiedIdentifierTest {

    @Test
    public void testEqualsWithGroup() {
        var fqi1 = FullyQualifiedIdentifier.build(null, "g1", "d1");

        ServiceDescription desc1 = new ServiceDescription();
        desc1.setIdentifier("d1");
        desc1.setGroup("g1");

        ServiceDescription otherGroup = new ServiceDescription();
        otherGroup.setIdentifier("d1");
        otherGroup.setGroup("g2");

        ServiceDescription otherIdentifier = new ServiceDescription();
        otherIdentifier.setIdentifier("d2");
        otherIdentifier.setGroup("g1");

        assertTrue(fqi1.isSimilarTo(desc1));
        assertFalse(fqi1.isSimilarTo(otherGroup));
        assertFalse(fqi1.isSimilarTo(otherIdentifier));
    }

    @Test
    public void testEqualsWithoutGroup() {
        var fqi1 = FullyQualifiedIdentifier.build(null, "g1", "d1");

        ServiceDescription desc1 = new ServiceDescription();
        desc1.setIdentifier("d1");
        desc1.setGroup(null);
        assertTrue(fqi1.isSimilarTo(desc1));

        ServiceDescription otherGroup = new ServiceDescription();
        otherGroup.setIdentifier("d1");
        otherGroup.setGroup("g2");

        var fqiNoGroup = FullyQualifiedIdentifier.build(null, null, "d1");
        assertTrue(fqiNoGroup.isSimilarTo(desc1));
        assertTrue(fqiNoGroup.isSimilarTo(otherGroup));
    }
}
