package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.LandscapeImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;

import static org.junit.jupiter.api.Assertions.*;

class SVGItemTest {

    @Test
    @DisplayName("ensure item uses proper fqi as id")
    public void regression184() {
        LandscapeImpl landscape = new LandscapeImpl();
        landscape.setIdentifier("l1");

        // item has no group
        Item foo = new Item();
        foo.setIdentifier("foo");
        foo.setLandscape(landscape);

        SVGItem svgItem = new SVGItem(null, foo, new Point2D.Double(1,1));
        assertTrue( svgItem.render().render().contains("l1/common/foo"));
    }
}