package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.Environment;
import de.bonndan.nivio.input.dto.ServiceDescription;
import de.bonndan.nivio.landscape.DataFlowItem;
import de.bonndan.nivio.util.RootPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.List;
import java.util.Set;

import static de.bonndan.nivio.landscape.ServiceItems.pick;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

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
    public void resolveOneReferenceIsNotAvailable() {

        //given
        File file = new File(RootPath.get() + "/src/test/resources/example/example_broken.yml");
        Environment environment = EnvironmentFactory.fromYaml(file);
        assertFalse(environment.getSourceReferences().isEmpty());
        assertFalse(environment.isPartial());

        //when
        SourceReferencesResolver sourceReferencesResolver = new SourceReferencesResolver();
        sourceReferencesResolver.resolve(environment, log);

        //then
        assertFalse(StringUtils.isEmpty(log.getError()));
        assertTrue(environment.isPartial());
    }

    @Test
    public void assignTemplateToAll() {

        File file = new File(RootPath.get() + "/src/test/resources/example/example_templates.yml");
        Environment environment = EnvironmentFactory.fromYaml(file);
        assertFalse(environment.getSourceReferences().isEmpty());

        SourceReferencesResolver sourceReferencesResolver = new SourceReferencesResolver();
        sourceReferencesResolver.resolve(environment, log);

        ServiceDescription redis = (ServiceDescription) pick("redis", null, environment.getServiceDescriptions());
        assertNotNull(redis);
        assertEquals("foo", redis.getGroup());

        ServiceDescription datadog = (ServiceDescription) pick("datadog", null, environment.getServiceDescriptions());
        assertNotNull(datadog);
        assertEquals("foo", datadog.getGroup());

        //web has previously been assigned to group "content" and will not be overwritten by further templates
        ServiceDescription web = (ServiceDescription) pick("web", null, environment.getServiceDescriptions());
        assertNotNull(web);
        assertEquals("content", web.getGroup());
    }

    @Test
    public void assignTemplateWithRegex() {

        File file = new File(RootPath.get() + "/src/test/resources/example/example_templates2.yml");
        Environment environment = EnvironmentFactory.fromYaml(file);
        assertFalse(environment.getSourceReferences().isEmpty());

        SourceReferencesResolver sourceReferencesResolver = new SourceReferencesResolver();
        sourceReferencesResolver.resolve(environment, log);

        ServiceDescription one = (ServiceDescription) pick("crappy_dockername-78345", null, environment.getServiceDescriptions());
        assertNotNull(one);
        assertEquals("alpha", one.getGroup());

        ServiceDescription two = (ServiceDescription) pick("crappy_dockername-2343a", null, environment.getServiceDescriptions());
        assertNotNull(two);
        assertEquals("alpha", two.getGroup());

        ServiceDescription three = (ServiceDescription) pick("other_crappy_name-2343a", null, environment.getServiceDescriptions());
        assertNotNull(three);
        assertEquals("beta", three.getGroup());
    }

    @Test
    public void assignsAllValues() {

        File file = new File(RootPath.get() + "/src/test/resources/example/example_templates.yml");
        Environment environment = EnvironmentFactory.fromYaml(file);
        assertFalse(environment.getSourceReferences().isEmpty());

        SourceReferencesResolver sourceReferencesResolver = new SourceReferencesResolver();
        sourceReferencesResolver.resolve(environment, log);


        //web has previously been assigned to group "content" and will not be overwritten by further templates
        ServiceDescription web = (ServiceDescription) pick("web", null, environment.getServiceDescriptions());
        assertNotNull(web);
        assertEquals("content", web.getGroup());

        //other values from template
        assertNull(web.getName());
        assertNull(web.getShort_name());
        assertEquals("Wordpress", web.getSoftware());
        assertEquals("alphateam", web.getTeam());
        assertEquals("alphateam@acme.io", web.getContact());
        assertEquals(1, web.getTags().length);
    }

    @Test
    public void assignsOnlyToReferences() {

        File file = new File(RootPath.get() + "/src/test/resources/example/example_templates.yml");
        Environment environment = EnvironmentFactory.fromYaml(file);
        assertFalse(environment.getSourceReferences().isEmpty());

        SourceReferencesResolver sourceReferencesResolver = new SourceReferencesResolver();
        sourceReferencesResolver.resolve(environment, log);


        ServiceDescription redis = (ServiceDescription) pick("redis", null, environment.getServiceDescriptions());
        assertNotNull(redis);
        assertNull(redis.getSoftware());
    }

    @Test
    public void resolvesTemplatePlaceholdersInProviders() {

        File file = new File(RootPath.get() + "/src/test/resources/example/example_templates2.yml");
        Environment environment = EnvironmentFactory.fromYaml(file);

        SourceReferencesResolver sourceReferencesResolver = new SourceReferencesResolver();
        sourceReferencesResolver.resolve(environment, log);


        //the provider has been resolved using a query instead of naming a service
        ServiceDescription providedbyBar = (ServiceDescription) pick("crappy_dockername-78345", null, environment.getServiceDescriptions());
        assertNotNull(providedbyBar);
        assertNotNull(providedbyBar.getProvided_by());
        List<String> provided_by = providedbyBar.getProvided_by();
        String s = provided_by.get(0);
        assertEquals("crappy_dockername-2343a", s);
    }

    @Test
    public void resolvesTemplatePlaceholdersInDataflow() {

        File file = new File(RootPath.get() + "/src/test/resources/example/example_templates2.yml");
        Environment environment = EnvironmentFactory.fromYaml(file);

        SourceReferencesResolver sourceReferencesResolver = new SourceReferencesResolver();
        sourceReferencesResolver.resolve(environment, log);

        ServiceDescription hasdataFlow = (ServiceDescription) pick("crappy_dockername-78345", null, environment.getServiceDescriptions());
        assertNotNull(hasdataFlow);
        assertNotNull(hasdataFlow.getDataFlow());
        Set<DataFlowItem> dataFlow = hasdataFlow.getDataFlow();
        assertFalse(dataFlow.isEmpty());
        DataFlowItem s = (DataFlowItem) dataFlow.toArray()[0];
        assertEquals("nivio:templates2/beta/other_crappy_name-2343a", s.getTarget());
    }
}
