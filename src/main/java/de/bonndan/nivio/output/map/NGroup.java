package de.bonndan.nivio.output.map;

import j2html.tags.DomContent;

class NGroup extends Component {

    private final GroupMapItem group;
    private final Layout layout;
    private final int sizeFactor;

    NGroup(GroupMapItem group, Layout layout, int sizeFactor) {
        this.group = group;
        this.layout = layout;
        this.sizeFactor = sizeFactor;
    }


    public DomContent render() {
        var fill = group.color;
        var fillId = (fill) != null ? "#" + fill : null;
        var size = 40;

        var hexCoords1 = new HexCoords(group.x1, group.y1, sizeFactor);
        var hexCoords2 = new HexCoords(group.x2, group.y2, sizeFactor);
        var startPoint = HexUtils.hexToPixel(hexCoords1.toHex(), layout);
        var endPoint = HexUtils.hexToPixel(hexCoords2.toHex(), layout);
        var width = endPoint.x - startPoint.x + 4 * size;
        var height = endPoint.y - startPoint.y + 5 * size;

        return SvgTagCreator.g(
          SvgTagCreator.rect()
                .attr("x", startPoint.x - 2 * size)
                .attr("y", startPoint.y - 2 * size)
                .attr("rx", 50)
                .attr("ry", 50)
                .attr("width", width)
                .attr("height", height)
                //.attr("style", cellStyle)
                .attr("stroke", fillId)
                .attr("fill", fillId)
                .attr("class", "group")
                ,

          SvgTagCreator.text(group.name)
                .attr("x", startPoint.x - 2 * size + width / 2)
                .attr("y", startPoint.y + height - 3 * size)
                .attr("fill", fillId)
                .attr("font-size", 24)
                .attr("width", width)
                .attr("text-anchor", "middle")
        );
    }
}

