package de.bonndan.nivio.output.map;


import j2html.tags.ContainerTag;
import j2html.tags.DomContent;

import java.awt.geom.Point2D;

class Nexagon extends Component {

    private final String id;
    private DomContent children;
    private final Point2D.Double pixel;
    private final String fill;
    private final String cellStyle;

    Nexagon(DomContent children, ItemMapItem itemMapItem, Point2D.Double pixel, String fill, String cellStyle) {
        this.children = children;
        this.fill = fill;
        this.cellStyle = cellStyle;
        this.pixel = pixel;
        this.id = itemMapItem.landscapeItem.getFullyQualifiedIdentifier().toString();
    }

    public DomContent render() {

        var fillId = (fill) != null ? "url(#" + fill + ")" : null;

        ContainerTag inner = SvgTagCreator.g(
                SvgTagCreator.circle()
                        .attr("id", this.id)
                        .attr("cx", 0)
                        .attr("cy", 0)
                        .attr("r", 40)
                        .attr("fill", fillId)
                        .attr("style", cellStyle),
                children)
                .attr("class", "hexagon");

        return SvgTagCreator.g(inner)
                .attr("class", "hexagon-group")
                .attr("transform", "translate(" + pixel.x + "," + pixel.y + ")");
    }


}
