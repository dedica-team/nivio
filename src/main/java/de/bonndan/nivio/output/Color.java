package de.bonndan.nivio.output;

import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.GroupItem;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.LandscapeImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.Optional;

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
            java.awt.Color col = java.awt.Color.decode(color.startsWith("#") ? color : "#" + color);
            return Integer.toHexString(col.brighter().getRGB());
        } catch (IllegalArgumentException ex) {
            LOGGER.error(color + " --> " + ex.getMessage());
            return color;
        }
    }

    public static String getGroupColor(Item item) {
        if (item.getGroup() == null || item.getGroup().startsWith(Group.COMMON))
            return GRAY;

        return getGroupColor(item.getGroup(), item.getLandscape());
    }

    public static String getGroupColor(String name, LandscapeImpl landscape) {

        GroupItem group = landscape.getGroups().getOrDefault(name, Group.DEFAULT_GROUP);

        return Optional.ofNullable(group.getColor())
                .orElse(Color.nameToRGB(name, Color.DARKGRAY));
    }
}
