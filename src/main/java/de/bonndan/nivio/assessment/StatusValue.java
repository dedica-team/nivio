package de.bonndan.nivio.assessment;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * One specific property/kpi/key ... carrying a status.
 *
 * Status (see {@link Status}) is an ordered set of status represented as colors
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatusValue {

    public static final String LABEL_SUFFIX_STATUS = "status";
    public static final String LABEL_SUFFIX_MESSAGE = "message";
    public static final String SUMMARY_FIELD_VALUE = "summary";

    @NonNull
    private final String identifier;

    @NonNull
    private final String field;

    @NonNull
    private final Status status;

    @Nullable
    private final String message;

    private boolean summary;


    /**
     * New StatusValue with message.
     *
     * @param identifier assessment identifier (e.g. item fqi)
     * @param field      field / label name
     * @param status     current status
     * @param message    additional message
     */
    public StatusValue(@NonNull final String identifier,
                       @NonNull final String field,
                       @Nullable final Status status,
                       @Nullable final String message
    ) {
        if (!StringUtils.hasLength(identifier)) {
            throw new IllegalArgumentException("Assessment identifier is empty");
        }
        if (!StringUtils.hasLength(field)) {
            throw new IllegalArgumentException("Status value has no field");
        }

        this.identifier = identifier;
        this.field = field;
        this.status = status == null ? Status.UNKNOWN : status;
        this.message = message;
        this.summary = false;
    }

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
    @NonNull
    public static Set<StatusValue> fromMapping(@NonNull final String identifier, @NonNull final Map<String, Map<String, String>> valuesByKey) {

        Set<StatusValue> statusValues = new HashSet<>();
        valuesByKey.forEach((key, stringStringMap) -> {
            StatusValue value = new StatusValue(
                    identifier,
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
     * @param identifier assessment identifier (e.g. item fqi)
     * @param values     status values
     * @return summary
     */
    @NonNull
    public static StatusValue summary(@NonNull final String identifier, @NonNull final List<StatusValue> values) {
        //order from worst to best
        List<StatusValue> sortedValues;
        values.sort(new StatusValue.Comparator());
        if (!values.isEmpty()) {
            Status worstStatus = values.get(values.size() - 1).getStatus();
            sortedValues = values.stream().filter(statusValue -> statusValue.getStatus().equals(worstStatus)).collect(Collectors.toUnmodifiableList());
        } else {
            sortedValues = new ArrayList<>();
        }
        Status status = sortedValues.stream().findFirst().map(StatusValue::getStatus).orElse(Status.UNKNOWN);

        //skipping the child summaries
        String message = sortedValues.stream()
                .filter(statusValue -> !statusValue.summary)
                .map(statusValue -> String.format("%s %s: %s", statusValue.getIdentifier(), statusValue.getField(), statusValue.getMessage()))
                .collect(Collectors.joining("; "));
        StatusValue statusValue = new StatusValue(identifier, SUMMARY_FIELD_VALUE, status, message);
        statusValue.summary = true;
        return statusValue;
    }

    @NonNull
    public String getIdentifier() {
        return identifier;
    }

    @NonNull
    public String getField() {
        return field;
    }

    @NonNull
    public Status getStatus() {
        return status;
    }

    @Nullable
    public String getMessage() {
        return message;
    }

    public boolean isSummary() {
        return summary;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StatusValue) {
            StatusValue other = (StatusValue) obj;
            return identifier.equalsIgnoreCase(other.identifier) && field.equalsIgnoreCase(other.field);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(field);
    }

    @Override
    public String toString() {
        return
                "StatusValue{" +
                        "identifier='" + identifier + '\'' +
                        ", field='" + field + '\'' +
                        ", status=" + status +
                        ", message='" + message + '\'' +
                        ", summary=" + summary +
                        '}';

    }

    public static class Comparator implements java.util.Comparator<StatusValue> {
        public int compare(StatusValue s1, StatusValue s2) {
            return Objects.requireNonNull(s1.status).compareTo(Objects.requireNonNull(s2.status));
        }
    }

}