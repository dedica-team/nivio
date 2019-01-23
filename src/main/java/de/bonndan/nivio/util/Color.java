package de.bonndan.nivio.util;

import org.springframework.util.StringUtils;

public class Color {

    public static String DARK = "111111";

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

        return String.format("%X", name.hashCode()).concat("000000").substring(0,6);
    }
}
