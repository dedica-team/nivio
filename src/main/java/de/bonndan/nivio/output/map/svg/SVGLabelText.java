package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.output.map.ItemMapItem;
import j2html.tags.ContainerTag;

class SVGLabelText extends Component {

    private final ItemMapItem item;
    final long x, y;
    private String className;

    SVGLabelText(ItemMapItem item, long x, long y, String className) {
        this.item = item;
        this.x = x;
        this.y = y;
        this.className = className;
    }

    public ContainerTag render() {

        return SvgTagCreator.text(item.name)
                .attr("x", x)
                .attr("y", y==0 ? "0.3em" : y)
                .attr("class", className)
                .attr("text-anchor", "middle");
    }
}

