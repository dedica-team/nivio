package de.bonndan.nivio.assessment;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * One specific property/kpi/key ... carrying a status.
 * <p>
 * Status (see {@link Status}) is an ordered set of statuses represented as colors
 */
public class StatusValue {

    public static final String SUMMARY_LABEL = "summary";

    private final String field;
    private final Status status;
    private final String message;

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