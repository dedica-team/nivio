package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.output.map.hex.Hex;
import j2html.tags.DomContent;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Displays a group as an area containing items.
 *
 *
 */
class SVGGroupArea extends Component {

    private final Group group;
    private final Set<Hex> territory;

    SVGGroupArea(Group group, Set<Hex> territory) {
        this.group = group;
        this.territory = territory;
    }

    public DomContent render() {
        var fill = group.getColor();
        var fillId = fill != null ? "#" + fill : "";

        List<DomContent> territoryHexes = territory.stream()
                .map(hex -> new SVGHex(hex, fillId).render())
                .collect(Collectors.toList());

        Hex first = territory.iterator().next();
        territoryHexes.add(
                SvgTagCreator.text(group.getIdentifier())
                        .attr("x", first.toPixel().x)
                        .attr("y",  first.toPixel().y)
                        .attr("fill", fillId)
                        .attr("font-size", 24)
                        .attr("class", "groupLabel")
        );

        return SvgTagCreator.g(territoryHexes.toArray(DomContent[]::new));
    }
}

