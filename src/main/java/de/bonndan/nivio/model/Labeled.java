package de.bonndan.nivio.model;

import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Anything that has labels (key-value).
 */
public interface Labeled {

    String PREFIX_VALUE_DELIMITER = ";";

    /**
     * Removes all labels having a key starting with one of the prefixes
     *
     * @param labels   to filter
     * @param prefixes filter criteria
     * @return filtered labels
     */
    static Map<String, String> withoutKeys(Map<String, String> labels, String... prefixes) {
        return labels.entrySet().stream()
                .filter(entry -> {
                    for (String prefix : prefixes) {
                        if (entry.getKey().startsWith(prefix))
                            return false;
                    }
                    return true;
                }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    default String getLabel(Label key) {
        return getLabel(key.name().toLowerCase());
    }

    @Nullable
    String getLabel(String key);

    /**
     * Returns all label with values having the given prefix.
     */
    default Map<String, String> getLabels(String prefix) {
        return Labeled.withPrefix(prefix, getLabels());
    }

    /**
     * Returns all labels with the given prefix.
     */
    default Map<String, String> getLabels(Label prefix) {
        return getLabels(prefix.name().toLowerCase());
    }

    /**
     * Returns all label with values.
     */
    Map<String, String> getLabels();

    void setLabel(String key, String value);

    /**
     * Sets the given label.
     *
     * @param key   key used in lowercase
     * @param value value
     */
    default void setLabel(Label key, String value) {
        setLabel(key.name().toLowerCase(), value);
    }

    static Map<String, String> withPrefix(String prefix, Map<String, String> all) {
        return all.entrySet().stream()
                .filter(stringStringEntry -> stringStringEntry.getValue() != null)
                .filter(stringStringEntry -> stringStringEntry.getKey().startsWith(prefix))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Reduces labels having the same prefix to one map field.
     * <p>
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
     * <p>
     * The key is just the prefix and the value a delimited string.
     *
     * @param all       raw labels with prefixed
     * @param delimiter concatenation delimiter
     * @return prefix-unique map
     */
    static Map<String, String> groupedByPrefixes(Map<String, String> all, String delimiter) {
        Map<String, String> grouped = new HashMap<>(all.size());
        all.forEach((key, value1) -> {
            if (key.contains(Label.DELIMITER)) {
                key = key.split("\\" + Label.DELIMITER)[0];
            }

            String value = grouped.getOrDefault(key, "");
            value += (value.length() > 0 ? delimiter : "") + value1;
            grouped.put(key, value);
        });

        return grouped;
    }

    /**
     * Returns all values having a key starting with the prefix.
     * <p>
     * Labels:
     * foo.bar.baz = hello
     * Returned is prefix is "foo"
     * bar.baz -> hello
     *
     * @param prefix label enum
     * @return map
     */
    default Map<String, Map<String, String>> indexedByPrefix(Label prefix) {
        return indexedByPrefix(prefix.name());
    }

    default Map<String, Map<String, String>> indexedByPrefix(String prefix) {
        Map<String, Map<String, String>> byValue = new HashMap<>();
        getLabels().forEach((s, labelValue) -> {
            if (!s.startsWith(prefix)) {
                return;
            }
            //label status.foo.status
            //label status.foo.message
            String[] parts = s.replace(prefix + Label.DELIMITER, "").split("\\" + Label.DELIMITER);
            if (parts.length != 2) {
                return;
            }
            String key = parts[0];
            String statusOrMessage = parts[1];
            if (statusOrMessage == null) {
                return;
            }
            if (labelValue == null) {
                labelValue = "";
            }
            byValue.putIfAbsent(key, new HashMap<>());
            byValue.get(key).put(statusOrMessage, labelValue);
        });
        return byValue;
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
     * Map-setter that prevents overwriting existing labels.
     *
     * @param labels map of labels
     */
    default void setLabels(@Nullable Map<String, String> labels) {
        if (labels == null) {
            return;
        }
        labels.forEach((s, s2) -> getLabels().put(s, s2));
    }

    /**
     * Convenience method to set array-like labels.
     *
     * @param prefix         label prefix enum
     * @param suffixAndValue value (label suffix is the same as the value)
     */
    default void setPrefixed(Label prefix, String suffixAndValue) {
        setPrefixed(prefix.name(), suffixAndValue);
    }

    /**
     * Convenience method to set array-like labels.
     *
     * @param prefix         prefix, may contain a dot as delimiter
     * @param suffixAndValue value (label suffix is the same as the value)
     */
    default void setPrefixed(String prefix, String suffixAndValue) {
        if (StringUtils.isEmpty(prefix)) {
            throw new IllegalArgumentException("Prefix is empty.");
        }
        if (!prefix.endsWith(Label.DELIMITER)) {
            prefix = prefix + Label.DELIMITER;
        }
        setLabel(prefix.toLowerCase() + suffixAndValue, suffixAndValue);
    }

    /**
     * @param prefix
     * @param suffixAndValue
     */
    default void setPrefixed(String prefix, String[] suffixAndValue) {
        Arrays.stream(suffixAndValue).forEach(s -> setPrefixed(prefix, s));
    }
}
