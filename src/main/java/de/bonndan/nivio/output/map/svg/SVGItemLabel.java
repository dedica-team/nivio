package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import de.bonndan.nivio.output.map.ItemMapItem;
import j2html.tags.ContainerTag;
import j2html.tags.DomContent;
import org.springframework.util.StringUtils;

class SVGItemLabel extends Component {

    public static final int LABEL_WIDTH = 140;
    private final ItemMapItem item;
    private int width;
    private final int size;
    private final int padding;

    SVGItemLabel(ItemMapItem item, int size, int padding) {
        this.item = item;
        this.width = LABEL_WIDTH;
        this.size = size;
        this.padding = padding;
    }


    public DomContent render() {
        ContainerTag labelText = null;

        //TODO this is naive
        if (item.name.length() < 10) {
            this.width = 100;
        }
        if (item.name.length() > 20) {
            this.width = 200;
        }
        var yShift = size + padding;
        if (!StringUtils.isEmpty(item.name)) {
            labelText = new SVGLabelText(item, 0, yShift + padding + 2, "").render();
        }

        var rect = SvgTagCreator.rect()
                .attr("x", -width / 2)
                .attr("y", yShift - 3)
                .attr("rx", 10)
                .attr("ry", 10)
                .attr("fill", "white")
                .attr("width", width)
                .attr("height", size / 2)
                .attr("style", "stroke: " + item.status);


        ContainerTag g = SvgTagCreator.g(rect, labelText).attr("class", "label");
        g.attr("id", getId());

        if (!StringUtils.isEmpty(item.landscapeItem.getName()))
            g.attr("data-name", item.landscapeItem.getName());
        if (!StringUtils.isEmpty(item.landscapeItem.getDescription()))
            g.attr("data-description", item.landscapeItem.getDescription());
        if (!StringUtils.isEmpty(item.landscapeItem.getOwner()))
            g.attr("data-owner", item.landscapeItem.getOwner());
        if (!StringUtils.isEmpty(item.landscapeItem.getTeam()))
            g.attr("data-team", item.landscapeItem.getTeam());
        if (!StringUtils.isEmpty(item.landscapeItem.getContact()))
            g.attr("data-contact", item.landscapeItem.getContact());
        if (!StringUtils.isEmpty(item.landscapeItem.getCapability()))
            g.attr("data-capability", item.landscapeItem.getCapability());
        if (!StringUtils.isEmpty(item.landscapeItem.getSoftware()))
            g.attr("data-software", item.landscapeItem.getSoftware());
        if (!StringUtils.isEmpty(item.landscapeItem.getVersion()))
            g.attr("data-version", item.landscapeItem.getVersion());
        if (!StringUtils.isEmpty(item.landscapeItem.getScale()))
            g.attr("data-scale", item.landscapeItem.getScale());
        if (!StringUtils.isEmpty(item.landscapeItem.getLifecycle()))
            g.attr("data-lifecycle", item.landscapeItem.getLifecycle());
        if (!StringUtils.isEmpty(item.landscapeItem.getCosts()))
            g.attr("data-costs", item.landscapeItem.getCosts());

        return g;
    }

    private String getId() {
        return "label_" + item.landscapeItem.getFullyQualifiedIdentifier().toString()
                .replace(FullyQualifiedIdentifier.SEPARATOR, "_")
                .replace(".", "_")
                .replace(":", "_")
                ;
    }
}

