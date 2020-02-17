package de.bonndan.nivio.output.map.svg;


import de.bonndan.nivio.model.LandscapeItem;
import de.bonndan.nivio.model.Lifecycle;
import de.bonndan.nivio.output.map.ItemMapItem;
import j2html.tags.ContainerTag;
import j2html.tags.DomContent;
import org.springframework.util.StringUtils;

import java.awt.geom.Point2D;

class SVGItem extends Component {

    private final String id;
    private final LandscapeItem landscapeItem;
    private DomContent children;
    private final Point2D.Double pixel;
    private final String fill;
    private final String cellStyle;

    SVGItem(DomContent children, ItemMapItem itemMapItem, Point2D.Double position, String fill, String cellStyle) {
        this.children = children;
        this.fill = fill;
        this.cellStyle = cellStyle;
        this.pixel = position;
        this.landscapeItem = itemMapItem.landscapeItem;
        this.id = itemMapItem.landscapeItem.getFullyQualifiedIdentifier().toString();
    }

    public DomContent render() {

        var fillId = (fill) != null ? "url(#" + fill + ")" : null;
        ContainerTag circle = SvgTagCreator.circle()
                .attr("id", this.id)
                .attr("cx", 0)
                .attr("cy", 0)
                .attr("r", 40)
                .attr("fill", fillId)
                .attr("style", cellStyle);
        if (Lifecycle.PLANNED.equals(landscapeItem.getLifecycle())) {
            circle.attr("stroke-dasharray", 5);
            circle.attr("opacity", 0.7);
        }
        ContainerTag inner = SvgTagCreator.g(circle, children)
                .attr("class", "hexagon");

        Integer scaleVal = 0;
        try {
            scaleVal = Integer.valueOf(landscapeItem.getScale());
        } catch (NumberFormatException ignored) { }
        ContainerTag scale = StringUtils.isEmpty(landscapeItem.getScale()) ? null :
                SvgTagCreator.g(
                        SvgTagCreator.circle()
                                .attr("cx", 0)
                                .attr("cy", 0)
                                .attr("r", 12)
                                .attr("fill", scaleVal > 0 ? "green" : "red")
                        ,
                        SvgTagCreator.text(landscapeItem.getScale())
                                .attr("transform", "translate(-" + 4 + "," + 5 + ")")
                ).attr("transform", "translate(" + 30 + "," + 30 + ")");


        return SvgTagCreator.g(inner, scale)
                .attr("class", "hexagon-group")
                .attr("transform", "translate(" + pixel.x + "," + pixel.y + ")");
    }


}
