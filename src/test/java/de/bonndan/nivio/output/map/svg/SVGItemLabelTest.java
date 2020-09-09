package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.LandscapeFactory;
import de.bonndan.nivio.model.LandscapeImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SVGItemLabelTest {

    @Test
    @DisplayName("ensure label uses proper fqi")
    public void regression184() {

        LandscapeImpl landscape = LandscapeFactory.create("l1");

        // item has no group
        Item foo = new Item(null, "foo");
        foo.setLandscape(landscape);


        SVGItemLabel svgItemLabel = new SVGItemLabel(foo);
        String render = svgItemLabel.render().render();
        assertTrue(render.contains("l1/common/foo"));
        assertFalse(render.contains("l1//foo"));
    }
}