package de.bonndan.nivio.model;

import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Anything that has labels (key-value).
 */
public interface Labeled {

    String PREFIX_DELIMITER = ".";
    String PREFIX_VALUE_DELIMITER = ";";

    default String getLabel(Label key) {
        return getLabel(key.name().toLowerCase());
    }

    String getLabel(String key);

    /**
     * Returns all label with values having the given prefix.
     */
    default Map<String, String> getLabels(String prefix) {
        return Labeled.withPrefix(prefix, getLabels());
    }

    /**
     * Returns all labels with the given prefix.
     *
     */
    default Map<String, String> getLabels(Label prefix) {
        return getLabels(prefix.name().toLowerCase());
    }

    default void setLabels(String prefix, String[] keys, String[] values) {
        if (StringUtils.isEmpty(prefix)) {
            throw new IllegalArgumentException("prefix is empty");
        }

        if (keys.length != values.length) {
            throw new IllegalArgumentException("keys length is not values length");
        }

        for (int i = 0; i < keys.length; i++) {
            setLabel(prefix + keys[0], values[0]);
        }
    }

    /**
     * Returns all label with values.
     */
    Map<String, String> getLabels();

    void setLabel(String key, String value);

    /**
     * Sets the given label.
     *
     * @param key key used in lowercase
     * @param value value
     */
    default void setLabel(Label key, String value) {
        setLabel(key.name().toLowerCase(), value);
    }

    static Map<String, String> withPrefix(String prefix, Map<String, String> all) {
        return all.entrySet().stream()
                .filter(stringStringEntry -> stringStringEntry.getKey().startsWith(prefix))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Reduces labels having the same prefix to one map field.
     *
     * The key is just the prefix and the value a semicolon separated string.
     *
     * @param all raw labels with prefixed
     * @return prefix-unique map
     */
    static Map<String, String> groupedByPrefixes(Map<String, String> all) {
        return groupedByPrefixes(all, PREFIX_VALUE_DELIMITER);
    }

    /**
     * Reduces labels having the same prefix to one map field.
     *
     * The key is just the prefix and the value a delimited string.
     *
     * @param all raw labels with prefixed
     * @param delimiter concatenation delimiter
     * @return prefix-unique map
     */
    static Map<String, String> groupedByPrefixes(Map<String, String> all, String delimiter) {
        Map<String, String> grouped = new HashMap<>();
        all.forEach((key, value1) -> {
            if (key.contains(PREFIX_DELIMITER)) {
                key = key.split("\\" + PREFIX_DELIMITER)[0];
            }

            String value = grouped.getOrDefault(key, "");
            value += (value.length() > 0 ? delimiter : "") + value1;
            grouped.put(key, value);
        });

        return grouped;
    }

    /**
     * Copies all non-null labels from source to target.
     *
     * @param source label source
     * @param target target
     */
    static void merge(Labeled source, Labeled target) {
        source.getLabels().entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .filter(entry -> target.getLabel(entry.getKey()) == null)
                .forEach(entry -> target.setLabel(entry.getKey(), entry.getValue()));
    }

    /**
     * Convenience method.
     *
     * @param prefix prefix, may contain a dot as delimiter
     * @param suffixAndValue value (label suffix is the same as the value)
     */
    default void setPrefixed(String prefix, String suffixAndValue) {
        if (StringUtils.isEmpty(prefix)) {
            throw new IllegalArgumentException("Prefix is empty.");
        }
        if (!prefix.endsWith(PREFIX_DELIMITER)) {
            prefix = prefix + PREFIX_DELIMITER;
        }
        setLabel(prefix.toLowerCase() + suffixAndValue, suffixAndValue);
    }

    /**
     *
     * @param prefix
     * @param suffixAndValue
     */
    default void setPrefixed(String prefix, String[] suffixAndValue) {
        Arrays.stream(suffixAndValue).forEach(s -> setPrefixed(prefix, s));
    }
}
