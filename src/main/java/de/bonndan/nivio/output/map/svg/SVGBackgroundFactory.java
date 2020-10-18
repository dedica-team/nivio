package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.output.map.hex.Hex;
import j2html.tags.ContainerTag;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.round;

public class SVGBackgroundFactory {

    private static final String HEX = "hex";

    public static ContainerTag getHex() {
        ContainerTag fullHex = (ContainerTag) new SVGHex(new Hex(0, 0), "none", "#cccccc")
                .render();
        fullHex.attr("id", "hex");
        return fullHex;
    }

    public static List<ContainerTag> getBackgroundTiles(int minQ, int maxQ, int minR, int maxR, int height) {
        //render background hexes
        List<ContainerTag> background = new ArrayList<>();
        var i = 0;
        for (int q = minQ; q <= maxQ; q++) {
            for (int r = minR - i; r < (maxR + maxQ - q); r++) {
                Point2D.Double hex = new Hex(q, r).toPixel();
                float y = round((hex.y + 146)*10f)/10f ; //TODO why 146? without this bg hexes are displaced
                if (y < 0 || y > height) continue;
                ContainerTag use = SvgTagCreator.use("#" + HEX)
                        .attr("x", (int) hex.x - 2 * Hex.HEX_SIZE)
                        .attr("y", y);
                background.add(use);
            }
            i++;
        }

        return background;
    }
}
