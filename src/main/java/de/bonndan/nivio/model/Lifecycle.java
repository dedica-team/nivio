package de.bonndan.nivio.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;


public enum Lifecycle {

    PLANNED("planned"),
    INTEGRATION("integration"),
    PRODUCTION("production"),
    END_OF_LIFE("end of life");

    public String lifecycleString;

    Lifecycle(@NonNull String lifecycleString) {
        this.lifecycleString = requireNonNullAndNonEmpty(lifecycleString);
    }

    @Override
    public String toString() {
        return lifecycleString;
    }

    public static String requireNonNullAndNonEmpty(String string) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(string)) {
            throw new IllegalArgumentException("The string argument of the lifecycle constructor is null or empty.");
        }
        return string;
    }

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
    public static boolean isPlanned(Labeled labeled) {
        return PLANNED.name().equals(labeled.getLabel(Label.lifecycle));
    }

    public static boolean isEndOfLife(Labeled labeled) {
        return END_OF_LIFE.name().equals(labeled.getLabel(Label.lifecycle));
    }
}