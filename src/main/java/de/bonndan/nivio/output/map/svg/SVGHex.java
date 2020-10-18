package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.output.map.hex.Hex;
import j2html.tags.DomContent;
import org.springframework.util.StringUtils;

import java.util.stream.Collectors;

/**
 * A hexagon.
 */
public class SVGHex extends Component {

    private final Hex hex;
    private final String fillId;
    private final String stroke;

    private boolean debug = false;

    public SVGHex(Hex hex, String fillId, String stroke) {
        this.hex = hex;
        this.fillId = fillId;
        this.stroke = stroke;
    }

    public DomContent render() {
        return SvgTagCreator.polygon()
                .condAttr(!StringUtils.isEmpty(stroke), "stroke", stroke)
                .condAttr(!StringUtils.isEmpty(fillId), "fill", fillId)
                .condAttr(!StringUtils.isEmpty(fillId), "fill-opacity", String.valueOf(0.1))
                .attr("stroke-width", 1)
                .condAttr(debug, "data-hex-coords", hex.q + "," + hex.r)
                .attr("points", asPoints(hex))
                ;
    }

    private String asPoints(Hex hex) {
        return hex.asPoints(Hex.HEX_SIZE).stream()
                .map(aDouble -> (int)aDouble.x + " " + (int)aDouble.y)
                .collect(Collectors.joining(","));
    }
}
