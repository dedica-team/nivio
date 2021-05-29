package de.bonndan.nivio.output.map.svg;

import j2html.tags.DomContent;
import j2html.tags.UnescapedText;

public class SVGStatus {

    public static final String GLOW_FILTER_ID = "glow";
    public static final int ADDED_STROKE = 5;

    public static DomContent glowFilter() {
        return new UnescapedText(
                "<filter id=\"" + GLOW_FILTER_ID + "\">\n" +
                        "<feGaussianBlur result=\"coloredBlur\" stdDeviation=\"4\"></feGaussianBlur>\n" +
                        "</filter>"
        );
    }
}
