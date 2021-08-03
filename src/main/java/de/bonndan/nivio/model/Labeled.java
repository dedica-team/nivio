package de.bonndan.nivio.model;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import de.bonndan.nivio.input.AppearanceProcessor;
import de.bonndan.nivio.input.ProcessingException;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Anything that has labels (key-value).
 */
public interface Labeled {

    /**
     * Used to concatenate values when same-prefix labels are grouped.
     */
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
                })
                .filter(stringStringEntry -> stringStringEntry.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
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
    @NonNull
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

    /**
     * Any-setter default implementation for deserialization.
     *
     * @param key   label key
     * @param value label value (string|string[]|number|list|map)
     */
    default void setLabel(@NonNull final String key, final Object value) {
        if (StringUtils.isEmpty(key)) {
            throw new IllegalArgumentException("Label key is empty.");
        }

        if (value instanceof String) {
            getLabels().put(key.toLowerCase(), (String) value);
            return;
        }

        if (value instanceof Number) {
            getLabels().put(key.toLowerCase(), String.valueOf(value));
            return;
        }

        if (value instanceof String[]) {
            Arrays.stream(((String[]) value)).forEach(s -> setPrefixed(key, s));
            return;
        }

        if (value instanceof List) {
            try {
                //noinspection unchecked,rawtypes
                ((List) value).forEach(s -> setPrefixed(key, (String) s));
                return;
            } catch (ClassCastException e) {
                throw new ProcessingException(String.format("Cannot set '%s' to list '%s'. Is this a list-like structure", key, value), e);
            }
        }

        if (value instanceof Map) {
            throw new IllegalArgumentException(String.format("Cannot use the value of '%s' as map ('%s'). Please check the spelling of", key, value));
        }

        getLabels().put(key, String.valueOf(value));
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
     * Add all non-null labels from source to target where target labels are not set.
     *
     * @param source label source
     * @param target target
     */
    static void add(@NonNull final Labeled source, @NonNull final Labeled target) {
        source.getLabels().entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .filter(entry -> target.getLabel(entry.getKey()) == null)
                .forEach(entry -> target.setLabel(entry.getKey(), entry.getValue()));
    }

    /**
     * Copies all non-null value labels from source to target.
     *
     * @param source label source
     * @param target target
     */
    static void merge(@NonNull final Labeled source, @NonNull final Labeled target) {
        source.getLabels().entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .forEach(entry -> target.setLabel(entry.getKey(), entry.getValue()));
    }

    /**
     * Compares the labels against a previous version.
     *
     * @param before instance of labeled before the change
     * @return key-based diff
     */
    default List<String> diff(@NonNull final Labeled before) {
        List<String> diff = new ArrayList<>();
        MapDifference<String, String> difference = Maps.difference(Objects.requireNonNull(before).getLabels(), getLabels());
        difference.entriesOnlyOnLeft().keySet().stream()
                .filter(s -> !AppearanceProcessor.affectedLabels.contains(s))
                .forEach(s -> diff.add(String.format("Label '%s' has been removed", s)));
        difference.entriesOnlyOnRight().keySet().stream()
                .filter(s -> !AppearanceProcessor.affectedLabels.contains(s))
                .forEach(s -> diff.add(String.format("Label '%s' has been added", s)));
        difference.entriesDiffering().forEach((key, value) -> {
            if (AppearanceProcessor.affectedLabels.contains(key)) return;
            String msg = String.format("Label '%s' has changed from '%s' to '%s'", key, value.leftValue(), value.rightValue());
            diff.add(msg);
        });

        return diff;
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

}
