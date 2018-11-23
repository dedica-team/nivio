package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.Environment;
import de.bonndan.nivio.input.dto.InterfaceDescription;
import de.bonndan.nivio.input.dto.ServiceDescription;
import de.bonndan.nivio.landscape.*;
import de.bonndan.nivio.service.NotificationService;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runners.MethodSorters;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IndexerIntegrationTest {

    @Autowired
    ServiceRepository serviceRepository;

    @Autowired
    LandscapeRepository environmentRepo;

    @Mock
    NotificationService notificationService;

    @MockBean
    JavaMailSender mailSender;

    private Landscape index() {
        File file = new File(getRootPath() + "/src/test/resources/example/example_env.yml");
        Environment environment = EnvironmentFactory.fromYaml(file);

        Indexer indexer = new Indexer(environmentRepo, serviceRepository, notificationService);

        Landscape landscape = indexer.reIndex(environment);

        return landscape;
    }

    @Test //first pass
    public void testIndexing() {
        Landscape landscape = index();

        Assertions.assertNotNull(landscape);
        assertEquals("mail@acme.org", landscape.getContact());
        Assertions.assertNotNull(landscape.getServices());
        assertEquals(7, landscape.getServices().size());
        Service blog = Utils.pick("blog-server", landscape.getServices());
        Assertions.assertNotNull(blog);
        assertEquals(3, blog.getProvidedBy().size());

        Service webserver = Utils.pick("wordpress-web", blog.getProvidedBy());
        Assertions.assertNotNull(webserver);
        assertEquals(1, webserver.getProvides().size());

        DataFlow push = (DataFlow) blog.getDataFlow().stream()
                .filter(d -> d.getDescription().equals("push"))
                .findFirst()
                .orElse(null);

        Assertions.assertNotNull(push);

        assertEquals("push", push.getDescription());
        assertEquals("json", push.getFormat());
        assertEquals(blog.getIdentifier(), push.getSourceEntity().getIdentifier());
        assertEquals("kpi-dashboard", push.getTarget());

        Set<InterfaceItem> interfaces = blog.getInterfaces();
        assertEquals(3, interfaces.size());
        InterfaceDescription i = (InterfaceDescription) blog.getInterfaces().stream()
                .filter(d -> d.getDescription().equals("posts"))
                .findFirst()
                .orElseThrow();
        assertEquals("form", i.getFormat());
    }

    @Test //second pass
    public void testReIndexing() {
        Landscape landscape = index();

        Assertions.assertNotNull(landscape);
        assertEquals("mail@acme.org", landscape.getContact());
        Assertions.assertNotNull(landscape.getServices());
        assertEquals(7, landscape.getServices().size());
        Service blog = Utils.pick("blog-server", landscape.getServices());
        Assertions.assertNotNull(blog);
        assertEquals(3, blog.getProvidedBy().size());

        Service webserver = Utils.pick("wordpress-web", blog.getProvidedBy());
        Assertions.assertNotNull(webserver);
        assertEquals(1, webserver.getProvides().size());

        DataFlow push = (DataFlow) blog.getDataFlow().stream()
                .filter(d -> d.getDescription().equals("push"))
                .findFirst()
                .orElse(null);

        Assertions.assertNotNull(push);

        assertEquals("push", push.getDescription());
        assertEquals("json", push.getFormat());
        assertEquals(blog.getIdentifier(), push.getSourceEntity().getIdentifier());
        assertEquals("kpi-dashboard", push.getTarget());

        Set<InterfaceItem> interfaces = blog.getInterfaces();
        assertEquals(3, interfaces.size());
        InterfaceDescription i = (InterfaceDescription) blog.getInterfaces().stream()
                .filter(d -> d.getDescription().equals("posts"))
                .findFirst()
                .orElseThrow();
        assertEquals("form", i.getFormat());
    }

    @Test
    public void testIncrementalUpdate() {
        Landscape landscape = index();
        Service blog = Utils.pick("blog-server", landscape.getServices());
        int before = landscape.getServices().size();

        Environment environment = new Environment();
        environment.setIdentifier(landscape.getIdentifier());
        environment.setIsIncrement(true);

        ServiceDescription sd = new ServiceDescription();
        sd.setIdentifier(blog.getIdentifier());
        sd.setGroup("completelyNewGroup");

        environment.getServiceDescriptions().add(sd);

        Indexer indexer = new Indexer(environmentRepo, serviceRepository, notificationService);

        landscape = indexer.reIndex(environment);
        blog = Utils.pick("blog-server", landscape.getServices());
        assertEquals("completelyNewGroup", blog.getGroup());
        assertEquals(before, landscape.getServices().size());
    }

    private String getRootPath() {
        Path currentRelativePath = Paths.get("");
        return currentRelativePath.toAbsolutePath().toString();
    }
}
