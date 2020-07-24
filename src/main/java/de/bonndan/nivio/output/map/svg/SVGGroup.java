package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.model.Group;
import j2html.tags.DomContent;

import static de.bonndan.nivio.output.map.svg.SVGRenderer.DEFAULT_ICON_SIZE;

class SVGGroup extends Component {

    private final Group group;
    private final int size;
    final double x;
    final double y;
    final int width;
    final int height;

    SVGGroup(Group group, double x, double y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        size = 40;
        this.group = group;
    }

    public DomContent render() {
        var fill = group.getColor();
        var fillId = fill != null ? "#" + fill : "";

        return SvgTagCreator.g(
                SvgTagCreator.rect()
                        .attr("x", x)
                        .attr("y", y)
                        .attr("rx", DEFAULT_ICON_SIZE)
                        .attr("ry", DEFAULT_ICON_SIZE)
                        .attr("width", width)
                        .attr("height", height)
                        //.attr("style", cellStyle)
                        .attr("stroke", fillId)
                        .attr("fill", fillId)
                        .attr("class", "group")
                        .attr("id", "group_" + group.getIdentifier()),

                SvgTagCreator.text(group.getIdentifier())
                        .attr("x", x + size / 2)
                        .attr("y", y + height + size / 2)
                        .attr("fill", fillId)
                        .attr("font-size", 24)
                        .attr("class", "groupLabel")
        );
    }
}

