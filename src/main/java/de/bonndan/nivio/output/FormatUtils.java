package de.bonndan.nivio.output;

import org.springframework.util.StringUtils;

import java.util.Collection;

import static org.springframework.util.StringUtils.isEmpty;

public class FormatUtils {

    public static String nice(Collection<String> strings) {
        if (strings == null)
            return "-";
        return nice(strings.toArray(new String[]{}));
    }

    public static String nice(String[] tags) {
        if (tags == null || tags.length == 0)
            return "-";

        return StringUtils.arrayToCommaDelimitedString(tags);
    }

    public static String nice(String string) {
        if (isEmpty(string))
            return "-";

        string = string.replace("_", " ");
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    public static String ifPresent(String string) {
        if (isEmpty(string))
            return "";

        return string;
    }
}
