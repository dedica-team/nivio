package de.bonndan.nivio.output.map;

import j2html.tags.ContainerTag;

class NText extends Component {

    private final ItemMapItem item;
    final long x, y;
    private String className;
    private final int width;

    NText(ItemMapItem item, long x, long y, String className, int width) {
        this.item = item;
        this.x = x;
        this.y = y;
        this.className = className;
        this.width = width;
    }

    public ContainerTag render() {

        return SvgTagCreator.text(item.name)
                .attr("x", x)
                .attr("y", y==0 ? "0.3em" : y)
                .attr("width", width)
                .attr("class", className)
                .attr("text-anchor", "middle");
    }
}

