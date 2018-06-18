package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.Environment;
import de.bonndan.nivio.input.dto.ServiceDescription;
import de.bonndan.nivio.input.dto.ServiceDescriptionFactory;
import de.bonndan.nivio.landscape.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class IndexerTest {

    @Autowired
    ServiceRepository serviceRepository;

    @Autowired
    LandscapeRepository environmentRepo;


    @Test
    public void testIndexing() {
        File file = new File(getRootPath() + "/src/test/resources/example/example_env.yml");
        Environment environment = EnvironmentFactory.fromYaml(file);
        for (Source source : environment.getSources()) {
            ServiceDescription serviceDescription = ServiceDescriptionFactory.fromYaml(new File(source.getFullUrl()));
            environment.addServiceDescription(serviceDescription);
        }

        Indexer indexer = new Indexer(environmentRepo, serviceRepository);

        Landscape landscape = indexer.reIndex(environment);

        Assertions.assertNotNull(landscape);
        Assertions.assertNotNull(landscape.getServices());
        Assertions.assertEquals(1, landscape.getServices().size());
        Optional<Service> matchingObject = landscape.getServices().stream().
                filter(p -> p.getIdentifier().equals("blog-server")).
                findFirst();
        Service blog = matchingObject.get();
        Assertions.assertNotNull(blog);
        Assertions.assertEquals(3, blog.getProvidedBy().size());

        Optional<Service> webserver = blog.getProvidedBy().stream().
                filter(p -> p.getIdentifier().equals("wordpress-web")).
                findFirst();
        Assertions.assertNotNull(webserver.get());
        Assertions.assertEquals(1, webserver.get().getProvides().size());
    }

    private String getRootPath() {
        Path currentRelativePath = Paths.get("");
        return currentRelativePath.toAbsolutePath().toString();
    }
}
