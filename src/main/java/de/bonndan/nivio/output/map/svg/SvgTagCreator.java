package de.bonndan.nivio.output.map.svg;

import j2html.tags.ContainerTag;
import j2html.tags.DomContent;

public class SvgTagCreator {

    private SvgTagCreator() {}

    public static ContainerTag text(String text) {
        return (new ContainerTag("text")).withText(text);
    }

    public static ContainerTag path() {
        return new ContainerTag("path");
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

    public static ContainerTag polygon() {
        return (new ContainerTag("polygon"));
    }

    public static ContainerTag rect() {
        return (new ContainerTag("rect"));
    }

    public static ContainerTag svg(DomContent... dc) {
        return (new ContainerTag("svg")).with(dc);
    }

    public static ContainerTag image() {
        return new ContainerTag("image");
    }

    public static ContainerTag defs() {
        return new ContainerTag("defs");
    }

    public static ContainerTag marker() {
        return new ContainerTag("marker");
    }

    public static ContainerTag use(String path) {
        return new ContainerTag("use").attr("xlink:href", path);
    }

    public static ContainerTag title(String title) {
        return new ContainerTag("title").withText(title);
    }
}
