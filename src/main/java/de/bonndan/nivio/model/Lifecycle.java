package de.bonndan.nivio.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;


public enum Lifecycle {

    PLANNED,
    INTEGRATION,
    PRODUCTION,
    END_OF_LIFE;

    @JsonCreator
    @Nullable
    public static Lifecycle from(String lifecycle) {
        if (StringUtils.isEmpty(lifecycle))
            return null;

        lifecycle = lifecycle.toLowerCase().trim();
        if (lifecycle.contains("plan"))
            return PLANNED;

        if (lifecycle.contains("integrat") || lifecycle.contains("test"))
            return INTEGRATION;

        if (lifecycle.contains("prod"))
            return PRODUCTION;

        if (lifecycle.contains("end") || lifecycle.contains("eol"))
            return END_OF_LIFE;

        return null;
    }

    /**
     * @return true if the label "lifecycle" is "PLANNED"
     */
    public static boolean isPlanned(@NonNull final Labeled labeled) {
        return PLANNED.name().equals(labeled.getLabel(Label.lifecycle));
    }

    public static boolean isEndOfLife(@NonNull final Labeled labeled) {
        return END_OF_LIFE.name().equals(labeled.getLabel(Label.lifecycle));
    }
}