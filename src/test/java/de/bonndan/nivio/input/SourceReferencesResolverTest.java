package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.model.DataFlowItem;
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

import static de.bonndan.nivio.model.ServiceItems.pick;
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
        LandscapeDescription landscapeDescription = LandscapeDescriptionFactory.fromYaml(file);
        assertFalse(landscapeDescription.getSourceReferences().isEmpty());

        SourceReferencesResolver sourceReferencesResolver = new SourceReferencesResolver();
        sourceReferencesResolver.resolve(landscapeDescription, log);

        ItemDescription mapped = (ItemDescription) pick("blog-server", null, landscapeDescription.getItemDescriptions());
        assertNotNull(mapped);
        assertEquals("blog1", mapped.getShortName());
        assertEquals("name2", mapped.getName());
    }

    @Test
    public void resolveOneReferenceIsNotAvailable() {

        //given
        File file = new File(RootPath.get() + "/src/test/resources/example/example_broken.yml");
        LandscapeDescription landscapeDescription = LandscapeDescriptionFactory.fromYaml(file);
        assertFalse(landscapeDescription.getSourceReferences().isEmpty());
        assertFalse(landscapeDescription.isPartial());

        //when
        SourceReferencesResolver sourceReferencesResolver = new SourceReferencesResolver();
        sourceReferencesResolver.resolve(landscapeDescription, log);

        //then
        assertFalse(StringUtils.isEmpty(log.getError()));
        assertTrue(landscapeDescription.isPartial());
    }

    @Test
    public void assignTemplateToAll() {

        File file = new File(RootPath.get() + "/src/test/resources/example/example_templates.yml");
        LandscapeDescription landscapeDescription = LandscapeDescriptionFactory.fromYaml(file);
        assertFalse(landscapeDescription.getSourceReferences().isEmpty());

        SourceReferencesResolver sourceReferencesResolver = new SourceReferencesResolver();
        sourceReferencesResolver.resolve(landscapeDescription, log);

        ItemDescription redis = (ItemDescription) pick("redis", null, landscapeDescription.getItemDescriptions());
        assertNotNull(redis);
        assertEquals("foo", redis.getGroup());

        ItemDescription datadog = (ItemDescription) pick("datadog", null, landscapeDescription.getItemDescriptions());
        assertNotNull(datadog);
        assertEquals("foo", datadog.getGroup());

        //web has previously been assigned to group "content" and will not be overwritten by further templates
        ItemDescription web = (ItemDescription) pick("web", null, landscapeDescription.getItemDescriptions());
        assertNotNull(web);
        assertEquals("content", web.getGroup());
    }

    @Test
    public void assignTemplateWithRegex() {

        File file = new File(RootPath.get() + "/src/test/resources/example/example_templates2.yml");
        LandscapeDescription landscapeDescription = LandscapeDescriptionFactory.fromYaml(file);
        assertFalse(landscapeDescription.getSourceReferences().isEmpty());

        SourceReferencesResolver sourceReferencesResolver = new SourceReferencesResolver();
        sourceReferencesResolver.resolve(landscapeDescription, log);

        ItemDescription one = (ItemDescription) pick("crappy_dockername-78345", null, landscapeDescription.getItemDescriptions());
        assertNotNull(one);
        assertEquals("alpha", one.getGroup());

        ItemDescription two = (ItemDescription) pick("crappy_dockername-2343a", null, landscapeDescription.getItemDescriptions());
        assertNotNull(two);
        assertEquals("alpha", two.getGroup());

        ItemDescription three = (ItemDescription) pick("other_crappy_name-2343a", null, landscapeDescription.getItemDescriptions());
        assertNotNull(three);
        assertEquals("beta", three.getGroup());
    }

    @Test
    public void assignsAllValues() {

        File file = new File(RootPath.get() + "/src/test/resources/example/example_templates.yml");
        LandscapeDescription landscapeDescription = LandscapeDescriptionFactory.fromYaml(file);
        assertFalse(landscapeDescription.getSourceReferences().isEmpty());

        SourceReferencesResolver sourceReferencesResolver = new SourceReferencesResolver();
        sourceReferencesResolver.resolve(landscapeDescription, log);


        //web has previously been assigned to group "content" and will not be overwritten by further templates
        ItemDescription web = (ItemDescription) pick("web", null, landscapeDescription.getItemDescriptions());
        assertNotNull(web);
        assertEquals("content", web.getGroup());

        //other values from template
        assertNull(web.getName());
        assertNull(web.getShortName());
        assertEquals("Wordpress", web.getSoftware());
        assertEquals("alphateam", web.getTeam());
        assertEquals("alphateam@acme.io", web.getContact());
        assertEquals(1, web.getTags().length);
    }

    @Test
    public void assignsOnlyToReferences() {

        File file = new File(RootPath.get() + "/src/test/resources/example/example_templates.yml");
        LandscapeDescription landscapeDescription = LandscapeDescriptionFactory.fromYaml(file);
        assertFalse(landscapeDescription.getSourceReferences().isEmpty());

        SourceReferencesResolver sourceReferencesResolver = new SourceReferencesResolver();
        sourceReferencesResolver.resolve(landscapeDescription, log);


        ItemDescription redis = (ItemDescription) pick("redis", null, landscapeDescription.getItemDescriptions());
        assertNotNull(redis);
        assertNull(redis.getSoftware());
    }

    @Test
    public void resolvesTemplatePlaceholdersInProviders() {

        File file = new File(RootPath.get() + "/src/test/resources/example/example_templates2.yml");
        LandscapeDescription landscapeDescription = LandscapeDescriptionFactory.fromYaml(file);

        SourceReferencesResolver sourceReferencesResolver = new SourceReferencesResolver();
        sourceReferencesResolver.resolve(landscapeDescription, log);


        //the provider has been resolved using a query instead of naming a service
        ItemDescription providedbyBar = (ItemDescription) pick("crappy_dockername-78345", null, landscapeDescription.getItemDescriptions());
        assertNotNull(providedbyBar);
        assertNotNull(providedbyBar.getProvidedBy());
        List<String> provided_by = providedbyBar.getProvidedBy();
        String s = provided_by.get(0);
        assertEquals("crappy_dockername-2343a", s);
    }

    @Test
    public void resolvesTemplatePlaceholdersInDataflow() {

        File file = new File(RootPath.get() + "/src/test/resources/example/example_templates2.yml");
        LandscapeDescription landscapeDescription = LandscapeDescriptionFactory.fromYaml(file);

        SourceReferencesResolver sourceReferencesResolver = new SourceReferencesResolver();
        sourceReferencesResolver.resolve(landscapeDescription, log);

        ItemDescription hasdataFlow = (ItemDescription) pick("crappy_dockername-78345", null, landscapeDescription.getItemDescriptions());
        assertNotNull(hasdataFlow);
        assertNotNull(hasdataFlow.getDataFlow());
        Set<DataFlowItem> dataFlow = hasdataFlow.getDataFlow();
        assertFalse(dataFlow.isEmpty());
        DataFlowItem s = (DataFlowItem) dataFlow.toArray()[0];
        assertEquals("nivio:templates2/beta/other_crappy_name-2343a", s.getTarget());
    }
}
