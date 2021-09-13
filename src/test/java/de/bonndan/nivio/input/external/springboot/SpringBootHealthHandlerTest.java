package de.bonndan.nivio.input.external.springboot;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.bonndan.nivio.assessment.kpi.HealthKPI;
import de.bonndan.nivio.input.FileFetcher;
import de.bonndan.nivio.input.dto.ComponentDescription;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.model.Link;
import de.bonndan.nivio.util.RootPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class SpringBootHealthHandlerTest {

    @Autowired
    RestTemplate restTemplate;

    private WireMockServer wireMockServer;
    private SpringBootHealthHandler handler;

    @BeforeEach
    void configureSystemUnderTest() {
        wireMockServer = new WireMockServer(options().dynamicPort());
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());

        handler = new SpringBootHealthHandler(restTemplate);
    }

    @AfterEach
    void stopWireMockServer() {
        wireMockServer.stop();
    }

    @Test
    void resolvesHealth() throws ExecutionException, InterruptedException, MalformedURLException {

        //given
        String path = RootPath.get() + "/src/test/resources/example/springhealth.json";
        String json = FileFetcher.readFile(new File(path));
        givenThat(
                get("/actuator/health").willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-type", "application/json")
                        .withBody(json)
                )
        );

        String url = String.format("http://localhost:%d/actuator/health", wireMockServer.port());
        Link link = new Link(new URL(url));

        //when
        CompletableFuture<ComponentDescription> resolve = handler.resolve(link);
        ItemDescription itemDescription = (ItemDescription) resolve.get();

        //then
        assertThat(itemDescription).isNotNull();
        assertThat(itemDescription.getLabel(Label.health)).isEqualTo(HealthKPI.HEALTHY);
        assertThat(itemDescription.getLabel(Label.health.withPrefix("broker"))).isEqualTo(HealthKPI.HEALTHY);
    }

    @Test
    void badResponse() throws MalformedURLException {

        //given
        givenThat(
                get("/actuator/health").willReturn(aResponse()
                        .withStatus(500)
                )
        );

        String url = String.format("http://localhost:%d/actuator/health", wireMockServer.port());
        Link link = new Link(new URL(url));

        //when
        CompletableFuture<ComponentDescription> resolve = handler.resolve(link);

        assertThat(resolve.isCompletedExceptionally()).isTrue();
        assertThrows(ExecutionException.class, () -> resolve.get());

    }

    @Test
    void noConnection() throws MalformedURLException {

        //given

        String url = "http://totally.unknown/actuator/health";
        Link link = new Link(new URL(url));

        //when
        CompletableFuture<ComponentDescription> resolve = handler.resolve(link);

        assertThat(resolve.isCompletedExceptionally()).isTrue();
        assertThrows(ExecutionException.class, () -> resolve.get());

    }
}