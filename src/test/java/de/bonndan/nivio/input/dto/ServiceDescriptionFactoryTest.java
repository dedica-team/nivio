package de.bonndan.nivio.input.dto;


import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


class ServiceDescriptionFactoryTest {

    @Test
    public void read() {

        File file = new File(getRootPath() + "/src/test/resources/example/wordpress.yml");
        ServiceDescription service = ServiceDescriptionFactory.fromYaml(file);
        assertEquals("Demo Blog", service.getName());
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
        assertEquals("content", service.getBounded_context());
        assertEquals("docker", service.getHost_type());
        assertEquals(1, service.getTags().length);
        assertTrue(Arrays.asList(service.getTags()).contains("CMS"));

        assertNotNull(service.getIncoming());
        assertEquals(3, service.getIncoming().size());
        service.getIncoming().forEach(dataFlow -> {
            if (dataFlow.getDescription().equals("posts")) {
                Assert.assertEquals("form", dataFlow.getFormat());
            }
        });

        assertNotNull(service.getOutgoing());
        assertEquals(1, service.getOutgoing().size());
        service.getOutgoing().forEach(dataFlow -> {
            if (dataFlow.getDescription().equals("posts")) {
                Assert.assertEquals("wordpress-mysql", dataFlow.getTarget());
            }
        });

        assertFalse(service.getInfrastructure().isEmpty());
        Infrastructure first = service.getInfrastructure().get(0);
        assertEquals("wordpress-web", first.getIdentifier());
        assertEquals("Webserver", first.getDescription());
        assertEquals("Apache", first.getSoftware());
        assertEquals("2.4", first.getVersion());
        assertEquals("https", first.getProtocol());
        assertEquals("443", first.getPort());
        assertEquals("Pentium 1 512MB RAM", first.getMachine());
        assertEquals("application", first.getType());
        assertEquals("ops guys", first.getTeam());
        assertEquals("content", first.getNetwork_zone());
        assertEquals("docker", first.getHost_type());
    }

    private String getRootPath() {
        Path currentRelativePath = Paths.get("");
        return currentRelativePath.toAbsolutePath().toString();
    }
}