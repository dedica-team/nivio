package de.bonndan.nivio.output;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.AnythingPattern;
import de.bonndan.nivio.landscape.Service;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static de.bonndan.nivio.output.IconService.VENDOR_PREFIX;
import static org.junit.jupiter.api.Assertions.*;

class IconServiceTest {

    IconService iconService;
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
        assertTrue(iconUrlContains("service", iconService.getIcon(new Service())));
    }

    @Test
    public void returnsServiceWithUnknownType() {
        Service service = new Service();
        service.setType("asb");
        assertTrue(iconUrlContains("service", iconService.getIcon(service)));
    }

    @Test
    public void returnsType() {
        Service service = new Service();
        service.setType("firewall");
        assertTrue(iconUrlContains("firewall", iconService.getIcon(service)));
    }

    @Test
    public void returnsTypeIgnoreCase() {
        Service service = new Service();
        service.setType("FireWall");
        assertTrue(iconUrlContains("firewall", iconService.getIcon(service)));
    }

    @Test
    public void returnsIcon() {
        Service service = new Service();
        service.setIcon("http://my.icon");
        assertTrue(iconService.getIcon(service).getUrl().toString().contains("http://my.icon"));
    }

    @Test
    public void isCustom() {
        Service service = new Service();
        service.setIcon("http://my.icon");
        assertTrue(iconService.getIcon(service).isCustom());
    }

    @Test
    public void usesVendorIcon() {
        Service service = new Service();
        service.setIcon(VENDOR_PREFIX + "redis");
        assertTrue(iconService.getIcon(service).getUrl().toString().contains("http://download.redis.io/logocontest/82.png"));
    }

    @Test
    public void usesIconWithImageCacheIcon() {

        wireMockServer.stubFor(get("/")
                .willReturn(ok("OK")));
        String urlprefix = String.format("http://localhost:%d", wireMockServer.port());
        iconService = new IconService();
        iconService.setImageProxy(urlprefix);

        Service service = new Service();
        service.setIcon(VENDOR_PREFIX + "redis");
        assertEquals(urlprefix + "//" + "http://download.redis.io/logocontest/82.png", iconService.getIcon(service).getUrl().toString());
    }

    @Test
    public void usesImageCache() {

        wireMockServer.stubFor(get("/")
                .willReturn(ok("OK")));
        iconService = new IconService();
        iconService.setImageProxy(String.format("http://localhost:%d", wireMockServer.port()));

        Service service = new Service();
        service.setIcon("http://my.icon");
        String urlprefix = String.format("http://localhost:%d", wireMockServer.port());
        assertEquals(urlprefix + "//" + "http://my.icon", iconService.getIcon(service).getUrl().toString());

    }

    private boolean iconUrlContains(String part, Icon icon) {
        return icon.getUrl().toString().contains(part);
    }
}