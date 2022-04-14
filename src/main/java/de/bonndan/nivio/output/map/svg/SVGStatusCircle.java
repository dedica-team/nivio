package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.assessment.StatusValue;
import j2html.tags.DomContent;
import org.springframework.lang.NonNull;

import java.util.Objects;

/**
 * Renders a simple circle containing the status color
 */
public class SVGStatusCircle extends Component {

    private final float radius;
    private final String stroke;
    private final StatusValue statusValue;
    private int cx = 0;
    private int cy = 0;

    public SVGStatusCircle(float radius,
                           @NonNull final String stroke,
                           @NonNull final StatusValue statusValue
    ) {
        this.radius = radius;
        this.stroke = Objects.requireNonNull(stroke);
        this.statusValue = Objects.requireNonNull(statusValue);
    }

    @Override
    public DomContent render() {
        return SvgTagCreator.circle()
                .attr(SVGAttr.CLASS, String.format("assessment %s", statusValue.getStatus().getName()))
                .attr("cx", cx)
                .attr("cy", cy)
                .attr("r", radius)
                .attr(SVGAttr.STROKE, stroke)
                .attr(SVGAttr.STROKE_WIDTH, 5)
                .attr(SVGAttr.FILL, statusValue.getStatus().getColor());
    }

    @NonNull
    public Component setCenter(int x, int y) {
        this.cx = x;
        this.cy = y;
        return this;
    }
}
