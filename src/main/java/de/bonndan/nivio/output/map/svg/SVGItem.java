package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.model.Lifecycle;
import de.bonndan.nivio.output.Color;
import de.bonndan.nivio.output.layout.LayoutedComponent;
import j2html.tags.ContainerTag;
import j2html.tags.DomContent;
import org.springframework.util.StringUtils;

import java.awt.geom.Point2D;
import java.util.Locale;

import static de.bonndan.nivio.output.map.svg.SVGRenderer.DEFAULT_ICON_SIZE;

/**
 * A landscape item to be rendered in svg.
 */
class SVGItem extends Component {

    private final String id;
    private final LayoutedComponent layoutedComponent;
    private final DomContent children;
    private final Point2D.Double pixel;

    SVGItem(DomContent children, LayoutedComponent layoutedComponent, Point2D.Double position) {
        this.children = children;
        this.pixel = position;
        this.layoutedComponent = layoutedComponent;
        this.id = layoutedComponent.getComponent().getFullyQualifiedIdentifier().jsonValue();
    }

    /**
     * Renders the fill as background if possible, otherwise tries explicit icon or shortName.
     */
    public DomContent render() {

        boolean hasText = false;
        boolean hasFill = !StringUtils.isEmpty(layoutedComponent.getFill());
        var fillId = hasFill ? "url(#" + SVGPattern.idForLink(layoutedComponent.getFill()) + ")" : "white";
        DomContent content = null;
        Item item = (Item) layoutedComponent.getComponent();
        //use the shortname as text instead
        if (!hasFill && StringUtils.isEmpty(item.getType()) && !StringUtils.isEmpty(item.getLabel(Label.shortname))) {
            content = new SVGLabelText(item.getLabel(Label.shortname), "0", "3", "itemLabel")
                    .render().attr("text-anchor", "middle");
            fillId = "white";
            hasText = true;
        }

        DomContent icon = null;
        if (!hasFill && !hasText && !StringUtils.isEmpty(layoutedComponent.getIcon())) {
            final int size = DEFAULT_ICON_SIZE * 3;
            final int trans = Math.round(size/2);
            icon = SvgTagCreator.image()
                    .attr("xlink:href", layoutedComponent.getIcon())
                    .attr("width", size)
                    .attr("height", size)
                    .attr("transform", "translate(-" + trans + ",-" + trans + ")")
            ;
        }

        ContainerTag circle = SvgTagCreator.circle()
                .attr("id", this.id)
                .attr("cx", 0)
                .attr("cy", 0)
                .attr("r", DEFAULT_ICON_SIZE * 2)
                .condAttr(!StringUtils.isEmpty(fillId), "fill", fillId)
                .attr("stroke", "#" + (layoutedComponent.getColor() != null ? layoutedComponent.getColor() : Color.GRAY))
                .attr("data-x", String.format(Locale.ENGLISH, "%.2f", pixel.x))
                .attr("data-y", String.format(Locale.ENGLISH, "%.2f", pixel.y));
        if (Lifecycle.isPlanned(item)) {
            circle.attr("stroke-dasharray", 5);
            circle.attr("opacity", 0.7);
        }
        ContainerTag inner = SvgTagCreator.g(circle, content, children)
                .attr("class", "hexagon");

        return SvgTagCreator.g(inner, icon)
                .attr("class", "hexagon-group")
                .attr("transform", "translate(" + pixel.x + "," + pixel.y + ")");
    }

    /* TODO make scale reappear, but without evaluation (this is part of assessment)
    private ContainerTag getScale() {

        if (StringUtils.isEmpty(item.getLabel(Label.SCALE))) {
            return null;
        }

        int scaleVal = 0;
        try {
            scaleVal = Integer.parseInt(item.getLabel(Label.SCALE));
        } catch (NumberFormatException ignored) {
        }

        return SvgTagCreator.g(
                        SvgTagCreator.circle()
                                .attr("cx", 0)
                                .attr("cy", 0)
                                .attr("r", 12)
                                .attr("fill", scaleVal > 0 ? "green" : "red")
                        ,
                        SvgTagCreator.text(String.valueOf(scaleVal))
                                .attr("transform", "translate(-" + 4 + "," + 5 + ")")
                ).attr("transform", "translate(" + 30 + "," + 30 + ")");
    }

     */
}
