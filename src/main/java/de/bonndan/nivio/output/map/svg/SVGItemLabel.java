package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import de.bonndan.nivio.model.LandscapeItem;
import de.bonndan.nivio.model.Status;
import j2html.tags.ContainerTag;
import j2html.tags.DomContent;
import org.springframework.util.StringUtils;

class SVGItemLabel extends Component {

    public static final int LABEL_WIDTH = 140;
    private final LandscapeItem item;
    private int width;
    private final int size;
    private final int padding;

    SVGItemLabel(LandscapeItem item, int size, int padding) {
        this.item = item;
        this.width = LABEL_WIDTH;
        this.size = size;
        this.padding = padding;
    }

    public DomContent render() {
        ContainerTag labelText = null;

        //TODO this is naive
        if (item.getName().length() < 10) {
            this.width = 100;
        }
        if (item.getName().length() > 19) {
            this.width = 200;
        }
        var yShift = size + padding;
        if (!StringUtils.isEmpty(item.getName())) {
            labelText = new SVGLabelText(item.getName(), "0", String.valueOf(yShift + padding-2), "").render();
        }

        var rect = SvgTagCreator.rect()
                .attr("x", -width / 2)
                .attr("y", yShift - 3)
                .attr("rx", 10)
                .attr("ry", 10)
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

