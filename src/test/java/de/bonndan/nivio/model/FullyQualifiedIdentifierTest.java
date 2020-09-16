package de.bonndan.nivio.model;

import de.bonndan.nivio.input.dto.ItemDescription;
import org.junit.jupiter.api.Test;

import static junit.framework.TestCase.assertFalse;
import static org.junit.jupiter.api.Assertions.*;

public class FullyQualifiedIdentifierTest {

    @Test
    public void testToString() {
        var fqi1 = FullyQualifiedIdentifier.build(null, "g1", "d1");
        assertEquals("/g1/d1", fqi1.toString());

        var fqi2 = FullyQualifiedIdentifier.build("l1", "g1", "d1");
        assertEquals("l1/g1/d1", fqi2.toString());

        var fqi3 = FullyQualifiedIdentifier.build("l1", "g1", null);
        assertEquals("l1/g1", fqi3.toString());

        var fqi4 = FullyQualifiedIdentifier.build("l1", null, null);
        assertEquals("l1", fqi4.toString());
    }

    @Test
    public void testJsonValue() {
        var fqi1 = FullyQualifiedIdentifier.build(null, "g1", "d1");
        assertEquals("", fqi1.jsonValue());

        var fqi2 = FullyQualifiedIdentifier.build("l1", "g1", "d1");
        assertEquals("l1/g1/d1", fqi2.jsonValue());

        var fqi3 = FullyQualifiedIdentifier.build("l1", "g1", null);
        assertEquals("l1/g1", fqi3.jsonValue());

        var fqi4 = FullyQualifiedIdentifier.build("l1", null, null);
        assertEquals("l1", fqi4.jsonValue());

        var fqi5 = FullyQualifiedIdentifier.build("l1", null, "d1");
        assertEquals("l1/" + Group.COMMON + "/d1", fqi5.jsonValue());

    }

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
    public void fromComplete() {

        String three = "a/b/c";
        FullyQualifiedIdentifier fqi = FullyQualifiedIdentifier.from(three);
        assertEquals("a", fqi.getLandscape());
        assertEquals("b", fqi.getGroup());
        assertEquals("c", fqi.getItem());
    }

    @Test
    public void fromLandscapeAndGroup() {
        String two = "b/c";
        FullyQualifiedIdentifier fqi = FullyQualifiedIdentifier.from(two);
        assertEquals("b", fqi.getLandscape());
        assertEquals("c", fqi.getGroup());
        assertNull(fqi.getItem());
    }
    @Test
    public void fromLandscape() {
        String one = "c";
        FullyQualifiedIdentifier fqi = FullyQualifiedIdentifier.from(one);
        assertEquals("c", fqi.getLandscape());
        assertNull(fqi.getGroup());
        assertNull(fqi.getItem());
    }
}
