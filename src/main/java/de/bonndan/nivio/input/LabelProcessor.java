package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ItemDescription;

import java.util.Arrays;
import java.util.List;

public class LabelProcessor {

    private static final List<String> keyBlacklist = Arrays.asList("secret", "pass", "credentials", "token");

    /**
     * Copies label key and value to item labels, or, if prefixed "nivio.", sets the field to the value.
     *
     * @param item  landscape item
     * @param key   label name
     * @param value label value
     */
    public static void applyLabel(ItemDescription item, String key, Object value) {

        if (inBlacklist(key)) {
            return;
        }

        item.getLabels().put(key, (String) value);
    }

    private static boolean inBlacklist(String key) {
        String lk = key.toLowerCase();
        return keyBlacklist.stream().anyMatch(lk::contains);
    }
}
