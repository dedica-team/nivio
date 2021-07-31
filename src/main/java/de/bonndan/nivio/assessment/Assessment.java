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

    private final Map<FullyQualifiedIdentifier, List<StatusValue>> results;
    private final ZonedDateTime date;

    public Assessment(Map<FullyQualifiedIdentifier, List<StatusValue>> results) {
        date = ZonedDateTime.now();
        this.results = results;
    }

    public Map<FullyQualifiedIdentifier, List<StatusValue>> getResults() {
        return results;
    }

    public ZonedDateTime getDate() {
        return date;
    }
}
