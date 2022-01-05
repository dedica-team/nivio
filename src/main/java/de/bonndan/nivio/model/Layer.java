package de.bonndan.nivio.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.util.Locale;

/**
 * A technical layer.
 *
 * Corresponds to detail level and is also used to create default groups.
 */
public enum Layer {
    global,
    domain,
    infrastructure;

    @JsonCreator
    @NonNull
    public static Layer of(String layer) {
        if (!StringUtils.hasLength(layer)) {
            return domain;
        }

        if (layer.equalsIgnoreCase(global.name()))
            return global;

        if (layer.toLowerCase(Locale.ROOT).startsWith("infra"))
            return infrastructure;

        return domain;
    }
}
