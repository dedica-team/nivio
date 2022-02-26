package de.bonndan.nivio.search;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Layer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ComponentMatcherTest {

    @Test
    void testEqualsWithGroup() {
        var fqi1 = ComponentMatcher.build("l1", null, null, "g1", "d1");

        ItemDescription desc1 = new ItemDescription();
        desc1.setIdentifier("d1");
        desc1.setGroup("g1");

        assertTrue(fqi1.isSimilarTo(desc1.getFullyQualifiedIdentifier()));
    }

    @Test
    void testNotEqualsWithGroup() {
        var matcher = ComponentMatcher.build("l1", null, null, "g1", "d1");

        ItemDescription otherGroup = new ItemDescription();
        otherGroup.setIdentifier("d1");
        otherGroup.setGroup("g2");

        ItemDescription otherIdentifier = new ItemDescription();
        otherIdentifier.setIdentifier("d2");
        otherIdentifier.setGroup("g1");

        assertFalse(matcher.isSimilarTo(otherGroup.getFullyQualifiedIdentifier()));
        assertFalse(matcher.isSimilarTo(otherIdentifier.getFullyQualifiedIdentifier()));
    }

    @Test
    void testEqualsWithoutGroup() {

        ItemDescription desc1 = new ItemDescription();
        desc1.setIdentifier("d1");
        desc1.setGroup(null);

        ItemDescription otherGroup = new ItemDescription();
        otherGroup.setIdentifier("d1");
        otherGroup.setGroup("g2");

        var fqi1 = ComponentMatcher.build(null, null, null, "g1", "d1");

        //when
        assertTrue(fqi1.isSimilarTo(desc1.getFullyQualifiedIdentifier()));

        assertFalse(fqi1.isSimilarTo(otherGroup.getFullyQualifiedIdentifier()));
    }

    @Test
    void testEqualsWithoutGroupInMatcher() {

        ItemDescription desc1 = new ItemDescription();
        desc1.setIdentifier("d1");
        desc1.setGroup(null);

        ItemDescription otherGroup = new ItemDescription();
        otherGroup.setIdentifier("d1");
        otherGroup.setGroup("g2");

        var fqiNoGroup = ComponentMatcher.build(null, null, null, null, "d1");
        var fqiUndefined = ComponentMatcher.build(null, null, null, FullyQualifiedIdentifier.UNDEFINED, "d1");

        //when
        assertTrue(fqiNoGroup.isSimilarTo(desc1.getFullyQualifiedIdentifier()));
        assertTrue(fqiUndefined.isSimilarTo(desc1.getFullyQualifiedIdentifier()));

        assertTrue(fqiNoGroup.isSimilarTo(otherGroup.getFullyQualifiedIdentifier()));
        assertTrue(fqiUndefined.isSimilarTo(otherGroup.getFullyQualifiedIdentifier()));
    }

    @Test
    void testEqualsWithCommonGroupAndNoGroup() {
        var fqi1 = ComponentMatcher.build(null, null, null, Layer.domain.name(), "d1");

        ItemDescription desc1 = new ItemDescription();
        desc1.setIdentifier("d1");
        desc1.setGroup(null);
        assertTrue(fqi1.isSimilarTo(desc1.getFullyQualifiedIdentifier()));
    }

    @Test
    void forTargetComplete() {

        String three = "a/b/c";
        ComponentMatcher fqi = ComponentMatcher.forTarget(three);
        assertEquals("a", fqi.getLandscape());
        assertEquals("b", fqi.getGroup());
        assertEquals("c", fqi.getItem());
    }

    @Test
    void forTargetGroupAndItem() {
        String two = "b/c";
        ComponentMatcher fqi = ComponentMatcher.forTarget(two);
        assertEquals("", fqi.getLandscape());
        assertEquals("b", fqi.getGroup());
        assertEquals("c", fqi.getItem());
    }

    @Test
    void forItem() {
        String one = "c";
        ComponentMatcher fqi = ComponentMatcher.forTarget(one);
        assertEquals("", fqi.getLandscape());
        assertEquals(null, fqi.getGroup());
        assertEquals("c", fqi.getItem());
    }

    @Test
    void forTargetWithURIAndClass() {
        String one = "item://_/_/_/g1/i1";
        ComponentMatcher fqi = ComponentMatcher.forTarget(one, Item.class);
        assertEquals(FullyQualifiedIdentifier.UNDEFINED, fqi.getLandscape());
        assertEquals("i1", fqi.getItem());
        assertEquals("g1", fqi.getGroup());
    }

    @Test
    void forTargetWithUnitAndContext() {
        String one = "context://_/unit/context";
        ComponentMatcher fqi = ComponentMatcher.forTarget(one, Item.class);
        assertEquals(FullyQualifiedIdentifier.UNDEFINED, fqi.getLandscape());
        assertEquals("context", fqi.getContext());
        assertEquals("unit", fqi.getUnit());
    }

    @Test
    void forTargetWithURI() {
        String one = "context://_/u1/c1";
        ComponentMatcher fqi = ComponentMatcher.forTarget(one);
        assertEquals(FullyQualifiedIdentifier.UNDEFINED, fqi.getLandscape());
        assertEquals("u1", fqi.getUnit());
        assertEquals("c1", fqi.getContext());
    }
}
