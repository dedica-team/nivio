package de.bonndan.nivio.model;

import de.bonndan.nivio.input.dto.ItemDescription;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ItemMatcherTest {

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
    public void testEqualsWithGroup() {
        var fqi1 = ItemMatcher.build(null, "g1", "d1");

        ItemDescription desc1 = new ItemDescription();
        desc1.setIdentifier("d1");
        desc1.setGroup("g1");

        ItemDescription otherGroup = new ItemDescription();
        otherGroup.setIdentifier("d1");
        otherGroup.setGroup("g2");

        ItemDescription otherIdentifier = new ItemDescription();
        otherIdentifier.setIdentifier("d2");
        otherIdentifier.setGroup("g1");

        assertTrue(fqi1.isSimilarTo(desc1.getFullyQualifiedIdentifier()));
        assertFalse(fqi1.isSimilarTo(otherGroup.getFullyQualifiedIdentifier()));
        assertFalse(fqi1.isSimilarTo(otherIdentifier.getFullyQualifiedIdentifier()));
    }

    @Test
    public void testEqualsWithoutGroup() {
        var fqi1 = ItemMatcher.build(null, "g1", "d1");

        ItemDescription desc1 = new ItemDescription();
        desc1.setIdentifier("d1");
        desc1.setGroup(null);
        assertTrue(fqi1.isSimilarTo(desc1.getFullyQualifiedIdentifier()));

        ItemDescription otherGroup = new ItemDescription();
        otherGroup.setIdentifier("d1");
        otherGroup.setGroup("g2");

        var fqiNoGroup = ItemMatcher.build(null, null, "d1");
        assertTrue(fqiNoGroup.isSimilarTo(desc1.getFullyQualifiedIdentifier()));
        assertTrue(fqiNoGroup.isSimilarTo(otherGroup.getFullyQualifiedIdentifier()));
    }

    @Test
    public void forTargetComplete() {

        String three = "a/b/c";
        ItemMatcher fqi = ItemMatcher.forTarget(three);
        assertEquals("a", fqi.getLandscape());
        assertEquals("b", fqi.getGroup());
        assertEquals("c", fqi.getItem());
    }

    @Test
    public void forTargetGroupAndItem() {
        String two = "b/c";
        ItemMatcher fqi = ItemMatcher.forTarget(two);
        assertEquals("", fqi.getLandscape());
        assertEquals("b", fqi.getGroup());
        assertEquals("c", fqi.getItem());
    }
    @Test
    public void forItem() {
        String one = "c";
        ItemMatcher fqi = ItemMatcher.forTarget(one);
        assertEquals("", fqi.getLandscape());
        assertEquals(null, fqi.getGroup());
        assertEquals("c", fqi.getItem());
    }
}
