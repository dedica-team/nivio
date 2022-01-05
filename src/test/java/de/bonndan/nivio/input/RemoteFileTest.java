package de.bonndan.nivio.input;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.AnythingPattern;
import de.bonndan.nivio.input.http.HttpService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.givenThat;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class RemoteFileTest {

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
    void fetchesYaml() throws MalformedURLException {

        String path = getRootPath() + "/src/test/resources/example/services/wordpress.yml";
        String yml = FileFetcher.readFile(new File(path));

        givenThat(
                get("/some/file.yml").willReturn(aResponse().withStatus(200).withBody(yml))
        );

        SourceReference sourceReference = new SourceReference(buildApiUrl());
        FileFetcher fetcher = new FileFetcher(new HttpService());
        String s = fetcher.get(sourceReference);
        assertEquals(yml, s);
    }

    @Test
    void fetchesYamlWithHeader() throws MalformedURLException {

        String path = getRootPath() + "/src/test/resources/example/services/wordpress.yml";
        String yml = FileFetcher.readFile(new File(path));

        givenThat(
                get("/some/file.yml")
                        .withHeader("PRIVATE_KEY", new AnythingPattern())
                        .willReturn(aResponse().withStatus(200).withBody(yml))
        );

        SourceReference sourceReference = new SourceReference(buildApiUrl());
        sourceReference.setHeaderTokenName("PRIVATE_KEY");
        sourceReference.setHeaderTokenValue("xyz");
        FileFetcher fetcher = new FileFetcher(new HttpService());
        String s = fetcher.get(sourceReference);
        assertEquals(yml, s);
    }

    @Test
    void fetchesYamlWithBasicAuth() throws MalformedURLException {

        String path = getRootPath() + "/src/test/resources/example/services/wordpress.yml";
        String yml = FileFetcher.readFile(new File(path));

        givenThat(
                get("/some/file.yml")
                        .withHeader("Authorization", new AnythingPattern())
                        .willReturn(aResponse().withStatus(200).withBody(yml))
        );

        SourceReference sourceReference = new SourceReference(buildApiUrl());
        sourceReference.setBasicAuthUsername("x");
        sourceReference.setBasicAuthPassword("y");
        FileFetcher fetcher = new FileFetcher(new HttpService());
        String s = fetcher.get(sourceReference);
        assertEquals(yml, s);
    }

    @Test
    void fetchingFails() throws MalformedURLException {

        givenThat(
                get("/some/file.yml")
                        .withHeader("Authorization", new AnythingPattern())
                        .willReturn(aResponse().withStatus(404))
        );

        SourceReference sourceReference = new SourceReference(buildApiUrl());
        sourceReference.setBasicAuthUsername("x");
        sourceReference.setBasicAuthPassword("y");
        FileFetcher fetcher = new FileFetcher(new HttpService());
        assertThrows(ReadingException.class, () -> fetcher.get(sourceReference));
    }

    private URL buildApiUrl() throws MalformedURLException {
        return new URL(String.format("http://localhost:%d/some/file.yml", wireMockServer.port()));
    }

    private String getRootPath() {
        Path currentRelativePath = Paths.get("");
        return currentRelativePath.toAbsolutePath().toString();
    }
}
