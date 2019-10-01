package de.bonndan.nivio.model;

import de.bonndan.nivio.input.dto.ItemDescription;
import org.junit.jupiter.api.Test;

import static junit.framework.TestCase.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FullyQualifiedIdentifierTest {

    @Test
    public void testEqualsWithGroup() {
        var fqi1 = FullyQualifiedIdentifier.build(null, "g1", "d1");

        ItemDescription desc1 = new ItemDescription();
        desc1.setIdentifier("d1");
        desc1.setGroup("g1");

        ItemDescription otherGroup = new ItemDescription();
        otherGroup.setIdentifier("d1");
        otherGroup.setGroup("g2");

        ItemDescription otherIdentifier = new ItemDescription();
        otherIdentifier.setIdentifier("d2");
        otherIdentifier.setGroup("g1");

        assertTrue(fqi1.isSimilarTo(desc1));
        assertFalse(fqi1.isSimilarTo(otherGroup));
        assertFalse(fqi1.isSimilarTo(otherIdentifier));
    }

    @Test
    public void testEqualsWithoutGroup() {
        var fqi1 = FullyQualifiedIdentifier.build(null, "g1", "d1");

        ItemDescription desc1 = new ItemDescription();
        desc1.setIdentifier("d1");
        desc1.setGroup(null);
        assertTrue(fqi1.isSimilarTo(desc1));

        ItemDescription otherGroup = new ItemDescription();
        otherGroup.setIdentifier("d1");
        otherGroup.setGroup("g2");

        var fqiNoGroup = FullyQualifiedIdentifier.build(null, null, "d1");
        assertTrue(fqiNoGroup.isSimilarTo(desc1));
        assertTrue(fqiNoGroup.isSimilarTo(otherGroup));
    }

    @Test
    public void testFrom() {

        String three = "a/b/c";
        FullyQualifiedIdentifier fqi = FullyQualifiedIdentifier.from(three);
        assertEquals("a", fqi.getLandscape());
        assertEquals("b", fqi.getGroup());
        assertEquals("c", fqi.getIdentifier());

        String two = "b/c";
        fqi = FullyQualifiedIdentifier.from(two);
        assertEquals("", fqi.getLandscape());
        assertEquals("b", fqi.getGroup());
        assertEquals("c", fqi.getIdentifier());

        String one = "c";
        fqi = FullyQualifiedIdentifier.from(one);
        assertEquals("", fqi.getLandscape());
        assertEquals(null, fqi.getGroup());
        assertEquals("c", fqi.getIdentifier());
    }
}
