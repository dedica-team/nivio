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

    public SVGHex(Hex hex, String fillId) {
        this.hex = hex;
        this.fillId = fillId;
    }

    public DomContent render() {
        return SvgTagCreator.polygon()
                .condAttr(!StringUtils.isEmpty(fillId), "stroke", fillId)
                .condAttr(!StringUtils.isEmpty(fillId), "fill", fillId)
                .condAttr(!StringUtils.isEmpty(fillId), "fill-opacity", String.valueOf(0.1))
                .attr("stroke-width", 2)
                .attr("points", asPoints(hex));
    }

    private String asPoints(Hex hex) {
        return hex.asPoints(Hex.HEX_SIZE - 1).stream()
                .map(aDouble -> aDouble.x + " " + aDouble.y)
                .collect(Collectors.joining(","));
    }
}
