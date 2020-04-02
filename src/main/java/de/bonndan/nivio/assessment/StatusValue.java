package de.bonndan.nivio.assessment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * One specific property/kpi/key ... carrying a status.
 * <p>
 * Status (see {@link Status}) is an ordered set of statuses represented as colors
 */
public class StatusValue {

    private final String label;
    private final Status status;
    private final String message;

    public StatusValue(String label, Status status, String message) {
        this.label = label;
        this.status = status;
        this.message = message;
    }

    public StatusValue(String label, Status status) {
        this.label = label;
        this.status = status;
        message = null;
    }

    public String getLabel() {
        return label;
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
}