package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.model.LandscapeFactory;
import de.bonndan.nivio.output.layout.LayoutedComponent;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;
import java.util.List;

import static de.bonndan.nivio.output.map.svg.SVGDocument.DATA_IDENTIFIER;
import static de.bonndan.nivio.output.map.svg.SVGDocument.VISUAL_FOCUS_UNSELECTED;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SVGItemTest {

    private Item foo;

    @BeforeEach
    void setup() {
        Landscape landscape = LandscapeFactory.createForTesting("l1", "l1Landscape").build();
        // item has no group
        foo = new Item("foo", landscape, Group.COMMON, null, null, null,
                null, null, null, null, null);
    }

    @Test
    @DisplayName("ensure item uses proper fqi as id")
    void regression184() {

        SVGItem svgItem = new SVGItem(null, new LayoutedComponent(foo), List.of(), new Point2D.Double(1, 1));
        assertThat(svgItem.render().render()).contains("l1/common/foo");
    }

    @Test
    @DisplayName("contains x and y data")
    void xyData() {
        SVGItem svgItem = new SVGItem(null, new LayoutedComponent(foo), List.of(), new Point2D.Double(1, 2.0303030));

        assertTrue(svgItem.render().render().contains("data-x=\"1.00\""));
        assertTrue(svgItem.render().render().contains("data-y=\"2.03\""));
    }

    @Test
    void supportsVisualFocus() {

        SVGItem svgItem = new SVGItem(null, new LayoutedComponent(foo), List.of(), new Point2D.Double(1, 2.0303030));

        //then
        String render1 = svgItem.render().render();
        Assertions.assertThat(render1)
                .contains(DATA_IDENTIFIER)
                .contains(VISUAL_FOCUS_UNSELECTED);
    }
}