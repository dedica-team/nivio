package de.bonndan.nivio.assessment;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.util.StringUtils;

import java.util.Objects;

public enum Status {

    UNKNOWN("grey", 0),
    GREEN("green", 1),
    YELLOW("yellow", 2),
    ORANGE("orange", 3),
    RED("red", 4),
    BROWN("brown", 5);

    private final String status;
    private final int order;

    Status(String status, int order) {
        this.status = status;
        this.order = order;
    }

    @JsonCreator
    public static Status from(String status) {
        if (StringUtils.isEmpty(status))
            return UNKNOWN;

        switch (status.toLowerCase().trim()) {
            case "green":
                return GREEN;
            case "yellow":
                return YELLOW;
            case "orange":
                return ORANGE;
            case "red":
                return RED;
            case "brown":
                return BROWN;
        }

        return UNKNOWN;
    }

    @JsonValue
    public String getName() {
        return name();
    }

    @Override
    public String toString() {
        return status;
    }

    public static class Comparator implements java.util.Comparator<Status> {
        public int compare(Status s1, Status s2) {
            if (Objects.requireNonNull(s1).order > Objects.requireNonNull(s2).order) return 1;
            if (s1.equals(s2)) return 0;
            return -1;
        }
    }
}
