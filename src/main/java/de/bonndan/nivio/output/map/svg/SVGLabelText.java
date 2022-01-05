package de.bonndan.nivio.output.map.svg;

import j2html.tags.ContainerTag;
import org.springframework.util.StringUtils;

class SVGLabelText extends Component {

    private final String text;
    final String x;
    final String y;
    private final String className;

    SVGLabelText(String text, String x, String y, String className) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.className = className;
    }

    public ContainerTag render() {

        return SvgTagCreator.text(text)
                .attr("x", x)
                .attr("y", !StringUtils.hasLength(y) ? "0.3em" : y)
                .attr("class", className)
                .attr("alignment-baseline", "middle")
                .attr("text-anchor", "left");
    }
}

