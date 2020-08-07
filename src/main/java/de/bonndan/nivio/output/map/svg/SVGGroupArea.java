package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.output.map.hex.Hex;
import j2html.tags.DomContent;
import org.springframework.util.StringUtils;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Displays a group as an area containing items.
 */
class SVGGroupArea extends Component {

    final Group group;
    final Set<Hex> territory;

    SVGGroupArea(Group group, Set<Hex> territory) {
        this.group = group;
        this.territory = territory;
    }

    public DomContent render() {
        var fill = group.getColor();
        var fillId = fill != null ? "#" + fill : "";

        AtomicReference<Hex> lowest = new AtomicReference<>(null);

        List<DomContent> territoryHexes = territory.stream()
                .map(hex -> {
                    Point2D.Double coords = hex.toPixel();
                    if (lowest.get() == null || coords.y > lowest.get().toPixel().y)
                        lowest.set(hex);
                    return new SVGHex(hex, fillId).render();
                })
                .collect(Collectors.toList());

        if (lowest.get() != null) {
            Point2D.Double anchor = lowest.get().toPixel();
            territoryHexes.add(
                    SvgTagCreator.text(group.getIdentifier())
                            .attr("x", anchor.x)
                            .attr("y", anchor.y + Hex.HEX_SIZE + 10)
                            .condAttr(!StringUtils.isEmpty(fillId), "fill", fillId)
                            .attr("font-size", 24)
                            .attr("text-anchor", "middle")
                            .attr("class", "groupLabel")
            );
        }

        return SvgTagCreator.g(territoryHexes.toArray(DomContent[]::new));
    }
}

