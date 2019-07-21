package de.bonndan.nivio.landscape;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.util.StringUtils;


public enum Lifecycle {

    PLANNED,
    INTEGRATION,
    PRODUCTION,
    END_OF_LIFE;

    @JsonCreator
    public static Lifecycle from(String lifecycle) {
        if (StringUtils.isEmpty(lifecycle))
            return PRODUCTION;

        lifecycle = lifecycle.toLowerCase().trim();
        if (lifecycle.contains("plan"))
            return PLANNED;

        if (lifecycle.contains("integrat") || lifecycle.contains("test"))
            return INTEGRATION;

        if (lifecycle.contains("prod"))
            return PRODUCTION;

        if (lifecycle.contains("end") || lifecycle.contains("eol"))
            return END_OF_LIFE;

        return PRODUCTION;
    }
}
