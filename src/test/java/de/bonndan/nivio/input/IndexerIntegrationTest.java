package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.Environment;
import de.bonndan.nivio.input.dto.InterfaceDescription;
import de.bonndan.nivio.input.dto.ServiceDescription;
import de.bonndan.nivio.landscape.*;
import de.bonndan.nivio.notification.NotificationService;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.Assertions;
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

import static de.bonndan.nivio.landscape.ServiceItems.pick;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
        return index("/src/test/resources/example/example_env.yml");
    }

    private Landscape index(String path) {
        File file = new File(getRootPath() + path);
        Environment environment = EnvironmentFactory.fromYaml(file);

        Indexer indexer = new Indexer(environmentRepo, serviceRepository, notificationService);

        ProcessLog processLog = indexer.reIndex(environment);
        return (Landscape) processLog.getLandscape();
    }

    @Test //first pass
    public void testIndexing() {
        Landscape landscape = index();

        Assertions.assertNotNull(landscape);
        assertEquals("mail@acme.org", landscape.getContact());
        Assertions.assertNotNull(landscape.getServices());
        assertEquals(8, landscape.getServices().size());
        Service blog = (Service) ServiceItems.pick("blog-server", null, landscape.getServices());
        Assertions.assertNotNull(blog);
        assertEquals(3, blog.getProvidedBy().size());

        Service webserver = (Service) ServiceItems.pick("wordpress-web", null, new ArrayList<>(blog.getProvidedBy()));
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
        assertEquals("http://acme.io/create", i.getUrl().toString());
    }

    @Test //second pass
    public void testReIndexing() {
        Landscape landscape = index();

        Assertions.assertNotNull(landscape);
        assertEquals("mail@acme.org", landscape.getContact());
        Assertions.assertNotNull(landscape.getServices());
        assertEquals(8, landscape.getServices().size());
        Service blog = (Service) ServiceItems.pick("blog-server", null,landscape.getServices());
        Assertions.assertNotNull(blog);
        assertEquals(3, blog.getProvidedBy().size());

        Service webserver = (Service) ServiceItems.pick("wordpress-web", null, new ArrayList<ServiceItem>(blog.getProvidedBy()));
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

    /**
     * wordpress-web updates must not create new services
     */
    @Test
    public void testIncrementalUpdate() {
        Landscape landscape = index();
        Service blog = (Service) ServiceItems.pick("blog-server", null, landscape.getServices());
        int before = landscape.getServices().size();

        Environment environment = new Environment();
        environment.setIdentifier(landscape.getIdentifier());
        environment.setIsPartial(true);

        ServiceDescription newItem = new ServiceDescription();
        newItem.setIdentifier(blog.getIdentifier());
        newItem.setGroup("completelyNewGroup");
        environment.getServiceDescriptions().add(newItem);

        ServiceDescription exsistingWordPress = new ServiceDescription();
        exsistingWordPress.setIdentifier("wordpress-web");
        exsistingWordPress.setName("Other name");
        environment.getServiceDescriptions().add(exsistingWordPress);

        Indexer indexer = new Indexer(environmentRepo, serviceRepository, notificationService);

        //created
        landscape = (Landscape) indexer.reIndex(environment).getLandscape();
        blog = (Service) ServiceItems.pick("blog-server", "completelyNewGroup", landscape.getServices());
        assertEquals("completelyNewGroup", blog.getGroup());
        assertEquals(before +1, landscape.getServices().size());

        //updated
        Service wordpress = (Service) ServiceItems.pick("wordpress-web", "content", landscape.getServices());
        assertEquals("Other name", wordpress.getName());
        assertEquals("content", wordpress.getGroup());


    }

    /**
     * Ensures that same names in different landscapes do not collide
     */
    @Test
    public void testNameConflictDifferentLandscapes() {
        Landscape landscape1 = index("/src/test/resources/example/example_env.yml");
        Landscape landscape2 = index("/src/test/resources/example/example_other.yml");

        Assertions.assertNotNull(landscape1);
        assertEquals("mail@acme.org", landscape1.getContact());
        Assertions.assertNotNull(landscape1.getServices());
        Service blog1 = (Service) ServiceItems.pick("blog-server", null,landscape1.getServices());
        Assertions.assertNotNull(blog1);
        assertEquals("blog", blog1.getShort_name());

        Assertions.assertNotNull(landscape2);
        assertEquals("nivio:other", landscape2.getIdentifier());
        assertEquals("mail@other.org", landscape2.getContact());
        Assertions.assertNotNull(landscape2.getServices());
        Service blog2 = (Service) ServiceItems.pick("blog-server", null,landscape2.getServices());
        Assertions.assertNotNull(blog2);
        assertEquals("blog1", blog2.getShort_name());
    }

    /**
     * Ensures that same names in different landscapes do not collide
     */
    @Test
    public void testDataflow() {
        Landscape landscape1 = index("/src/test/resources/example/example_dataflow.yml");

        Assertions.assertNotNull(landscape1);
        Assertions.assertNotNull(landscape1.getServices());
        Service blog1 = (Service) ServiceItems.pick("blog-server", "content1",landscape1.getServices());
        Assertions.assertNotNull(blog1);
        Service blog2 = (Service) ServiceItems.pick("blog-server", "content2",landscape1.getServices());
        Assertions.assertNotNull(blog2);
        assertEquals("Demo Blog", blog1.getName());
        assertEquals(
                FullyQualifiedIdentifier.build("nivio:dataflowtest", "content1", "blog-server").toString(),
                blog1.toString()
        );

        assertNotNull(blog1.getDataFlow());
        assertEquals(1, blog1.getDataFlow().size());
    }

    @Test
    public void environmentTemplatesApplied() {
        Landscape landscape = index("/src/test/resources/example/example_templates.yml");

        ServiceItem web = pick( "web", null, landscape.getServices());
        Assert.assertNotNull(web);
        assertEquals("web", web.getIdentifier());
        assertEquals("webservice", web.getType());
    }

    private String getRootPath() {
        Path currentRelativePath = Paths.get("");
        return currentRelativePath.toAbsolutePath().toString();
    }
}
