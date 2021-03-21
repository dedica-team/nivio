package de.bonndan.nivio.input.external.openapi;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.bonndan.nivio.input.FileFetcher;
import de.bonndan.nivio.input.dto.ComponentDescription;
import de.bonndan.nivio.input.dto.InterfaceDescription;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.http.HttpService;
import de.bonndan.nivio.model.Link;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static de.bonndan.nivio.input.external.openapi.OpenAPILinkHandler.NAMESPACE;
import static org.assertj.core.api.Assertions.assertThat;

class OpenAPILinkHandlerTest {

    private WireMockServer wireMockServer;
    private OpenAPILinkHandler openAPILinkHandler;

    @BeforeEach
    void configureSystemUnderTest() {
        wireMockServer = new WireMockServer(options().dynamicPort());
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());

        openAPILinkHandler = new OpenAPILinkHandler(new HttpService());
    }

    @AfterEach
    void stopWireMockServer() {
        wireMockServer.stop();
    }

    @Test
    void fetchesJson() throws MalformedURLException, ExecutionException, InterruptedException {

        String path = getRootPath() + "/src/test/resources/petstore.json";
        String json = FileFetcher.readFile(new File(path));

        givenThat(
                get("/v3/api-docs/").willReturn(aResponse().withStatus(200).withBody(json))
        );

        String address = String.format("http://localhost:%d/v3/api-docs/", wireMockServer.port());
        Link link = new Link(new URL(address));

        //when
        CompletableFuture<ComponentDescription> resolve = openAPILinkHandler.resolve(link);
        ItemDescription itemDescription = (ItemDescription) resolve.get();
        assertThat(itemDescription).isNotNull();

        assertThat(itemDescription.getDescription()).contains("This is a sample Pet Store Server");
        assertThat(itemDescription.getContact()).isEqualTo("apiteam@swagger.io");

        assertThat(itemDescription.getLabel(NAMESPACE + "_version")).isEqualTo("1.0.5");
        assertThat(itemDescription.getLabel(NAMESPACE + "_license")).isEqualTo("Apache 2.0");
        assertThat(itemDescription.getLabel(NAMESPACE + "_terms")).isEqualTo("http://swagger.io/terms/");

        //Link link1 = itemDescription.getLinks().get(NAMESPACE + "_externaldocs");
        //assertThat(link1).isNotNull();
        //assertThat(link1.getHref().toString()).isEqualTo("asd");

        Set<InterfaceDescription> interfaces = itemDescription.getInterfaces();
        assertThat(interfaces.size()).isEqualTo(19);
        Optional<InterfaceDescription> next = interfaces.stream()
                .filter(interfaceDescription -> interfaceDescription.getName().equals("GET /pet/findByStatus"))
                .findFirst();
        assertThat(next).isNotEmpty();
        assertThat(next.get().getPath()).isEqualTo("/pet/findByStatus");
        assertThat(next.get().getSummary()).isEqualTo("Finds Pets by status");
        assertThat(next.get().getDescription()).isEqualTo("Multiple status values can be provided with comma separated strings");
        assertThat(next.get().getPayload()).isNull();
        assertThat(next.get().getParameters()).isEqualTo("status");
        assertThat(next.get().getProtection()).isEqualTo("petstore_auth");
    }

    @Test
    void notFound() throws MalformedURLException, ExecutionException, InterruptedException {

        givenThat(
                get("/v3/api-docs/").willReturn(aResponse().withStatus(404).withBody(""))
        );

        String address = String.format("http://localhost:%d/v3/api-docs/", wireMockServer.port());
        Link link = new Link(new URL(address));

        //when
        CompletableFuture<ComponentDescription> resolve = openAPILinkHandler.resolve(link);
        assertThat(resolve).hasFailed();
    }

    private String getRootPath() {
        Path currentRelativePath = Paths.get("");
        return currentRelativePath.toAbsolutePath().toString();
    }
}