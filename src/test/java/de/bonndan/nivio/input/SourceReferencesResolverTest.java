package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.Environment;
import de.bonndan.nivio.input.dto.ServiceDescription;
import de.bonndan.nivio.landscape.ServiceItems;
import de.bonndan.nivio.util.RootPath;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.springframework.util.StringUtils;

import java.io.File;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;

public class SourceReferencesResolverTest {

    @Mock
    ProcessLog log;

    @BeforeEach
    public void setup() {
        log = new ProcessLog(Mockito.mock(Logger.class));
    }

    @Test
    public void resolve() {

        File file = new File(RootPath.get() + "/src/test/resources/example/example_incremental_env.yml");
        Environment environment = EnvironmentFactory.fromYaml(file);
        assertFalse(environment.getSourceReferences().isEmpty());

        SourceReferencesResolver sourceReferencesResolver = new SourceReferencesResolver();
        sourceReferencesResolver.resolve(environment, log);

        ServiceDescription mapped = (ServiceDescription) ServiceItems.pick("blog-server", null, environment.getServiceDescriptions());
        assertNotNull(mapped);
        assertEquals("blog1", mapped.getShort_name());
        assertEquals("name2", mapped.getName());
    }

    @Test
    public void resolveBreaks() {

        File file = new File(RootPath.get() + "/src/test/resources/example/example_broken.yml");
        Environment environment = EnvironmentFactory.fromYaml(file);
        assertFalse(environment.getSourceReferences().isEmpty());

        SourceReferencesResolver sourceReferencesResolver = new SourceReferencesResolver();
        sourceReferencesResolver.resolve(environment, log);

        assertFalse(StringUtils.isEmpty(log.getError()));
    }
}
