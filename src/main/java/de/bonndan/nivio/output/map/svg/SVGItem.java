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
 *
 *
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

        /*
         * use the shortname as text instead, if it is shorter than 3 chars (utf8: one "symbol"), font size is increased
         */
        String shortName = item.getLabel(Label.shortname);
        if (!hasFill && StringUtils.isEmpty(item.getType()) && !StringUtils.isEmpty(shortName)) {
            String className = shortName.length() < 3 ? "itemShortnameIcon" : "itemShortname";
            content = new SVGLabelText(shortName, "0", "3", className).render()
                    .attr("text-anchor", "middle");
            fillId = "white";
            hasText = true;
        }

        DomContent icon = null;
        if (!hasFill && !hasText && !StringUtils.isEmpty(layoutedComponent.getIcon())) {
            final int size = DEFAULT_ICON_SIZE * 3;
            final int trans = Math.round(size / 2);
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
        ContainerTag inner = SvgTagCreator.g(circle, content, children);

        return SvgTagCreator.g(inner, icon)
                .attr("data-identifier", this.id)
                .attr("class", "item")
                .attr("transform", "translate(" + pixel.x + "," + pixel.y + ")");
    }

}
