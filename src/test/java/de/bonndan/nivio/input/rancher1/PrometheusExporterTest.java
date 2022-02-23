package de.bonndan.nivio.input.rancher1;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.bonndan.nivio.input.FileFetcher;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.util.RootPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PrometheusExporterTest {


    private WireMockServer wireMockServer;

    @BeforeEach
    void configureSystemUnderTest() {
        wireMockServer = new WireMockServer(options().dynamicPort());
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
    }

    @AfterEach
    void stopWireMockServer() {
        wireMockServer.stop();
    }

    @Test
     void testSuccess() throws MalformedURLException {

        String path = RootPath.get() + "/src/test/resources/example/rancher_prometheus_exporter.txt";
        String prometheusExport = FileFetcher.readFile(new File(path));

        givenThat(
                get("/some/export").willReturn(aResponse().withStatus(200).withBody(prometheusExport))
        );

        String url = String.format("http://localhost:%d/some/export", wireMockServer.port());


        PrometheusExporter exporter = new PrometheusExporter(new URL(url));
        List<ItemDescription> descriptions = exporter.getDescriptions();
        assertNotNull(descriptions);
        assertFalse(descriptions.isEmpty());
        Optional<ItemDescription> op = descriptions.stream()
                .filter(itemDescription -> itemDescription.getIdentifier().equals("rocketchat"))
                .findFirst();
        assertNotNull(op.get());
        ItemDescription rocketchat = op.get();
        assertThat(rocketchat.getFullyQualifiedIdentifier().toString()).contains("rocket-chat/rocketchat");
        assertEquals("unhealthy", rocketchat.getLabel(Label.health));
    }
}