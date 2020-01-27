package de.bonndan.nivio.output.map;

import j2html.tags.ContainerTag;
import j2html.tags.DomContent;
import org.springframework.util.StringUtils;

class SVGItemLabel extends Component {

    private final ItemMapItem item;
    private final int width, size, padding;

    SVGItemLabel(ItemMapItem item, int width, int size, int padding) {
        this.item = item;
        this.width = width;
        this.size = size;
        this.padding = padding;
    }


    public DomContent render() {
        var rect = SvgTagCreator.rect()
                .attr("x", size + padding)
                .attr("y", -10)
                .attr("rx", 10)
                .attr("ry", 10)
                .attr("fill", "white")
                .attr("width", width)
                .attr("height", size / 2)
                .attr("style", "stroke: " + item.status);
        NText nText = new NText(item, size + padding + (width / 2), 5, "", width);

        ContainerTag g = SvgTagCreator.g(rect, nText.render()).attr("class", "label");
        g.attr("data-identifier", item.landscapeItem.getFullyQualifiedIdentifier().toString());

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
}

