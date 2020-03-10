package de.bonndan.nivio.model;

import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Anything that has labels (key-value).
 */
public interface Labeled {

    default String getLabel(Label key) {
        return getLabel(key.name().toLowerCase());
    }

    String getLabel(String key);

    /**
     * Returns all label with values having the given prefix.
     */
    Map<String, String> getLabels(String prefix);

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

    default void setLabel(Label key, String value) {
        setLabel(key.name(), value);
    };

    static Map<String, String> withPrefix(String prefix, Map<String, String> all) {
        return all.entrySet().stream()
                .filter(stringStringEntry -> stringStringEntry.getKey().startsWith(prefix))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
 }
