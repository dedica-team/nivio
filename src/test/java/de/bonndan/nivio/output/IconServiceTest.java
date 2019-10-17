package de.bonndan.nivio.output;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.bonndan.nivio.model.Item;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static de.bonndan.nivio.output.IconService.VENDOR_PREFIX;
import static org.junit.jupiter.api.Assertions.*;

class IconServiceTest {

    private IconService iconService;
    private WireMockServer wireMockServer;

    @BeforeEach
    public void setup() {
        wireMockServer = new WireMockServer(options().dynamicPort());
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
        iconService = new IconService();
    }

    @AfterEach
    void stopWireMockServer() {
        wireMockServer.stop();
    }

    @Test
    public void returnsServiceAsDefault() {
        assertTrue(iconUrlContains("service", iconService.getIcon(new Item())));
    }

    @Test
    public void returnsServiceWithUnknownType() {
        Item item = new Item();
        item.setType("asb");
        assertTrue(iconUrlContains("service", iconService.getIcon(item)));
    }

    @Test
    public void returnsType() {
        Item item = new Item();
        item.setType("firewall");
        assertTrue(iconUrlContains("firewall", iconService.getIcon(item)));
    }

    @Test
    public void returnsTypeIgnoreCase() {
        Item item = new Item();
        item.setType("FireWall");
        assertTrue(iconUrlContains("firewall", iconService.getIcon(item)));
    }

    @Test
    public void returnsIcon() {
        Item item = new Item();
        item.setIcon("http://my.icon");
        assertTrue(iconService.getIcon(item).getUrl().toString().contains("http://my.icon"));
    }

    @Test
    public void isCustom() {
        Item item = new Item();
        item.setIcon("http://my.icon");
        assertTrue(iconService.getIcon(item).isCustom());
    }

    @Test
    public void usesVendorIcon() {
        Item item = new Item();
        item.setIcon(VENDOR_PREFIX + "redis");
        assertTrue(iconService.getIcon(item).getUrl().toString().contains("http://download.redis.io/logocontest/82.png"));
    }

    @Test
    public void usesIconWithImageCacheIcon() {

        wireMockServer.stubFor(get("/")
                .willReturn(ok("OK")));
        String urlprefix = String.format("http://localhost:%d", wireMockServer.port());
        iconService = new IconService();
        iconService.setImageProxy(urlprefix);

        Item item = new Item();
        item.setIcon(VENDOR_PREFIX + "redis");
        assertEquals(urlprefix + "//" + "http://download.redis.io/logocontest/82.png", iconService.getIcon(item).getUrl().toString());
    }

    @Test
    public void usesImageCache() {

        wireMockServer.stubFor(get("/")
                .willReturn(ok("OK")));
        iconService = new IconService();
        iconService.setImageProxy(String.format("http://localhost:%d", wireMockServer.port()));

        Item item = new Item();
        item.setIcon("http://my.icon");
        String urlprefix = String.format("http://localhost:%d", wireMockServer.port());
        assertEquals(urlprefix + "//" + "http://my.icon", iconService.getIcon(item).getUrl().toString());

    }

    private boolean iconUrlContains(String part, Icon icon) {
        return icon.getUrl().toString().contains(part);
    }
}