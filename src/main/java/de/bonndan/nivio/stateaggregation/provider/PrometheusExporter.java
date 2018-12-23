package de.bonndan.nivio.stateaggregation.provider;

import de.bonndan.nivio.input.dto.StatusDescription;
import de.bonndan.nivio.landscape.FullyQualifiedIdentifier;
import de.bonndan.nivio.landscape.Status;
import de.bonndan.nivio.landscape.StatusItem;
import de.bonndan.nivio.stateaggregation.Provider;
import org.hawkular.agent.prometheus.PrometheusDataFormat;
import org.hawkular.agent.prometheus.PrometheusScraper;
import org.hawkular.agent.prometheus.types.Gauge;
import org.hawkular.agent.prometheus.types.Metric;
import org.hawkular.agent.prometheus.types.MetricFamily;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrometheusExporter implements Provider {

    private static final Logger logger = LoggerFactory.getLogger(PrometheusExporter.class);

    private final String landscape;
    private File file;
    private URL target;

    public PrometheusExporter(String landscape, File file) {
        this.landscape = landscape;
        this.file = file;
    }

    public PrometheusExporter(String landscape, URL target) {
        this.landscape = landscape;
        this.target = target;
    }

    public Map<FullyQualifiedIdentifier, StatusItem> getStates() {
        PrometheusScraper prometheusScraper = getScraper();
        final Map<FullyQualifiedIdentifier, StatusItem> tmp = new HashMap<>();
        try {
            List<MetricFamily> scrape = prometheusScraper.scrape();
            scrape.forEach(metricFamily -> {

                if ("rancher_host_state".equals(metricFamily.getName()))
                    return;
                if ("rancher_stack_health_status".equals(metricFamily.getName()))
                    return;
                if ("rancher_stack_state".equals(metricFamily.getName()))
                    return;

                metricFamily.getMetrics().forEach(metric -> {

                    FullyQualifiedIdentifier fqi = toFQI(metric);
                    if (fqi == null)
                        return;
                    StatusItem item = toStatusItem(metric);
                    if (item != null)
                        putIfHigher(fqi, item, tmp);
                });
            });

        } catch (IOException e) {
            logger.error("Failed to scrape " + target, e);
        }

        return tmp;
    }

    private void putIfHigher(FullyQualifiedIdentifier fqi, StatusItem serviceState, Map<FullyQualifiedIdentifier, StatusItem> tmp) {
        if (fqi == null || serviceState == null) {
            return;
        }

        StatusItem old = tmp.get(fqi);
        if (old == null) {
            tmp.put(fqi, serviceState);
        } else if (serviceState.getStatus().isHigherThan(old.getStatus())) {
            tmp.put(fqi, serviceState);
        }

    }

    private StatusItem toStatusItem(Metric metric) {

        StatusItem state = null;
        if (metric instanceof Gauge) {
            state = processGauge((Gauge) metric);
        }

        return state;
    }

    private StatusItem processGauge(Gauge metric) {
        if (metric.getName().equals("rancher_service_health_status")) {
            if (metric.getLabels().getOrDefault("health_state", "").equals("healthy") && metric.getValue() > 0) {
                return new StatusDescription(StatusItem.HEALTH, Status.GREEN, metric.getLabels().getOrDefault("health_state", ""));
            }
            if (metric.getLabels().getOrDefault("health_state", "").equals("unhealthy") && metric.getValue() > 0) {
                return new StatusDescription(StatusItem.HEALTH, Status.ORANGE, metric.getLabels().getOrDefault("health_state", ""));
            }

        }
        return null;
    }

    private FullyQualifiedIdentifier toFQI(Metric metric) {
        try {
            return FullyQualifiedIdentifier.build(
                    landscape,
                    metric.getLabels().getOrDefault("stack_name", ""),
                    metric.getLabels().getOrDefault("name", "")
            );
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to generate an fqi for metric " + metric.getName(), e);
        }
        return null;
    }

    private PrometheusScraper getScraper() {

        if (file != null)
            return new PrometheusScraper(file, PrometheusDataFormat.TEXT);

        return new PrometheusScraper(target, PrometheusDataFormat.TEXT);
    }
}
