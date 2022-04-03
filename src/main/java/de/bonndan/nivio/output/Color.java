package de.bonndan.nivio.output;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import javax.annotation.Nullable;

import static org.apache.commons.lang3.StringUtils.leftPad;

/**
 * Color generation utility.
 */
public class Color {

    private Color() {}

    public static final String DARKGRAY = "333333";
    public static final String GRAY = "aaaaaa";

    private static final Logger LOGGER = LoggerFactory.getLogger(Color.class);
    public static final float MIN_BRIGHTNESS = 0.3f;
    public static final float MIN_SATURATION = 0.2f;

    /**
     * Creates a hex color based on the given name.
     *
     * @param name of a group etc
     * @return a hex color, more saturated or lightened if below limits
     */
    @Nullable
    public static String nameToRGB(@Nullable final String name, @Nullable final String defaultColor) {
        if (!StringUtils.hasLength(name)) {
            return defaultColor;
        }

        String colorString = nameToRGBRaw(name);
        float[] hsb = hsb(colorString);
        if (hsb[2] < MIN_BRIGHTNESS) {
            LOGGER.debug("Color {} is too dark: HSValue {}", colorString, hsb[2]);
            return lighten(colorString);
        }

        if (hsb[1] < MIN_SATURATION) {
            LOGGER.debug("Color {} has too less saturation: S {}", colorString, hsb[1]);
            return saturate(hsb[0], hsb[1], hsb[2]);
        }

        return colorString;
    }

    static float[] hsb(String hexColor) {
        java.awt.Color decode = new java.awt.Color(
                Integer.valueOf(hexColor.substring(0, 2), 16),
                Integer.valueOf(hexColor.substring(2, 4), 16),
                Integer.valueOf(hexColor.substring(4, 6), 16));

        float[] hsb = new float[3];
        java.awt.Color.RGBtoHSB(decode.getRed(), decode.getGreen(), decode.getBlue(), hsb);
        return hsb;
    }

    /**
     * Creates a hex color from a string. DO NOT USE OUTSIDE of tests.
     *
     * @link https://stackoverflow.com/questions/2464745/compute-hex-color-code-for-an-arbitrary-string
     */
    static String nameToRGBRaw(String name) {
        return String.format("%X", name.hashCode()).concat("000000").substring(0, 6);
    }

    /**
     * Converts to a awt color and invokes brighten
     *
     * @param color hex color string
     * @return hex color string
     */
    public static String lighten(@NonNull final String color) {
        if (!StringUtils.hasLength(color)) {
            LOGGER.error("lighten used with empty color.");
            return DARKGRAY;
        }
        try {
            java.awt.Color col = java.awt.Color.decode(color.startsWith("#") ? color : "#" + color);
            java.awt.Color brighter = col.brighter();
            return toHex(brighter);
        } catch (IllegalArgumentException ex) {
            LOGGER.error(color + " --> " + ex.getMessage());
            return color;
        }
    }

    /**
     * Converts to a awt color and invokes darken
     *
     * @param color hex color string
     * @return hex color string
     */
    @NonNull
    public static String darken(@NonNull final String color) {
        if (StringUtils.isEmpty(color)) {
            LOGGER.error("Darken used with empty color.");
            return DARKGRAY;
        }

        try {
            java.awt.Color col = java.awt.Color.decode(color.startsWith("#") ? color : "#" + color);
            java.awt.Color darker = col.darker();
            return toHex(darker);
        } catch (IllegalArgumentException ex) {
            LOGGER.error(color + " --> " + ex.getMessage());
            return color;
        }
    }

    private static String toHex(java.awt.Color color) {
        return
                asHex(Integer.toHexString(color.getRed())) +
                        asHex(Integer.toHexString(color.getGreen())) +
                        asHex(Integer.toHexString(color.getBlue()))
                ;
    }

    private static String asHex(String toHexString) {
        return leftPad(toHexString, 2, "0").substring(0, 2);
    }

    /**
     * Adds 20% to saturation.
     *
     * @param h hue
     * @param s saturation
     * @param b brightness
     * @return a hex color string
     */
    private static String saturate(float h, float s, float b) {
        try {
            java.awt.Color col = new java.awt.Color(java.awt.Color.HSBtoRGB(h, s + 0.2f, b));
            return Integer.toHexString(col.getRGB()).substring(0, 6);
        } catch (IllegalArgumentException ex) {
            LOGGER.error(ex.getMessage());
            return null;
        }
    }

    public static String desaturate(@NonNull final String color) {
        float[] hsb = hsb(color.startsWith("#") ? color.substring(1) : color);
        try {
            java.awt.Color col = new java.awt.Color(java.awt.Color.HSBtoRGB(hsb[0], hsb[1] - 0.2f, hsb[2]));
            return toHex(col);
        } catch (IllegalArgumentException ex) {
            LOGGER.error(ex.getMessage());
            return null;
        }
    }

    public static String getGroupColor(String groupIdentifier) {
        return Color.nameToRGB(groupIdentifier, Color.DARKGRAY);
    }

    /**
     * Ensures that a given string is turned into a proper hex color code.
     *
     * @param input string
     * @return safe hex code
     */
    public static String safe(@Nullable final String input) {
        if (!StringUtils.hasLength(input)) {
            return "";
        }
        String color;
        if (input.startsWith("#")) {
            color = input.replace("#", "");
        } else {
            color = input;
        }

        color = color.replaceAll("[^0-9a-fA-F]", "");
        color = color.concat("000000").substring(0, 6);
        color = Integer.toHexString(java.awt.Color.decode("0x" + color).getRGB()).substring(2);
        return color;
    }

    public static String htmlSafe(@Nullable final String input) {
        if (!StringUtils.hasLength(input)) {
            return "";
        }
        if (!input.startsWith("#")) {
            return "#" +  input;
        }
        return input;
    }
}
