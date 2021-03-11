package de.bonndan.nivio.input.dto;


import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.input.FileFetcher;
import de.bonndan.nivio.input.http.HttpService;
import de.bonndan.nivio.input.nivio.InputFormatHandlerNivio;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.model.Lifecycle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


class InputFormatHandlerNivioTest {

    private InputFormatHandlerNivio descriptionFactory;
    private LandscapeDescription landscapeDescription;

    @BeforeEach
    public void setup() {
        FileFetcher fileFetcher = new FileFetcher(new HttpService());
        descriptionFactory = new InputFormatHandlerNivio(fileFetcher);
        landscapeDescription = new LandscapeDescription("test");
    }

    @Test
    public void readServiceAndInfra() {

        SourceReference file = new SourceReference(getRootPath() + "/src/test/resources/example/services/wordpress.yml");

        descriptionFactory.applyData(file, null, landscapeDescription);
        ItemDescription service = landscapeDescription.getItemDescriptions().pick("blog-server", null);
        assertEquals("Demo Blog", service.getName());
        assertEquals("to be replaced", service.getLabel(Label.note));
        assertEquals("blog-server", service.getIdentifier());
        assertEquals("blog", service.getLabel(Label.shortname));
        assertEquals("1.0", service.getLabel(Label.version));
        assertEquals("public", service.getLabel(Label.visibility));
        assertEquals("Wordpress", service.getLabel(Label.software));
        assertEquals("5", service.getLabel(Label.scale));
        assertEquals("https://acme.io", service.getLinks().get("homepage").getHref().toString());
        assertEquals("https://git.acme.io/blog-server", service.getLinks().get("repository").getHref().toString());
        assertEquals("s", service.getLabel("machine"));
        assertNotNull(service.getLabels(Label.network));
        assertEquals("content", service.getLabels(Label.network).values().toArray()[0]);
        assertEquals("alphateam", service.getLabel(Label.team));
        assertEquals("alphateam@acme.io", service.getContact());
        assertEquals("content", service.getGroup());
        assertEquals("docker", service.getLabel("hosttype"));
        assertEquals(2, service.getTags().length);
        assertTrue(Arrays.asList(service.getTags()).contains("cms"));
        assertTrue(Arrays.asList(service.getTags()).contains("ui"));
        assertTrue(Lifecycle.isEndOfLife(service));


        assertEquals(Status.RED.toString(), service.indexedByPrefix(Label.status).get(Label.security.name()).get(StatusValue.LABEL_SUFFIX_STATUS));
        assertEquals(Status.YELLOW.toString(), service.indexedByPrefix(Label.status).get(Label.capability.name().toLowerCase()).get("status"));

        assertNotNull(service.getInterfaces());
        assertEquals(3, service.getInterfaces().size());
        service.getInterfaces().forEach(dataFlow -> {
            if (dataFlow.getDescription().equals("posts")) {
                assertEquals("form", dataFlow.getFormat());
            }
        });

        ItemDescription web = landscapeDescription.getItemDescriptions().pick("wordpress-web", null);
        assertEquals(Item.LAYER_INGRESS, web.getLabel("layer"));
        assertEquals("wordpress-web", web.getIdentifier());
        assertEquals("Webserver", web.getDescription());
        assertEquals("Apache", web.getLabel(Label.software));
        assertEquals("2.4", web.getLabel(Label.version));
        assertEquals("Pentium 1 512MB RAM", web.getLabel("machine"));
        assertEquals("ops guys", web.getLabel(Label.team));
        assertEquals("content", web.getLabels(Label.network).values().toArray()[0]);
        assertEquals("docker", web.getLabel("hosttype"));
        assertEquals("Host(`test.localhost`) && PathPrefix(`/test`)", web.getLabel("traefik.http.routers.router0.rule"));
        assertEquals("auth", web.getLabel("traefik.http.routers.router0.middlewares"));
    }


    @Test
    public void mergeGroups() {

        SourceReference file = new SourceReference(getRootPath() + "/src/test/resources/example/services/dashboard.yml");

        //when
        descriptionFactory.applyData(file, null, landscapeDescription);

        //then
        Map<String, GroupDescription> groups = landscapeDescription.getGroups();
        assertThat(groups).isNotEmpty();

        GroupDescription ingress = groups.get("ingress");
        assertThat(ingress).isNotNull();
        assertThat(ingress.getDescription()).isEqualTo("This group provides authentication.");
    }

    @Test
    public void mergeTemplates() {

        SourceReference file = new SourceReference(getRootPath() + "/src/test/resources/example/services/dashboard.yml");

        //when
        descriptionFactory.applyData(file, null, landscapeDescription);

        //then
        Map<String, ItemDescription> templates = landscapeDescription.getTemplates();
        assertThat(templates).hasSize(1);
    }

    private String getRootPath() {
        Path currentRelativePath = Paths.get("");
        return currentRelativePath.toAbsolutePath().toString();
    }
}
