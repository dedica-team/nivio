package de.bonndan.nivio.input.rancher1;

import de.bonndan.nivio.assessment.kpi.HealthKPI;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.search.ComponentMatcher;
import org.hawkular.agent.prometheus.PrometheusDataFormat;
import org.hawkular.agent.prometheus.PrometheusScraper;
import org.hawkular.agent.prometheus.types.Gauge;
import org.hawkular.agent.prometheus.types.Metric;
import org.hawkular.agent.prometheus.types.MetricFamily;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.*;

public class PrometheusExporter {

    private static final Logger logger = LoggerFactory.getLogger(PrometheusExporter.class);

    private File file;
    private URL target;

    public PrometheusExporter(File file) {
        this.file = file;
    }

    public PrometheusExporter(URL target) {
        this.target = target;
    }

    public List<ItemDescription> getDescriptions() {
        PrometheusScraper prometheusScraper = getScraper();
        final Map<URI, ItemDescription> tmp = new HashMap<>();
        try {
            List<MetricFamily> scrape = prometheusScraper.scrape();
            scrape.forEach(metricFamily -> {

                if ("rancher_host_state".equals(metricFamily.getName()))
                    return;
                if ("rancher_stack_health_status".equals(metricFamily.getName()))
                    return;
                if ("rancher_stack_state".equals(metricFamily.getName()))
                    return;

                metricFamily.getMetrics().stream()
                        .map(this::toItem)
                        .filter(Objects::nonNull)
                        .forEach(itemDescription -> {
                            ItemDescription inMap = tmp.computeIfAbsent(
                                    itemDescription.getFullyQualifiedIdentifier(),
                                    uri -> new ItemDescription(ComponentMatcher.getPartPath(4,uri).orElse(""))
                            );
                            inMap.assignNotNull(itemDescription);
                        });
            });

        } catch (IOException e) {
            logger.error("Failed to scrape " + target, e);
        }

        return new ArrayList<>(tmp.values());
    }

    private ItemDescription toItem(Metric metric) {

        ItemDescription itemDescription = null;
        if (metric instanceof Gauge) {

            itemDescription = processGauge((Gauge) metric);
            URI fqi = toFQI(metric);
            if (fqi != null) {
                itemDescription.setIdentifier(ComponentMatcher.getPartPath(4,fqi).orElseThrow());
                itemDescription.setGroup(ComponentMatcher.getPartPath(3, fqi).orElse(""));
            }
        }

        return itemDescription;
    }

    private ItemDescription processGauge(Gauge metric) {

        ItemDescription itemDescription = new ItemDescription();
        if (metric.getName().equals("rancher_service_health_status")) {

            String health_state = null;
            if (metric.getLabels().getOrDefault("health_state", "").equals("healthy") && metric.getValue() > 0) {
                health_state = HealthKPI.HEALTHY;
            }

            if (metric.getLabels().getOrDefault("health_state", "").equals("unhealthy") && metric.getValue() > 0) {
                health_state = HealthKPI.UNHEALTHY;
            }

            if (health_state != null) {
                itemDescription.setLabel(Label.health, health_state);
            }
        }

        //TODO add scale gauge

        //TODO add service state (active...)

        return itemDescription;
    }

    private URI toFQI(Metric metric) {
        try {
            return FullyQualifiedIdentifier.forDescription(
                    LandscapeDescription.class,
                    null,
                    null,
                    null,
                    metric.getLabels().getOrDefault("stack_name", ""),
                    metric.getLabels().getOrDefault("name", ""),
                    null
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
