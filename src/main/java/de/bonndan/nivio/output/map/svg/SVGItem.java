package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.assessment.Assessable;
import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.model.Lifecycle;
import de.bonndan.nivio.output.Color;
import de.bonndan.nivio.output.layout.LayoutedComponent;
import j2html.tags.ContainerTag;
import j2html.tags.DomContent;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static de.bonndan.nivio.output.map.svg.SVGRenderer.DEFAULT_ICON_SIZE;

/**
 * A landscape item to be rendered in svg.
 */
class SVGItem extends Component {

    private final String id;

    private final LayoutedComponent layoutedComponent;

    @Nullable
    private final DomContent children;

    @Nullable
    private final List<StatusValue> itemStatuses;

    private final Point2D.Double pixel;

    /**
     * @param children          optional child elements
     * @param layoutedComponent the rendered component with an {@link Item} inside
     * @param itemStatuses
     * @param position          the calculated position
     */
    SVGItem(@Nullable final DomContent children,
            @NonNull final LayoutedComponent layoutedComponent,
            @Nullable final List<StatusValue> itemStatuses,
            @NonNull final Point2D.Double position
    ) {
        this.children = children;
        this.layoutedComponent = Objects.requireNonNull(layoutedComponent);
        this.itemStatuses = itemStatuses;
        this.pixel = Objects.requireNonNull(position);
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
            final int trans = Math.round(size / 2f);
            icon = SvgTagCreator.image()
                    .attr("xlink:href", layoutedComponent.getIcon())
                    .attr("width", size)
                    .attr("height", size)
                    .attr("transform", "translate(-" + trans + ",-" + trans + ")")
            ;
        }

        String stroke = "#" + (layoutedComponent.getColor() != null ? layoutedComponent.getColor() : Color.GRAY);
        ContainerTag statusCircle = null;
        if (itemStatuses != null) {
            StatusValue worst = Assessable.getWorst(itemStatuses);
            if (worst.getStatus() != Status.UNKNOWN) {
                statusCircle = SvgTagCreator.circle()
                        .attr("cx", 70)
                        .attr("cy", 70)
                        .attr("r", DEFAULT_ICON_SIZE / 2)
                        .attr("stroke", "grey")
                        .attr("fill", worst.getStatus().getName())
                ;
            }
        }

        ContainerTag circle = SvgTagCreator.circle()
                .attr("id", this.id)
                .attr("cx", 0)
                .attr("cy", 0)
                .attr("r", DEFAULT_ICON_SIZE * 2)
                .condAttr(!StringUtils.isEmpty(fillId), "fill", fillId)
                .attr("stroke", stroke)
                .attr("data-x", String.format(Locale.ENGLISH, "%.2f", pixel.x))
                .attr("data-y", String.format(Locale.ENGLISH, "%.2f", pixel.y));
        if (Lifecycle.isPlanned(item)) {
            circle.attr("stroke-dasharray", 15);
        }
        ContainerTag inner = SvgTagCreator.g(circle, content, children);

        return SvgTagCreator.g(inner, icon, statusCircle)
                .attr("data-identifier", this.id)
                .attr("class", "item unselected")
                .attr("transform", "translate(" + pixel.x + "," + pixel.y + ")");
    }

}
