package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.LandscapeImpl;
import de.bonndan.nivio.model.Relation;
import de.bonndan.nivio.model.RelationItem;
import j2html.tags.DomContent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SVGRelationTest {

    @Test
    @DisplayName("items without groups use proper fqi")
    public void regression184() {

        LandscapeImpl landscape = new LandscapeImpl();
        landscape.setIdentifier("l1");

        //both items have no group
        Item foo = new Item();
        foo.setIdentifier("foo");
        foo.setLandscape(landscape);

        Item bar = new Item();
        bar.setIdentifier("bar");
        bar.setLandscape(landscape);

        RelationItem<Item> itemRelationItem = new Relation(foo, bar);
        SVGRelation svgRelation = new SVGRelation(new HexPath(new ArrayList<>()), "", itemRelationItem);
        DomContent render = svgRelation.render();
        String render1 = render.render();
        assertTrue(render1.contains("l1/common/foo"));
        assertFalse(render1.contains("l1//foo"));
        assertTrue(render1.contains("l1/common/bar"));
        assertFalse(render1.contains("l1//bar"));
    }

}