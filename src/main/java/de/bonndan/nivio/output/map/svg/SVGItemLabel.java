package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.model.*;
import j2html.tags.ContainerTag;
import j2html.tags.DomContent;
import org.springframework.util.StringUtils;

import java.util.Map;

class SVGItemLabel extends Component {

    public static final int LABEL_WIDTH = 140;
    public static final int CORNER_RADIUS = 10;
    private final Item item;
    private int width;

    SVGItemLabel(Item item) {
        this.item = item;
        this.width = LABEL_WIDTH;
    }

    public DomContent render() {
        String name = StringUtils.isEmpty(item.getName()) ? item.getIdentifier() : item.getName();
        int size = 40;
        ContainerTag labelText = new SVGLabelText(name, "0", String.valueOf(size + 15), "").render();

        //TODO this is naive
        if (name.length() < 10) {
            this.width = 100;
        }
        if (name.length() > 19) {
            this.width = 200;
        }

        var rect = SvgTagCreator.rect()
                .attr("x", -width / 2)
                .attr("y", size + 4)
                .attr("rx", CORNER_RADIUS)
                .attr("ry", CORNER_RADIUS)
                .attr("fill", "white")
                .attr("width", width)
                .attr("height", size / 2);

        Status highest = Status.highestOf(item.getStatusValues());
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

        Map<String, String> groupedLabels = Labeled.groupedByPrefixes(item.getLabels());
        groupedLabels.forEach((key, value) -> {
            if (!StringUtils.isEmpty(value)) {
                g.attr("data-" + key, value);
            }
        });

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

