package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.assessment.StatusValue;
import j2html.tags.DomContent;
import j2html.tags.UnescapedText;
import org.springframework.lang.Nullable;

public class SVGStatus {

    public static final String GLOW_FILTER_ID = "glow";

    public static DomContent glowFilter() {
        return new UnescapedText(
                "<filter id=\"" + GLOW_FILTER_ID + "\">\n" +
                        "<feGaussianBlur result=\"coloredBlur\" stdDeviation=\"4\"></feGaussianBlur>\n" +
                        "</filter>"
        );
    }

    /**
     * Calculates added stroke width based on status
     *
     */
    public static int getAddedStroke(@Nullable final StatusValue statusValue) {
        if (statusValue == null)
            return 1;

        switch (statusValue.getStatus()) {
            case UNKNOWN:
                return 0;
            case YELLOW:
            case ORANGE:
                return 2;
            case RED:
            case BROWN:
                return 5;

            case GREEN:
            default:
                return 1;
        }
    }
}
