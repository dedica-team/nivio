package de.bonndan.nivio.output.icons;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import de.bonndan.nivio.input.http.HttpService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ExternalIconsTest {

    public static final String FAKED_LOGO_PATH = "/logocontest/82.png";
    private WireMockServer wireMockServer;
    private ExternalIcons externalIcons;

    @BeforeEach
    public void setup() {
        wireMockServer = new WireMockServer(options().dynamicPort());
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());

        externalIcons = new ExternalIcons(new HttpService());
        externalIcons.add("redis", "http://localhost:" + wireMockServer.port() + FAKED_LOGO_PATH);
    }

    @AfterEach
    void stopWireMockServer() {
        wireMockServer.stop();
    }

    @Test
    public void returnsEmpty() {
        Optional<String> s = externalIcons.getUrl("foo");
        assertThat(s).isEmpty();
    }

    @Test
    public void usesVendorIcon() {

        wireMockServer.stubFor(get(FAKED_LOGO_PATH).willReturn(ok("somedata")));
        Optional<String> s = externalIcons.getUrl("redis");
        assertThat(s).isNotEmpty();
        assertThat(s.get()).contains("c29tZWRhdGE=");
        assertThat(s.get()).doesNotContain("Optional");
    }

    @Test
    public void usesIconWithImageCacheIcon() {

        wireMockServer.stubFor(get(FAKED_LOGO_PATH).willReturn(ok("somedata")));

        Optional<String> s = externalIcons.getUrl("redis");
        assertThat(s).isNotEmpty();
        assertThat(s.get()).contains("c29tZWRhdGE=");

        //2nd call
        externalIcons.getUrl("redis");

        wireMockServer.verify(1, RequestPatternBuilder.newRequestPattern().withUrl(FAKED_LOGO_PATH));
    }

    @Test
    public void loadUrl() throws MalformedURLException {

        wireMockServer.stubFor(get(FAKED_LOGO_PATH).willReturn(ok("somedata")));

        URL url = new URL("http://localhost:" + wireMockServer.port() + FAKED_LOGO_PATH);
        Optional<String> s = externalIcons.getUrl(url);
        assertThat(s).isNotEmpty();
        assertThat(s.get()).contains("c29tZWRhdGE=");

        //2nd call
        externalIcons.getUrl(url);

        wireMockServer.verify(1, RequestPatternBuilder.newRequestPattern().withUrl(FAKED_LOGO_PATH));
    }
}