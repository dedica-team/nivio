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
        assertEquals("to be replaced", service.getLabel(Label.note));
        assertEquals("blog-server", service.getIdentifier());
        assertEquals("blog", service.getLabel(Label.shortname));
        assertEquals("1.0", service.getLabel(Label.version));
        assertEquals("public", service.getLabel(Label.visibility));
        assertEquals("Wordpress", service.getLabel(Label.software));
        assertEquals("5", service.getLabel(Label.scale));
        assertEquals("https://acme.io", service.getLinks().get("homepage").toString());
        assertEquals("https://git.acme.io/blog-server", service.getLinks().get("repository").toString());
        assertEquals("s", service.getLabel("machine"));
        assertNotNull(service.getLabels(Label.network));
        assertEquals("content", service.getLabels(Label.network).values().toArray()[0]);
        assertEquals("alphateam", service.getLabel(Label.team));
        assertEquals("alphateam@acme.io", service.getContact());
        assertEquals("content", service.getGroup());
        assertEquals("docker", service.getLabel("hosttype"));
        assertEquals(1, service.getTags().length);
        assertTrue(Arrays.asList(service.getTags()).contains("CMS"));
        assertTrue(Lifecycle.isEndOfLife(service));


        assertEquals(Status.RED.toString(), service.indexedByPrefix(Label.status).get(Label.security.name()).get(StatusValue.LABEL_SUFFIX_STATUS));
        assertEquals(Status.YELLOW.toString(), service.indexedByPrefix(Label.status).get(Label.capability.name().toLowerCase()).get("status"));

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
        assertEquals(LandscapeItem.LAYER_INGRESS, web.getLabel("layer"));
        assertEquals("wordpress-web", web.getIdentifier());
        assertEquals("Webserver", web.getDescription());
        assertEquals("Apache", web.getLabel(Label.software));
        assertEquals("2.4", web.getLabel(Label.version));
        assertEquals("Pentium 1 512MB RAM", web.getLabel("machine"));
        assertEquals("ops guys", web.getLabel(Label.team));
        assertEquals("content", web.getLabels(Label.network).values().toArray()[0]);
        assertEquals("docker", web.getLabel("hosttype"));
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