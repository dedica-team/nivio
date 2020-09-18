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
    public static final String LABEL_SUFFIX_STATUS = "status";
    public static final String LABEL_SUFFIX_MESSAGE = "message";

    private final String field;
    private final Status status;
    private final String message;
    private final boolean summary;
    private final String maxField;

    /**
     * Turns a map of strings indexed by (KPI-)field into StatusValue objects.
     * <p>
     * Example:
     * status.foo.status
     * status.foo.message
     * status.bar.status
     * status.bar.message
     *
     * @param valuesByKey grouped label values
     * @return derived StatusValues
     */
    public static Set<StatusValue> fromMapping(Map<String, Map<String, String>> valuesByKey) {

        Set<StatusValue> statusValues = new HashSet<>();
        valuesByKey.forEach((key, stringStringMap) -> {
            StatusValue value = new StatusValue(
                    key,
                    Status.from(stringStringMap.get(LABEL_SUFFIX_STATUS)),
                    stringStringMap.get(LABEL_SUFFIX_MESSAGE)
            );
            statusValues.add(value);
        });
        return statusValues;
    }

    /**
     * Creates a summary status value.
     *
     * @param field composed field name
     * @param max   max/highest status value
     * @return summary
     */
    public static StatusValue summary(@NonNull String field, @NonNull StatusValue max) {

        if (StringUtils.isEmpty(field)) {
            throw new IllegalArgumentException("Status value has no field");
        }
        return new StatusValue(field, max.getStatus(), max.getMessage(), max.getField());
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
        this.summary = false;
        this.maxField = null;
    }

    private StatusValue(String field, Status status, String message, String maxField) {
        if (StringUtils.isEmpty(field)) {
            throw new IllegalArgumentException("Status value has no field");
        }
        this.field = field;

        if (status == null) {
            status = Status.UNKNOWN;
        }
        this.status = status;
        this.message = message;
        this.summary = true;
        this.maxField = status != Status.UNKNOWN ? maxField : null;
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

    public boolean isSummary() {
        return summary;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StatusValue) {
            return field.equals(((StatusValue) obj).field);
        }
        return false;
    }

    public String getMaxField() {
        return maxField;
    }

    public static class Comparator implements java.util.Comparator<StatusValue> {
        public int compare(StatusValue s1, StatusValue s2) {
            if (s1.status.isHigherThan(s2.status)) return 1;
            if (s1.status.equals(s2.status)) return 0;
            return -1;
        }
    }

}