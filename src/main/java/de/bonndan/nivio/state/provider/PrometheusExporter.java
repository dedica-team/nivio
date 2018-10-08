package de.bonndan.nivio.state.provider;

import de.bonndan.nivio.landscape.FullyQualifiedIdentifier;
import de.bonndan.nivio.state.Level;
import de.bonndan.nivio.state.Provider;
import de.bonndan.nivio.state.ServiceState;
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

    public void apply(Map<FullyQualifiedIdentifier, ServiceState> state) {
        PrometheusScraper prometheusScraper = getScraper();
        final Map<FullyQualifiedIdentifier, ServiceState> tmp = new HashMap<>();
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
                    ServiceState serviceState = toServiceState(metric);
                    if (serviceState != null)
                        putIfHigher(fqi, serviceState, tmp);
                });
            });

            state.putAll(tmp);
        } catch (IOException e) {
            logger.error("Failed to scrape " + target, e);
        }
    }

    private void putIfHigher(FullyQualifiedIdentifier fqi, ServiceState serviceState, Map<FullyQualifiedIdentifier, ServiceState> tmp) {
        if (fqi == null || serviceState == null) {
            return;
        }

        ServiceState old = tmp.get(fqi);
        if (old == null) {
            tmp.put(fqi, serviceState);
        } else if (serviceState.getLevel().isHigherThan(old.getLevel())) {
            tmp.put(fqi, serviceState);
        }

    }

    private ServiceState toServiceState(Metric metric) {

        ServiceState state = null;
        if (metric instanceof Gauge) {
            state = processGauge((Gauge) metric);
        }

        if (state == null)
            state = new ServiceState(Level.UNKNOWN, "unknown metric " + metric.getName());

        return state;
    }

    private ServiceState processGauge(Gauge metric) {
        if (metric.getName().equals("rancher_service_health_status")) {
            if (metric.getLabels().getOrDefault("health_state", "").equals("healthy") && metric.getValue() > 0) {
                return new ServiceState(Level.OK, metric.getLabels().getOrDefault("health_state", ""));
            }
            if (metric.getLabels().getOrDefault("health_state", "").equals("unhealthy") && metric.getValue() > 0) {
                return new ServiceState(Level.ERROR, metric.getLabels().getOrDefault("health_state", ""));
            }

        }
        return null;
    }

    private FullyQualifiedIdentifier toFQI(Metric metric) {
        try {
            return new FullyQualifiedIdentifier(
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
