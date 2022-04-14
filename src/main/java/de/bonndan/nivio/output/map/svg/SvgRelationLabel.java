package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.assessment.StatusValue;
import j2html.tags.ContainerTag;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.util.Objects;

import static de.bonndan.nivio.output.map.svg.SVGRenderer.DEFAULT_ICON_SIZE;

public class SvgRelationLabel extends Component {

    private final String textTransform;
    private final String text;
    private final String fillId;
    private final StatusValue statusValue;
    private final String transform;

    /**
     * Creates a new label object.
     *
     * @param text        the text to display
     * @param point       point and angle for the label
     * @param fillId      color
     * @param statusValue assessment status
     */
    public SvgRelationLabel(@Nullable final String text,
                            @NonNull final BezierPath.PointWithAngle point,
                            @Nullable final String fillId,
                            @Nullable final StatusValue statusValue
    ) {
        Objects.requireNonNull(point, "point is null");

        this.text = text;
        this.statusValue = statusValue != null ? statusValue : StatusValue.UNKNOWN;
        this.fillId = fillId;

        transform = "translate(" + round(point.point.getX()) + ' ' + round(point.point.getY()) + ")";
        textTransform = "rotate(" + round(point.degrees) + " 0 0)";
    }

    public ContainerTag render() {
        ContainerTag labelText = text != null ?
                SvgTagCreator.text(this.text)
                        .attr("x", 0)
                        .attr("y", -25)
                        .attr("text-anchor", "middle")
                        .attr("font-size", "1.5em")
                        .attr(SVGAttr.TRANSFORM, textTransform)
                        .condAttr(StringUtils.hasLength(fillId), "fill", fillId)
                : null;

        var statusCircle = new SVGStatusCircle(DEFAULT_ICON_SIZE / 3f, fillId, statusValue).render();

        return SvgTagCreator.g(statusCircle, labelText).attr(SVGAttr.TRANSFORM, transform);
    }
}
