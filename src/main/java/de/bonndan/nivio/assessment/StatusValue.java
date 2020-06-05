package de.bonndan.nivio.assessment;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * One specific property/kpi/key ... carrying a status.
 * <p>
 * Status (see {@link Status}) is an ordered set of statuses represented as colors
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatusValue {

    public static final String SUMMARY_LABEL = "summary";

    private final String field;
    private final Status status;
    private final String message;

    /**
     * Turns a map of strings indexed by (KPI-)field into StatusValue objects.
     *
     * @param valuesByKey grouped label values
     * @return derived StatusValues
     */
    public static Set<StatusValue> fromMapping(Map<String, Map<String, String>> valuesByKey) {

        Set<StatusValue> statusValues = new HashSet<>();
        valuesByKey.forEach((key, stringStringMap) -> {
            StatusValue value = new StatusValue(
                    key,
                    Status.from(stringStringMap.get("status")),
                    stringStringMap.get("message")
            );
            statusValues.add(value);
        });
        return statusValues;
    }

    public StatusValue(@NonNull String field, @Nullable Status status, @Nullable String message) {
        if (StringUtils.isEmpty(field)) {
            throw new IllegalArgumentException("Status value has no field");
        }
        this.field = field;

        if (status == null) {
            status = Status.UNKNOWN;
        }
        this.status = status;
        this.message = message;
    }

    public StatusValue(@NonNull String field, Status status) {
        this(field, status, null);
    }

    public String getField() {
        return field;
    }

    public Status getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    /**
     * Returns a list of status items with highest status.
     */
    static List<StatusValue> highestOf(Collection<StatusValue> statuses) {

        final List<StatusValue> highest = new ArrayList<>();
        statuses.forEach(statusItem -> {
            if (statusItem == null) {
                return;
            }

            if (highest.size() == 0 || highest.get(0).getStatus().equals(statusItem.getStatus())) {
                highest.add(statusItem);
                return;
            }

            if (statusItem.getStatus().isHigherThan(highest.get(0).getStatus())) {
                highest.clear();
                highest.add(statusItem);
            }
        });

        return highest;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StatusValue) {
            return field.equals(((StatusValue) obj).field);
        }
        return false;
    }

    public static class Comparator implements java.util.Comparator<StatusValue> {
        public int compare(StatusValue s1, StatusValue s2) {
            if (s1.status.isHigherThan(s2.status)) return 1;
            if (s1.status.equals(s2.status)) return 0;
            return -1;
        }
    }

}