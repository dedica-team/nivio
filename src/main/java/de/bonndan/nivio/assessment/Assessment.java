package de.bonndan.nivio.assessment;

import de.bonndan.nivio.assessment.kpi.AbstractKPI;
import de.bonndan.nivio.model.FullyQualifiedIdentifier;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

/**
 * Result of a landscape (or group, or item) assessment using {@link AbstractKPI}s.
 *
 * 
 */
public class Assessment {

    private final Map<String, List<StatusValue>> results;
    private final LocalDateTime date;

    public Assessment(Map<String, List<StatusValue>> results) {
        date = LocalDateTime.now();
        this.results = results;
    }

    public Map<String, List<StatusValue>> getResults() {
        return results;
    }

    public ZonedDateTime getDate() {
        return date;
    }
}
