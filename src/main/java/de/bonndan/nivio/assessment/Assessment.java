package de.bonndan.nivio.assessment;

import de.bonndan.nivio.assessment.kpi.AbstractKPI;
import org.springframework.lang.NonNull;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Result of a landscape (or group, or item) assessment using {@link AbstractKPI}s.
 */
public class Assessment {

    private final Map<URI, List<StatusValue>> results;
    private final ZonedDateTime date;

    public Assessment(@NonNull final Map<URI, List<StatusValue>> results) {
        date = ZonedDateTime.now();
        this.results = Objects.requireNonNull(results);
    }

    /**
     * Null-object
     *
     * @return an empty instance
     */
    public static Assessment empty() {
        return new Assessment(Map.of());
    }

    public Map<URI, List<StatusValue>> getResults() {
        return results;
    }

    public ZonedDateTime getDate() {
        return date;
    }
}
