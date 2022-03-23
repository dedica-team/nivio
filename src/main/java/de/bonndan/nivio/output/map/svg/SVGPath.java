package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.output.map.hex.PathElement;
import j2html.tags.DomContent;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SVGPath extends Component {

    private final List<PathElement> pointsPath;
    private final String fillId;

    private Point2D.Double offset = new Point2D.Double(0,0);

    public SVGPath(@NonNull final List<PathElement> pointsPath, @Nullable final String fillId) {
        this.pointsPath = Objects.requireNonNull(pointsPath);
        this.fillId = fillId;
    }

    @Override
    protected void applyShift(Point2D.Double offset) {
        this.offset = offset;
    }

    @Override
    public DomContent render() {
        String path = pointsPath.stream()
                .map(pathElement -> pathElement.shift(offset).toString())
                .collect(Collectors.joining(" "));

        return SvgTagCreator.path()
                .attr("d", path)
                .attr(SVGAttr.FILL, "none")
                .condAttr(StringUtils.hasLength(fillId), SVGAttr.STROKE, fillId)
                .attr(SVGAttr.STROKE_WIDTH, 3);
    }
}
