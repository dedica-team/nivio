package de.bonndan.nivio.assessment;

import de.bonndan.nivio.assessment.kpi.AbstractKPI;
import de.bonndan.nivio.assessment.kpi.KPI;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

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

    /**
     * if this is linked to a single kpi only, to be done
     */
    @Nullable
    private final KPI kpi;

    public Assessment(@NonNull final Map<URI, List<StatusValue>> results,
                      @Nullable final KPI kpi
    ) {
        this.kpi = kpi;
        date = ZonedDateTime.now();
        this.results = Objects.requireNonNull(results);
    }

    public Assessment(@NonNull final Map<URI, List<StatusValue>> results) {
        this(results, null);
    }

    /**
     * Null-object
     *
     * @return an empty instance
     */
    public static Assessment empty() {
        return new Assessment(Map.of(), null);
    }

    @NonNull
    public Map<URI, List<StatusValue>> getResults() {
        return results;
    }

    @NonNull
    public ZonedDateTime getDate() {
        return date;
    }

    @Nullable
    public KPI getKpi() {
        return kpi;
    }
}
