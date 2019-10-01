package de.bonndan.nivio.stateaggregation.provider;

import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import de.bonndan.nivio.model.Status;
import de.bonndan.nivio.model.StatusItem;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PrometheusExporterTest {

    @Test
    public void read() {
        File file = new File(getRootPath() + "/src/test/resources/example/rancher_prometheus_exporter.txt");
        PrometheusExporter prometheusExporter = new PrometheusExporter("test", file);

        Map<FullyQualifiedIdentifier, StatusItem> states = prometheusExporter.getStates();

        assertEquals(4, states.size());
        StatusItem hubot = states.get(FullyQualifiedIdentifier.build("test", "rocket-chat", "hubot"));
        assertNotNull(hubot);
        assertEquals(Status.GREEN, hubot.getStatus(),hubot.getMessage());
    }

    private String getRootPath() {
        Path currentRelativePath = Paths.get("");
        return currentRelativePath.toAbsolutePath().toString();
    }
}
