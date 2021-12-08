package de.bonndan.nivio.output.map.svg;

import j2html.tags.DomContent;

abstract class Component {

    public abstract DomContent render();

    public static double round(Float f) {
        return Math.round(f * 100.0) / 100.0;
    }

    public static double round(Double d) {
        return Math.round(d * 100.0) / 100.0;
    }
}
