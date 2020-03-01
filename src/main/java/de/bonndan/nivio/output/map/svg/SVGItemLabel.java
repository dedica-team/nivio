package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import de.bonndan.nivio.model.LandscapeItem;
import de.bonndan.nivio.model.Status;
import j2html.tags.ContainerTag;
import j2html.tags.DomContent;
import org.springframework.util.StringUtils;

class SVGItemLabel extends Component {

    public static final int LABEL_WIDTH = 140;
    public static final int CORNER_RADIUS = 10;
    private final LandscapeItem item;
    private int width;

    SVGItemLabel(LandscapeItem item) {
        this.item = item;
        this.width = LABEL_WIDTH;
    }

    public DomContent render() {
        String name = StringUtils.isEmpty(item.getName()) ? item.getIdentifier() : item.getName();
        int size = 40;
        ContainerTag labelText = new SVGLabelText(name, "0", String.valueOf(size + 15  ), "").render();

        //TODO this is naive
        if (name.length() < 10) {
            this.width = 100;
        }
        if (name.length() > 19) {
            this.width = 200;
        }

        var rect = SvgTagCreator.rect()
                .attr("x", -width / 2)
                .attr("y", size +4)
                .attr("rx", CORNER_RADIUS)
                .attr("ry", CORNER_RADIUS)
                .attr("fill", "white")
                .attr("width", width)
                .attr("height", size / 2);

        Status highest = Status.highestOf(item.getStatuses());
        if (!Status.UNKNOWN.equals(highest)) {
            rect.attr("style", "stroke: " + highest.name());
        }


        ContainerTag g = SvgTagCreator.g(rect, labelText).attr("class", "label");
        g.attr("id", getId());

        if (!StringUtils.isEmpty(item.getName()))
            g.attr("data-name", item.getName());
        if (!StringUtils.isEmpty(item.getDescription()))
            g.attr("data-description", item.getDescription());
        if (!StringUtils.isEmpty(item.getOwner()))
            g.attr("data-owner", item.getOwner());
        if (!StringUtils.isEmpty(item.getTeam()))
            g.attr("data-team", item.getTeam());
        if (!StringUtils.isEmpty(item.getContact()))
            g.attr("data-contact", item.getContact());
        if (!StringUtils.isEmpty(item.getCapability()))
            g.attr("data-capability", item.getCapability());
        if (!StringUtils.isEmpty(item.getSoftware()))
            g.attr("data-software", item.getSoftware());
        if (!StringUtils.isEmpty(item.getVersion()))
            g.attr("data-version", item.getVersion());
        if (!StringUtils.isEmpty(item.getScale()))
            g.attr("data-scale", item.getScale());
        if (!StringUtils.isEmpty(item.getLifecycle()))
            g.attr("data-lifecycle", item.getLifecycle());
        if (!StringUtils.isEmpty(item.getCosts()))
            g.attr("data-costs", item.getCosts());

        return g;
    }

    private String getId() {
        return "label_" + item.getFullyQualifiedIdentifier().toString()
                .replace(FullyQualifiedIdentifier.SEPARATOR, "_")
                .replace(".", "_")
                .replace(":", "_")
                ;
    }
}

