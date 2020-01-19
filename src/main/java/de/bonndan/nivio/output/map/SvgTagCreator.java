package de.bonndan.nivio.output.map;

import j2html.tags.ContainerTag;
import j2html.tags.DomContent;
import j2html.tags.Text;

public class SvgTagCreator {

    public static ContainerTag text(String text) {
        return (new ContainerTag("text")).withText(text);
    }

    public static ContainerTag path() {
        return (new ContainerTag("path"));
    }

    public static ContainerTag g(DomContent... dc) {
        return (new ContainerTag("g")).with(dc);
    }
    public static ContainerTag g() {
        return (new ContainerTag("g"));
    }

    public static ContainerTag circle() {
        return (new ContainerTag("circle"));
    }
    public static ContainerTag rect() {
        return (new ContainerTag("rect"));
    }

    public static ContainerTag svg(DomContent... dc) {
        return (new ContainerTag("svg")).with(dc);
    }
    public static ContainerTag foreignObject(DomContent... dc) {
        return (new ContainerTag("foreignObject")).with(dc);
    }
}
