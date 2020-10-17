package de.bonndan.nivio.output.icons;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import de.bonndan.nivio.input.http.HttpService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class VendorIconsTest {

    public static final String FAKED_LOGO_PATH = "/logocontest/82.png";
    private WireMockServer wireMockServer;
    private VendorIcons vendorIcons;

    @BeforeEach
    public void setup() {
        wireMockServer = new WireMockServer(options().dynamicPort());
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());

        vendorIcons = new VendorIcons(new HttpService());
        vendorIcons.add("redis", "http://localhost:" + wireMockServer.port() + FAKED_LOGO_PATH);
    }

    @AfterEach
    void stopWireMockServer() {
        wireMockServer.stop();
    }

    @Test
    public void returnsEmpty() {
        Optional<String> s = vendorIcons.getUrl("foo");
        assertThat(s).isEmpty();
    }

    @Test
    public void usesVendorIcon() {

        wireMockServer.stubFor(get(FAKED_LOGO_PATH).willReturn(ok("somedata")));
        Optional<String> s = vendorIcons.getUrl("redis");
        assertThat(s).isNotEmpty();
        assertThat(s.get()).contains("c29tZWRhdGE=");
        assertThat(s.get()).doesNotContain("Optional");
    }

    @Test
    public void usesIconWithImageCacheIcon() {

        wireMockServer.stubFor(get(FAKED_LOGO_PATH).willReturn(ok("somedata")));

        Optional<String> s = vendorIcons.getUrl("redis");
        assertThat(s).isNotEmpty();
        assertThat(s.get()).contains("c29tZWRhdGE=");

        //2nd call
        vendorIcons.getUrl("redis");

        wireMockServer.verify(1, RequestPatternBuilder.newRequestPattern().withUrl(FAKED_LOGO_PATH));
    }
}