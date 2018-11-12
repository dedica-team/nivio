package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.Environment;
import de.bonndan.nivio.input.dto.InterfaceDescription;
import de.bonndan.nivio.landscape.*;
import de.bonndan.nivio.service.NotificationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import java.util.Set;

import static org.mockito.MockitoAnnotations.initMocks;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class IndexerIntegrationTest {

    @Autowired
    ServiceRepository serviceRepository;

    @Autowired
    LandscapeRepository environmentRepo;

    @Mock
    NotificationService notificationService;

    @MockBean
    JavaMailSender mailSender;

    @Test
    public void testIndexing() {
        File file = new File(getRootPath() + "/src/test/resources/example/example_env.yml");
        Environment environment = EnvironmentFactory.fromYaml(file);

        Indexer indexer = new Indexer(environmentRepo, serviceRepository, notificationService);

        Landscape landscape = indexer.reIndex(environment);

        Assertions.assertNotNull(landscape);
        Assertions.assertEquals("mail@acme.org", landscape.getContact());
        Assertions.assertNotNull(landscape.getServices());
        Assertions.assertEquals(7, landscape.getServices().size());
        Service blog = Utils.pick("blog-server", landscape.getServices());
        Assertions.assertNotNull(blog);
        Assertions.assertEquals(3, blog.getProvidedBy().size());

        Service webserver = Utils.pick("wordpress-web", blog.getProvidedBy());
        Assertions.assertNotNull(webserver);
        Assertions.assertEquals(1, webserver.getProvides().size());

        DataFlow push = (DataFlow) blog.getDataFlow().stream()
                .filter(d -> d.getDescription().equals("push"))
                .findFirst()
                .orElseThrow();

        Assertions.assertNotNull(push);

        Assertions.assertEquals("push", push.getDescription());
        Assertions.assertEquals("json", push.getFormat());
        Assertions.assertEquals(blog.getIdentifier(), push.getSourceEntity().getIdentifier());
        Assertions.assertEquals("kpi-dashboard", push.getTarget());

        Set<InterfaceItem> interfaces = blog.getInterfaces();
        Assertions.assertEquals(3, interfaces.size());
        InterfaceDescription i = (InterfaceDescription) blog.getInterfaces().stream()
                .filter(d -> d.getDescription().equals("posts"))
                .findFirst()
                .orElseThrow();
        Assertions.assertEquals("form", i.getFormat());
    }

    private String getRootPath() {
        Path currentRelativePath = Paths.get("");
        return currentRelativePath.toAbsolutePath().toString();
    }
}
