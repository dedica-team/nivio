package de.bonndan.nivio.output;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.output.icons.VendorIcons;
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
        VendorIcons vendorIcons = new VendorIcons();
        vendorIcons.add("redis", "http://download.redis.io/logocontest/82.png");
        localServer = new LocalServer("", vendorIcons);
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
        String s = localServer.getIconUrl(item).toString();
        assertEquals("http://localhost:8080/vendoricons/aHR0cDovL215Lmljb24=",s);
        assertEquals("http://my.icon", LocalServer.deproxyUrl(s));
    }


    @Test
    public void usesVendorIcon() {
        Item item = new Item();
        item.setIcon(VENDOR_PREFIX + "redis");
        String s = localServer.getIconUrl(item).toString();
        assertEquals("http://localhost:8080/vendoricons/aHR0cDovL2Rvd25sb2FkLnJlZGlzLmlvL2xvZ29jb250ZXN0LzgyLnBuZw==", s);
        assertEquals("http://download.redis.io/logocontest/82.png", LocalServer.deproxyUrl(s));
    }

    @Test
    public void usesIconWithImageCacheIcon() {

        wireMockServer.stubFor(get("/")
                .willReturn(ok("OK")));
        String urlprefix = String.format("http://localhost:%d", wireMockServer.port());
        localServer.setImageProxy(urlprefix);

        Item item = new Item();
        item.setIcon(VENDOR_PREFIX + "redis");
        assertEquals(urlprefix + "/aHR0cDovL2Rvd25sb2FkLnJlZGlzLmlvL2xvZ29jb250ZXN0LzgyLnBuZw==", localServer.getIconUrl(item).toString());
    }

    @Test
    public void usesImageCache() {

        wireMockServer.stubFor(get("/")
                .willReturn(ok("OK")));

        localServer.setImageProxy(String.format("http://localhost:%d", wireMockServer.port()));

        Item item = new Item();
        item.setIcon("http://my.icon");
        String urlprefix = String.format("http://localhost:%d", wireMockServer.port());
        assertEquals(urlprefix + "/aHR0cDovL215Lmljb24=", localServer.getIconUrl(item).toString());

    }

    private boolean iconUrlContains(String part, URL url) {
        return url.toString().contains(part);
    }
}