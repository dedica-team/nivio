package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ServiceDescription;
import de.bonndan.nivio.input.dto.ServiceDescriptionFactory;
import de.bonndan.nivio.landscape.Service;
import de.bonndan.nivio.landscape.ServiceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
public class IndexerTest {

    @Mock
    ServiceRepository serviceRepository;

    @Mock
    EnvironmentRepo environmentRepo;


    @Test
    public void testIndexing() {
        File file = new File(getRootPath() + "/src/test/resources/example/example_env.yml");
        Environment environment = EnvironmentFactory.fromYaml(file);
        for (Source source : environment.getSources()) {
            ServiceDescription serviceDescription = ServiceDescriptionFactory.fromYaml(new File(source.getFullUrl()));
            environment.addService(serviceDescription);
        }

        Indexer indexer = new Indexer(environmentRepo, serviceRepository);

        Mockito.when(serviceRepository.findAllByEnvironment(environment.getIdentifier())).thenReturn(new ArrayList<>());

        indexer.reindex(environment);

        Mockito.verify(environmentRepo).save(environment);
        Mockito.verify(serviceRepository, Mockito.times(4)).save(Mockito.any(Service.class));
    }

    private String getRootPath() {
        Path currentRelativePath = Paths.get("");
        return currentRelativePath.toAbsolutePath().toString();
    }
}
