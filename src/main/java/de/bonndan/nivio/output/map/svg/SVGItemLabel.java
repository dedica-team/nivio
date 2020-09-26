package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Labeled;
import j2html.tags.ContainerTag;
import j2html.tags.DomContent;
import org.springframework.util.StringUtils;

import java.util.Map;

import static de.bonndan.nivio.output.map.svg.SVGRenderer.DEFAULT_ICON_SIZE;

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
        ContainerTag labelText = new SVGLabelText(name, DEFAULT_ICON_SIZE * 2 + 15 + "", "0", "itemLabel").render();

        //TODO this is naive
        if (name.length() < 10) {
            this.width = 100;
        }
        if (name.length() > 19) {
            this.width = 200;
        }

        ContainerTag g = SvgTagCreator.g(null, labelText).attr("class", "label");
        g.attr("id", getId());
        g.attr("data-identifier", item.getFullyQualifiedIdentifier().jsonValue());
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
        return "label_" + item.getFullyQualifiedIdentifier().jsonValue()
                .replace(FullyQualifiedIdentifier.SEPARATOR, "_")
                .replace(".", "_")
                .replace(":", "_")
                ;
    }
}

