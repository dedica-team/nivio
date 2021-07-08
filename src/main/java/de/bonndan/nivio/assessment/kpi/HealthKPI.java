package de.bonndan.nivio.assessment.kpi;

import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.input.rancher1.PrometheusExporter;
import de.bonndan.nivio.model.Label;

import java.util.Map;

/**
 * This KPI evaluates the health label as set by the {@link PrometheusExporter}
 */
public class HealthKPI extends CustomKPI {

    public static final String IDENTIFIER = "health";
    public static final String HEALTHY = "healthy";
    public static final String UNHEALTHY = "unhealthy";

    private static final Map<Status, String> matches = Map.of(
            Status.GREEN, HEALTHY,
            Status.RED, UNHEALTHY
    );

    @Override
    public String getDescription() {
        return "Evaluates the 'health' label values healthy and unhealthy.";
    }

    public HealthKPI() {
        super();
        label = Label.health.name();
        addSpecs(matches);
    }

}
