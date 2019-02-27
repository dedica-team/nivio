package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.Environment;
import de.bonndan.nivio.input.dto.ServiceDescription;
import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.landscape.ServiceItem;
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

import static de.bonndan.nivio.landscape.ServiceItems.find;
import static de.bonndan.nivio.landscape.ServiceItems.pick;
import static org.junit.Assert.*;
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

        ServiceDescription mapped = (ServiceDescription) pick("blog-server", null, environment.getServiceDescriptions());
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

    @Test
    public void autoGroups() {

        File file = new File(RootPath.get() + "/src/test/resources/example/example_formats.yml");
        Environment environment = EnvironmentFactory.fromYaml(file);
        assertFalse(environment.getSourceReferences().isEmpty());
        SourceReference dockerComposeRef = environment.getSourceReferences().get(1);

        assertEquals("webservice", dockerComposeRef.getAutoGroup());

        SourceReferencesResolver sourceReferencesResolver = new SourceReferencesResolver();
        sourceReferencesResolver.resolve(environment, log);


        ServiceItem web = find("web", "webservice", environment.getServiceDescriptions());
        assertNotNull("web");
        assertEquals("webservice", web.getGroup());
        ServiceItem blogService = find("blog-server", null, environment.getServiceDescriptions());
        assertNotNull(blogService);
        assertFalse("webservice".equals(blogService.getGroup()));
    }
}
