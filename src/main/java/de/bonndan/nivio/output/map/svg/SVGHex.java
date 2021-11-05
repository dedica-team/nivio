package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.output.map.hex.Hex;
import j2html.tags.DomContent;
import org.springframework.lang.NonNull;
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

    public SVGHex(@NonNull final Hex hex, String fillId, String stroke) {
        this.hex = hex;
        this.fillId = fillId;
        this.stroke = stroke;
    }

    public SVGHex(@NonNull final Hex hex, String fillId, String stroke, boolean debug) {
        this(hex, fillId, stroke);
        this.debug = debug;
    }

    public DomContent render() {
        return SvgTagCreator.polygon()
                .attr("stroke-width", 1)
                .attr("points", asPoints(hex))
                .condAttr(StringUtils.hasLength(stroke), "stroke", stroke)
                .condAttr(StringUtils.hasLength(fillId), "fill", fillId)
                .condAttr(StringUtils.hasLength(fillId), "fill-opacity", String.valueOf(0.4))
                .condAttr(debug, "data-hex-coords", hex.q + "," + hex.r)
                ;
    }

    private String asPoints(Hex hex) {
        return hex.asPoints(Hex.HEX_SIZE).stream()
                .map(aDouble -> round(aDouble.x) + " " + round(aDouble.y))
                .collect(Collectors.joining(","));
    }
}
