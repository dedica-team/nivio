package de.bonndan.nivio.output;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.bonndan.nivio.model.Item;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URL;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static de.bonndan.nivio.output.LocalServer.VENDOR_PREFIX;
import static org.junit.jupiter.api.Assertions.*;

class LocalServerTest {

    private WireMockServer wireMockServer;
    private LocalServer localServer;


    @BeforeEach
    public void setup() {
        wireMockServer = new WireMockServer(options().dynamicPort());
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
        localServer = new LocalServer("");
    }

    @AfterEach
    void stopWireMockServer() {
        wireMockServer.stop();
    }

    @Test
    public void returnsServiceAsDefault() {
        assertTrue(iconUrlContains("service", localServer.getIconUrl(new Item())));
    }

    @Test
    public void returnsServiceWithUnknownType() {
        Item item = new Item();
        item.setType("asb");
        assertTrue(iconUrlContains("service", localServer.getIconUrl(item)));
    }

    @Test
    public void returnsType() {
        Item item = new Item();
        item.setType("firewall");
        assertTrue(iconUrlContains("firewall", localServer.getIconUrl(item)));
    }

    @Test
    public void returnsTypeIgnoreCase() {
        Item item = new Item();
        item.setType("FireWall");
        assertTrue(iconUrlContains("firewall", localServer.getIconUrl(item)));
    }

    @Test
    public void returnsIcon() {
        Item item = new Item();
        item.setIcon("http://my.icon");
        assertTrue(localServer.getIconUrl(item).toString().contains("http://my.icon"));
    }


    @Test
    public void usesVendorIcon() {
        Item item = new Item();
        item.setIcon(VENDOR_PREFIX + "redis");
        assertTrue(localServer.getIconUrl(item).toString().contains("http://download.redis.io/logocontest/82.png"));
    }

    @Test
    public void usesIconWithImageCacheIcon() {

        wireMockServer.stubFor(get("/")
                .willReturn(ok("OK")));
        String urlprefix = String.format("http://localhost:%d", wireMockServer.port());
        localServer.setImageProxy(urlprefix);

        Item item = new Item();
        item.setIcon(VENDOR_PREFIX + "redis");
        assertEquals(urlprefix + "//" + "http://download.redis.io/logocontest/82.png", localServer.getIconUrl(item).toString());
    }

    @Test
    public void usesImageCache() {

        wireMockServer.stubFor(get("/")
                .willReturn(ok("OK")));

        localServer.setImageProxy(String.format("http://localhost:%d", wireMockServer.port()));

        Item item = new Item();
        item.setIcon("http://my.icon");
        String urlprefix = String.format("http://localhost:%d", wireMockServer.port());
        assertEquals(urlprefix + "//" + "http://my.icon", localServer.getIconUrl(item).toString());

    }

    private boolean iconUrlContains(String part, URL url) {
        return url.toString().contains(part);
    }
}