package de.bonndan.nivio.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * One specific property/kpi/key ... carrying a status.
 * <p>
 * Status (see {@link Status}) is an ordered set of statuses represented as colors.
 * <p>
 * The classical property are present as constants, but every kpi-like measurement can be added.
 */
public interface StatusItem {

    String HEALTH = "health";
    String SECURITY = "security";
    String STABILITY = "stability";
    String CAPABILITY = "capability";

    /**
     * The label / name, unique for a service.
     */
    String getLabel();

    Status getStatus();

    String getMessage();

    /**
     * Returns a list of status items with highest status.
     */
    static List<StatusItem> highestOf(Collection<StatusItem> statuses) {

        final List<StatusItem> highest = new ArrayList<>();
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
