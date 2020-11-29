package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.LandscapeFactory;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.output.layout.LayoutedComponent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;

import static org.junit.jupiter.api.Assertions.*;

class SVGItemLabelTest {

    @Test
    @DisplayName("ensure label uses proper fqi")
    public void regression184() {

        Landscape landscape = LandscapeFactory.create("l1");

        // item has no group
        Item foo = new Item(null, "foo");
        foo.setLandscape(landscape);


        SVGItem svgItem = new SVGItem(null, new LayoutedComponent(foo), new Point2D.Double(0,0));
        String render = svgItem.render().render();
        assertTrue(render.contains("l1/common/foo"));
        assertFalse(render.contains("l1//foo"));
    }
}