package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.output.map.hex.Hex;
import j2html.tags.DomContent;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.awt.geom.Point2D;
import java.util.StringJoiner;

/**
 * A hexagon.
 */
public class SVGHex extends Component {

    private final Hex hex;
    private final String fillId;
    private final String stroke;

    private boolean debug = false;
    private Point2D.Double offset = new Point2D.Double(0, 0);

    public SVGHex(@NonNull final Hex hex, String fillId, String stroke) {
        this.hex = hex;
        this.fillId = fillId;
        this.stroke = stroke;
    }

    public SVGHex(@NonNull final Hex hex, String fillId, String stroke, boolean debug) {
        this(hex, fillId, stroke);
        this.debug = debug;
    }

    @Override
    protected void applyShift(Point2D.Double offset) {
        this.offset = offset;
    }

    public DomContent render() {
        return SvgTagCreator.polygon()
                .attr(SVGAttr.STROKE_WIDTH, 1)
                .attr("points", asPoints(hex))
                .condAttr(StringUtils.hasLength(stroke), SVGAttr.STROKE, stroke)
                .condAttr(StringUtils.hasLength(fillId), SVGAttr.FILL, fillId)
                .condAttr(StringUtils.hasLength(fillId), SVGAttr.FILL_OPACITY, String.valueOf(0.4))
                .condAttr(debug, "data-hex-coords", hex.q + "," + hex.r)
                ;
    }

    private String asPoints(Hex hex) {
        StringJoiner joiner = new StringJoiner(",");
        for (Point2D.Double aDouble : hex.asPoints(Hex.HEX_SIZE)) {
            String s = round(aDouble.x + offset.x) + " " + round(aDouble.y + offset.y);
            joiner.add(s);
        }
        return joiner.toString();
    }
}
