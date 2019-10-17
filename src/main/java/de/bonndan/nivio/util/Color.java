package de.bonndan.nivio.util;

import com.lowagie.text.html.WebColors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class Color {

    public static String DARK = "111111";
    public static String DARKGRAY = "333333";
    public static String GRAY = "aaaaaa";

    private static final Logger LOGGER = LoggerFactory.getLogger(Color.class);

    /**
     * https://stackoverflow.com/questions/2464745/compute-hex-color-code-for-an-arbitrary-string
     *
     * @param name of a group etc
     * @return a hex color
     */
    public static String nameToRGB(String name) {
        return nameToRGB(name, "FFFFFF");
    }

    public static String nameToRGB(String name, String defaultColor) {
        if (StringUtils.isEmpty(name))
            return defaultColor;

        return String.format("%X", name.hashCode()).concat("000000").substring(0, 6);
    }

    public static String lighten(String color) {
        try {
            java.awt.Color col = WebColors.getRGBColor("#" + color).brighter();
            return Integer.toHexString(col.getRGB());
        } catch (IllegalArgumentException ex) {
            LOGGER.error(color + " --> "+ ex.getMessage());
            return color;
        }
    }
}
