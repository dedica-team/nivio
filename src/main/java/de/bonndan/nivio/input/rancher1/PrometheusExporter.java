package de.bonndan.nivio.input.rancher1;

import de.bonndan.nivio.input.ItemDescriptionValues;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import de.bonndan.nivio.model.Label;
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
import java.util.*;

public class PrometheusExporter {

    private static final Logger logger = LoggerFactory.getLogger(PrometheusExporter.class);
    public static final String HEALTHY = "healthy";
    public static final String UNHEALTHY = "unhealthy";

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

    public List<ItemDescription> getDescriptions() {
        PrometheusScraper prometheusScraper = getScraper();
        final Map<FullyQualifiedIdentifier, ItemDescription> tmp = new HashMap<>();
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
                            ItemDescription inMap = tmp.computeIfAbsent(itemDescription.getFullyQualifiedIdentifier(), ItemDescription::new);
                            ItemDescriptionValues.assignNotNull(inMap, itemDescription);
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
            FullyQualifiedIdentifier fqi = toFQI(metric);
            if (fqi != null) {
                itemDescription.setIdentifier(fqi.getItem());
                itemDescription.setGroup(fqi.getGroup());
            }
        }

        return itemDescription;
    }

    private ItemDescription processGauge(Gauge metric) {

        ItemDescription itemDescription = new ItemDescription();
        if (metric.getName().equals("rancher_service_health_status")) {

            String health_state = null;
            if (metric.getLabels().getOrDefault("health_state", "").equals("healthy") && metric.getValue() > 0) {
                health_state = HEALTHY;
            }

            if (metric.getLabels().getOrDefault("health_state", "").equals("unhealthy") && metric.getValue() > 0) {
                health_state = UNHEALTHY;
            }

            if (health_state != null) {
                itemDescription.setLabel(Label.health, health_state);
            }
        }

        //TODO add scale gauge

        //TODO add service state (active...)

        return itemDescription;
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
