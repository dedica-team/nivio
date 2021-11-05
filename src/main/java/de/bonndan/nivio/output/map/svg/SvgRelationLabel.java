package de.bonndan.nivio.output.map.svg;

import j2html.tags.ContainerTag;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.awt.geom.Point2D;
import java.util.Objects;

public class SvgRelationLabel extends Component {

    private final String transform;
    private final String text;
    private final String fillId;

    /**
     * Creates a new label object.
     *
     * @param text    the text to display
     * @param point   first point
     * @param point2  second point (diffed to first to determine angle)
     * @param fillId  color
     * @param upright flag to enforce text upright
     */
    public SvgRelationLabel(@NonNull final String text,
                            @NonNull final Point2D.Float point,
                            @NonNull final Point2D.Float point2,
                            @Nullable final String fillId,
                            final boolean upright
    ) {
        this.text = Objects.requireNonNull(text, "Label text is empty");
        Objects.requireNonNull(point, "First point is null");
        Objects.requireNonNull(point2, "Second point is null");

        this.fillId = fillId;

        var degrees = Math.atan2((point2.y - point.y), (point2.x - point.x)) * 180 / Math.PI;
        if (upright && (degrees > 90 || degrees < -90)) {
            degrees += 180; //always upright
        }
        transform = "translate(" + round(point.getX()) + ' ' + round(point.getY() - 10) + ") rotate(" + round(degrees) + " 0 0)";
    }

    public ContainerTag render() {
        return SvgTagCreator.text(text)
                .attr("x", 0)
                .attr("y", 0)
                .attr("text-anchor", "middle")
                .attr("font-size", "1.5em")
                .condAttr(StringUtils.hasLength(fillId), "fill", fillId)
                .attr("transform", transform);
    }
}
