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

    private static final Map<Status, String> matches = Map.of(
            Status.GREEN, PrometheusExporter.HEALTHY,
            Status.RED, PrometheusExporter.UNHEALTHY
    );

    public HealthKPI() {
        super(Label.health.toString().toLowerCase(), null, null, matches);
    }

}
