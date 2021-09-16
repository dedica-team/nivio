package de.bonndan.nivio.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.util.StringUtils;

import java.util.Locale;

public enum Layer {
    global,
    domain,
    infrastructure;

    @JsonCreator
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
