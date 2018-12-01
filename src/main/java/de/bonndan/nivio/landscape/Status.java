package de.bonndan.nivio.landscape;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.util.StringUtils;

import javax.persistence.Convert;

public enum Status {

    UNKNOWN("grey"),
    GREEN("green"),
    YELLOW("yellow"),
    ORANGE("orange"),
    RED("red");

    private final String status;

    Status(String status) {
        this.status = status;
    }

    @JsonCreator
    public static Status from(String status) {
        if (StringUtils.isEmpty(status))
            return UNKNOWN;

        switch (status.toLowerCase().trim()) {
            case "green": return GREEN;
            case "yellow": return YELLOW;
            case "orange": return ORANGE;
            case "red": return RED;
        }

        return UNKNOWN;
    }

    @Override
    @JsonValue
    public String toString() {
        return status;
    }
}
