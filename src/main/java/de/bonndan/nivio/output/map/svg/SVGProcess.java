package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.model.Process;
import de.bonndan.nivio.output.Color;
import j2html.tags.DomContent;
import org.springframework.lang.NonNull;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static de.bonndan.nivio.output.map.svg.SVGDocument.VISUAL_FOCUS_UNSELECTED;
import static de.bonndan.nivio.output.map.svg.SvgTagCreator.g;

/**
 * Renders a process as group of relations.
 *
 * Reuses shifted {@link SVGRelation}s.
 */
public class SVGProcess extends Component {

    @NonNull
    private final URI fqi;

    @NonNull
    private final String identifier;

    @NonNull
    private final String color;

    @NonNull
    private final List<SVGRelation> svgRelations;

    public SVGProcess(@NonNull final Process process, @NonNull final List<SVGRelation> svgRelations) {
        this.fqi = process.getFullyQualifiedIdentifier();
        this.identifier = process.getIdentifier();
        this.svgRelations = svgRelations;
        this.color = Color.htmlSafe(
                Objects.requireNonNullElseGet(process.getColor(),
                        () -> Color.getGroupColor(identifier))
        );
    }

    @Override
    public DomContent render() {
        List<DomContent> svgProcessParts = svgRelations.stream()
                .map(svgRelation -> svgRelation.renderAsProcessBranch(fqi, color))
                .collect(Collectors.toList());

        return g()
                .attr(SVGAttr.CLASS, String.format("process %s", VISUAL_FOCUS_UNSELECTED))
                .attr("data-identifier", fqi.toString())
                .attr("data-process", identifier)
                .with(svgProcessParts)
                ;
    }
}
