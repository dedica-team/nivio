package de.bonndan.nivio.state.provider;

import de.bonndan.nivio.landscape.FullyQualifiedIdentifier;
import de.bonndan.nivio.state.Level;
import de.bonndan.nivio.state.ServiceState;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PrometheusExporterTest {

    @Test
    public void read() {
        File file = new File(getRootPath() + "/src/test/resources/example/rancher_prometheus_exporter.txt");
        PrometheusExporter prometheusExporter = new PrometheusExporter("test", file);

        Map<FullyQualifiedIdentifier, ServiceState> map = prometheusExporter.getStates();

        assertEquals(4, map.size());
        ServiceState hubot = map.get(FullyQualifiedIdentifier.build("test", "rocket-chat", "hubot"));
        assertNotNull(hubot);
        assertEquals(Level.OK, hubot.getLevel(),hubot.getMessage());
    }

    private String getRootPath() {
        Path currentRelativePath = Paths.get("");
        return currentRelativePath.toAbsolutePath().toString();
    }
}
