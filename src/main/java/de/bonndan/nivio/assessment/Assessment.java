package de.bonndan.nivio.assessment;

import de.bonndan.nivio.assessment.kpi.AbstractKPI;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

/**
 * Result of a landscape (or group, or item) assessment using {@link AbstractKPI}s.
 */
public class Assessment {

    private final Map<String, List<StatusValue>> results;
    private final ZonedDateTime date;

    public Assessment(Map<String, List<StatusValue>> results) {
        date = ZonedDateTime.now();
        this.results = results;
    }

    /**
     * Null-object
     *
     * @return an empty instance
     */
    public static Assessment empty() {
        return new Assessment(Map.of());
    }

    public  Map<String, List<StatusValue>> getResults() {
        return results;
    }

    public ZonedDateTime getDate() {
        return date;
    }
}
