package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.assessment.StatusValue;
import j2html.tags.DomContent;
import j2html.tags.UnescapedText;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class SVGStatus {

    public static final String GLOW_FILTER_ID = "glow";
    public static final String STATUS_PATTERN_PREFIX = "pattern_";

    public static DomContent glowFilter() {
        return new UnescapedText(
                "<filter id=\"" + GLOW_FILTER_ID + "\">\n" +
                        "<feGaussianBlur result=\"coloredBlur\" stdDeviation=\"4\"></feGaussianBlur>\n" +
                        "</filter>"
        );
    }

    public static DomContent patternFor(@NonNull final Status status) {
        final String space = "20";
        return new UnescapedText("<pattern id=\"" + STATUS_PATTERN_PREFIX + status + "\" patternUnits=\"userSpaceOnUse\" width=\"10\" height=\"10\" patternTransform=\"rotate(45)\">\n" +
                "\t\t\t<line x1=\"0\" y=\"0\" x2=\"0\" y2=\"" + space + "\" stroke=\"" + status + "\" stroke-width=\"2\" />\n" +
                "\t\t</pattern>");

    }

    /**
     * Calculates added stroke width based on status
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
