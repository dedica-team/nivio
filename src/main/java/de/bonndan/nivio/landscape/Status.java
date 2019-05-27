package de.bonndan.nivio.landscape;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.util.StringUtils;

import javax.persistence.Convert;

public enum Status {

    UNKNOWN("grey", 0, ""),
    GREEN("green", 1, "checked"),
    YELLOW("yellow", 2, "warning"),
    ORANGE("orange", 3, "warning"),
    RED("red", 4, "flame"),
    BROWN("brown", 5, "danger");

    private final String status;
    private final int order;
    private final String symbol;

    Status(String status, int order, String symbol) {
        this.status = status;
        this.order = order;
        this.symbol = symbol;
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

    public boolean isHigherThan(Status current) {
        return order > current.order;
    }

    public String getSymbol() {
        return symbol;
    }
}
