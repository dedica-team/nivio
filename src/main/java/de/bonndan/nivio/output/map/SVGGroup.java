package de.bonndan.nivio.output.map;

import j2html.tags.DomContent;

class SVGGroup extends Component {

    private final GroupMapItem group;
    private final int size;
    final double x;
    final double y;
    final int width;
    final int height;

    SVGGroup(GroupMapItem group, double x, double y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        size = 40;
        this.group = group;
    }

    public DomContent render() {
        var fill = group.color;
        var fillId = (fill) != null ? "#" + fill : null;

        return SvgTagCreator.g(
                SvgTagCreator.rect()
                        .attr("x", x)
                        .attr("y", y)
                        .attr("rx", 50)
                        .attr("ry", 50)
                        .attr("width", width)
                        .attr("height", height)
                        //.attr("style", cellStyle)
                        .attr("stroke", fillId)
                        .attr("fill", fillId)
                        .attr("class", "group"),

                SvgTagCreator.text(group.name)
                        .attr("x", x + size / 2)
                        .attr("y", y + height + size / 2)
                        .attr("fill", fillId)
                        .attr("font-size", 24)
                        .attr("class", "groupLabel")
        );
    }
}

