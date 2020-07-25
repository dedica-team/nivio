package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.output.map.hex.Hex;
import j2html.tags.DomContent;

import java.util.stream.Collectors;

/**
 * A hexagon.
 */
public class SVGHex extends Component {

    public static final int HEX_SIZE = 100;
    private final Hex hex;
    private final String fillId;

    public SVGHex(Hex hex, String fillId) {
        this.hex = hex;
        this.fillId = fillId;
    }

    public DomContent render() {
        return SvgTagCreator.polygon()
                .attr("stroke", fillId)
                .attr("fill", fillId)
                .attr("fill-opacity", 0.1)
                .attr("stroke-width", 2)
                .attr("points", asPoints(hex));
    }

    private String asPoints(Hex hex) {
        return hex.asPoints(HEX_SIZE - 1).stream()
                .map(aDouble -> aDouble.x + " " + aDouble.y)
                .collect(Collectors.joining(","));
    }
}
