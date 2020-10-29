package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.output.map.hex.Hex;
import j2html.tags.ContainerTag;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.round;

/**
 * This generates the background hex tiles.
 *
 */
public class SVGBackgroundFactory {

    private static final String HEX = "hex";

    public static ContainerTag getHex() {
        ContainerTag fullHex = (ContainerTag) new SVGHex(new Hex(0, 0), "none", "#cccccc")
                .render();
        fullHex.attr("id", "hex");
        return fullHex;
    }

    /**
     * @param dimension svg dimesion, containing bounding boxes
     * @return a set of "use" references to a background hex
     */
    public static List<ContainerTag> getBackgroundTiles(SVGDimension dimension) {

        //render background hexes
        List<ContainerTag> background = new ArrayList<>();
        var i = 0;
        //add extra space for relations being drawn outside of group areas (which define the outer borders)
        int horMin = dimension.hex.horMin - 1;
        int horMax = dimension.hex.horMax + 1;


        final int yOffset = Hex.HEX_SIZE / 4; //why? without this bg hexes are displaced

        for (int q = horMin; q <= horMax; q++) {
            //the correction "+verticalmax -q" is because q and r are not orthogonal like x and y
            for (int r = dimension.hex.vertMin - i; r < (horMax + dimension.hex.vertMax - q); r++) {
                Point2D.Double hex = new Hex(q, r).toPixel();
                int x = (int) hex.x - 2 * Hex.HEX_SIZE;
                float y = (float) round((hex.y - yOffset)*10f)/10f ;
                if (y < dimension.cartesian.vertMin - dimension.cartesian.padding) {
                    continue;
                }

                if (y > dimension.cartesian.vertMax) {
                    continue;
                }

                ContainerTag use = SvgTagCreator.use("#" + HEX)
                        .attr("x", x)
                        .attr("y", y);
                background.add(use);
            }
            i++;
        }

        return background;
    }
}
