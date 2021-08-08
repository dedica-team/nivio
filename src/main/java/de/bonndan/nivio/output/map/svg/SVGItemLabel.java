package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import de.bonndan.nivio.model.Item;
import j2html.tags.ContainerTag;
import j2html.tags.DomContent;
import org.springframework.util.StringUtils;

import static de.bonndan.nivio.output.map.svg.SVGRenderer.DEFAULT_ICON_SIZE;

/**
 *
 */
class SVGItemLabel extends Component {

    private final String name;
    private final String id;
    private final String identifier;

    SVGItemLabel(Item item) {
        name = !StringUtils.hasLength(item.getName()) ? item.getIdentifier() : item.getName();
        id = getId(item);
        identifier = item.getFullyQualifiedIdentifier().jsonValue();
    }

    public DomContent render() {

        ContainerTag labelText = new SVGLabelText(name, "0", 2 * DEFAULT_ICON_SIZE + 30 + "", "itemLabel").render();

        return SvgTagCreator.g(null, labelText)
                .attr("class", "label")
                .attr("id", id)
                .attr("text-anchor", "middle");
    }

    private String getId(Item item) {
        return "label_" + item.getFullyQualifiedIdentifier().jsonValue()
                .replace(FullyQualifiedIdentifier.SEPARATOR, "_")
                .replace(".", "_")
                .replace(":", "_")
                ;
    }
}

