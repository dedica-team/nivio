package de.bonndan.nivio.input.dto;


import de.bonndan.nivio.landscape.LandscapeItem;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


class ServiceDescriptionFactoryTest {

    @Test
    public void readServiceAndInfra() {

        File file = new File(getRootPath() + "/src/test/resources/example/services/wordpress.yml");
        List<ServiceDescription> services = ServiceDescriptionFactory.fromYaml(file);
        ServiceDescription service = services.get(0);
        assertEquals(LandscapeItem.TYPE_APPLICATION, service.getType());
        assertEquals("Demo Blog", service.getName());
        assertEquals("to be replaced", service.getNote());
        assertEquals("blog-server", service.getIdentifier());
        assertEquals("blog", service.getShort_name());
        assertEquals("1.0", service.getVersion());
        assertEquals("public", service.getVisibility());
        assertEquals("Wordpress", service.getSoftware());
        assertEquals("5", service.getScale());
        assertEquals("https://acme.io", service.getHomepage());
        assertEquals("https://git.acme.io/blog-server", service.getRepository());
        assertEquals("s", service.getMachine());
        assertEquals("content", service.getNetwork_zone());
        assertEquals("alphateam", service.getTeam());
        assertEquals("alphateam@acme.io", service.getContact());
        assertEquals("content", service.getGroup());
        assertEquals("docker", service.getHost_type());
        assertEquals(1, service.getTags().length);
        assertTrue(Arrays.asList(service.getTags()).contains("CMS"));

        assertNotNull(service.getStatuses());
        assertEquals(4, service.getStatuses().size());
        service.getStatuses().forEach((status, color) -> {
            if (status.equals("security")) {
                Assert.assertEquals("red", color);
            }
            if (status.equals("business_capability")) {
                Assert.assertEquals("yellow", color);
            }
        });

        assertNotNull(service.getInterfaces());
        assertEquals(3, service.getInterfaces().size());
        service.getInterfaces().forEach(dataFlow -> {
            if (dataFlow.getDescription().equals("posts")) {
                Assert.assertEquals("form", dataFlow.getFormat());
            }
        });

        assertNotNull(service.getDataFlow());
        assertEquals(2, service.getDataFlow().size());
        service.getDataFlow().forEach(dataFlow -> {
            if (dataFlow.getDescription().equals("kpis")) {
                Assert.assertEquals("content-kpi-dashboard", dataFlow.getTarget());
            }
        });

        ServiceDescription infra = services.get(1);
        assertEquals(LandscapeItem.TYPE_INFRASTRUCTURE, infra.getType());
        assertEquals("wordpress-web", infra.getIdentifier());
        assertEquals("Webserver", infra.getDescription());
        assertEquals("Apache", infra.getSoftware());
        assertEquals("2.4", infra.getVersion());
        assertEquals("https", infra.getProtocol());
        assertEquals(443, (int)infra.getPort());
        assertEquals("Pentium 1 512MB RAM", infra.getMachine());
        assertEquals("ops guys", infra.getTeam());
        assertEquals("content", infra.getNetwork_zone());
        assertEquals("docker", infra.getHost_type());
    }

    @Test
    public void readIngress() {

        File file = new File(getRootPath() + "/src/test/resources/example/services/dashboard.yml");
        List<ServiceDescription> services = ServiceDescriptionFactory.fromYaml(file);
        ServiceDescription service = services.get(0);
        assertEquals(LandscapeItem.TYPE_INGRESS, service.getType());
        assertEquals("Keycloak SSO", service.getName());
        assertEquals("keycloak", service.getIdentifier());
    }

    private String getRootPath() {
        Path currentRelativePath = Paths.get("");
        return currentRelativePath.toAbsolutePath().toString();
    }
}