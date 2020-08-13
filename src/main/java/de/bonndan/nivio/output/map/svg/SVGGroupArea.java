package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.output.map.hex.Hex;
import j2html.tags.ContainerTag;
import j2html.tags.DomContent;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Displays a group as an area containing items.
 */
class SVGGroupArea extends Component {

    final Group group;
    final Set<Hex> groupArea;
    private final List<ContainerTag> outlines;

    SVGGroupArea(Group group, Set<Hex> groupArea, List<ContainerTag> outlines) {
        this.group = group;
        this.groupArea = groupArea;
        this.outlines = outlines;
    }

    public DomContent render() {
        var fill = group.getColor();
        var fillId = fill != null ? "#" + fill : "";

        List<DomContent> territoryHexes = outlines !=null ? new ArrayList<>(outlines):new ArrayList<>();

        Iterator<Hex> iterator = groupArea.iterator();
        if (iterator.hasNext()) {
            Hex first = iterator.next();
            territoryHexes.add(
                    SvgTagCreator.text(group.getIdentifier())
                            .attr("x", first.toPixel().x)
                            .attr("y", first.toPixel().y)
                            .condAttr(!StringUtils.isEmpty(fillId), "fill", fillId)
                            .attr("font-size", 24)
                            .attr("class", "groupLabel")
            );
        }

        return SvgTagCreator.g(territoryHexes.toArray(DomContent[]::new));
    }
}

