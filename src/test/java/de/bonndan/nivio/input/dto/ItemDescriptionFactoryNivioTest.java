package de.bonndan.nivio.input.dto;


import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.input.FileFetcher;
import de.bonndan.nivio.input.http.HttpService;
import de.bonndan.nivio.input.nivio.ItemDescriptionFactoryNivio;
import de.bonndan.nivio.model.*;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;


class ItemDescriptionFactoryNivioTest {

    private FileFetcher fileFetcher;

    private ItemDescriptionFactoryNivio descriptionFactory;

    @BeforeEach
    public void setup() {
        fileFetcher = new FileFetcher(new HttpService());
        descriptionFactory = new ItemDescriptionFactoryNivio(fileFetcher);
    }

    @Test
    public void readServiceAndInfra() {

        SourceReference file = new SourceReference(getRootPath() + "/src/test/resources/example/services/wordpress.yml");

        List<ItemDescription> services = descriptionFactory.getDescriptions(file, null);
        ItemDescription service = services.get(0);
        assertEquals("Demo Blog", service.getName());
        assertEquals("to be replaced", service.getLabel(Label.NOTE));
        assertEquals("blog-server", service.getIdentifier());
        assertEquals("blog", service.getLabel(Label.SHORTNAME));
        assertEquals("1.0", service.getLabel(Label.VERSION));
        assertEquals("public", service.getLabel(Label.VISIBILITY));
        assertEquals("Wordpress", service.getLabel(Label.SOFTWARE));
        assertEquals("5", service.getLabel(Label.SCALE));
        assertEquals("https://acme.io", service.getLinks().get("homepage").toString());
        assertEquals("https://git.acme.io/blog-server", service.getLinks().get("repository").toString());
        assertEquals("s", service.getLabel(Label.MACHINE));
        assertNotNull(service.getLabels(Label.PREFIX_NETWORK));
        assertEquals("content", service.getLabels(Label.PREFIX_NETWORK).values().toArray()[0]);
        assertEquals("alphateam", service.getLabel(Label.TEAM));
        assertEquals("alphateam@acme.io", service.getContact());
        assertEquals("content", service.getGroup());
        assertEquals("docker", service.getLabel(Label.HOSTTYPE));
        assertEquals(1, service.getTags().length);
        assertTrue(Arrays.asList(service.getTags()).contains("CMS"));
        assertEquals(Lifecycle.END_OF_LIFE, service.getLifecycle());

        assertNotNull(service.getStatuses());
        assertEquals(3, service.getStatuses().size());
        service.getStatuses().forEach(statusItem -> {
            Assert.assertNotNull(statusItem);
            Assert.assertNotNull(statusItem.getLabel());
            if (statusItem.getLabel().equals(StatusValue.SECURITY)) {
                Assert.assertEquals(Status.RED, statusItem.getStatus());
            }
            if (statusItem.getLabel().equals(StatusValue.CAPABILITY)) {
                Assert.assertEquals(Status.YELLOW, statusItem.getStatus());
            }
        });

        assertNotNull(service.getInterfaces());
        assertEquals(3, service.getInterfaces().size());
        service.getInterfaces().forEach(dataFlow -> {
            if (dataFlow.getDescription().equals("posts")) {
                Assert.assertEquals("form", dataFlow.getFormat());
            }
        });

        assertNotNull(RelationType.PROVIDER.filter(service.getRelations()));
        assertEquals(3, service.getProvidedBy().size());

        List<RelationItem> dataflows = RelationType.DATAFLOW.filter(service.getRelations());
        assertNotNull(dataflows);
        assertEquals(3, dataflows.size());
        dataflows.forEach(dataFlow -> {
             if (dataFlow.getDescription().equals("kpis")) {
                Assert.assertEquals("content-kpi-dashboard", dataFlow.getTarget());
            }
        });

        ItemDescription web = services.get(2);
        assertEquals(LandscapeItem.LAYER_INGRESS, web.getLabel(Label.LAYER));
        assertEquals("wordpress-web", web.getIdentifier());
        assertEquals("Webserver", web.getDescription());
        assertEquals("Apache", web.getLabel(Label.SOFTWARE));
        assertEquals("2.4", web.getLabel(Label.VERSION));
        assertEquals("Pentium 1 512MB RAM", web.getLabel(Label.MACHINE));
        assertEquals("ops guys", web.getLabel(Label.TEAM));
        assertEquals("content", web.getLabels(Label.PREFIX_NETWORK).values().toArray()[0]);
        assertEquals("docker", web.getLabel(Label.HOSTTYPE));
    }

    @Test
    public void readIngress() {

        SourceReference file = new SourceReference(getRootPath() + "/src/test/resources/example/services/dashboard.yml");

        List<ItemDescription> services = descriptionFactory.getDescriptions(file, null);
        ItemDescription service = services.get(0);
        assertEquals(LandscapeItem.LAYER_INGRESS, service.getGroup());
        assertEquals("Keycloak SSO", service.getName());
        assertEquals("keycloak", service.getIdentifier());
    }

    private String getRootPath() {
        Path currentRelativePath = Paths.get("");
        return currentRelativePath.toAbsolutePath().toString();
    }
}