package de.bonndan.nivio.input;

import de.bonndan.nivio.input.compose2.ItemDescriptionFactoryCompose2;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.nivio.ItemDescriptionFactoryNivio;
import de.bonndan.nivio.util.RootPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

import static de.bonndan.nivio.model.Items.pick;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TemplateResolverTest {

    private TemplateResolver templateResolver;

    @Mock
    ProcessLog log;

    @BeforeEach
    public void setup() {
        log = new ProcessLog(LoggerFactory.getLogger(TemplateResolver.class));
        templateResolver = new TemplateResolver();
    }


    @Test
    public void assignTemplateToAll() {

        LandscapeDescription landscapeDescription = getLandscapeDescription("/src/test/resources/example/example_templates.yml");
        Map<ItemDescription, List<String>> templates = getTemplates(landscapeDescription);
        templateResolver.processTemplates(landscapeDescription, templates);


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

        LandscapeDescription landscapeDescription = getLandscapeDescription("/src/test/resources/example/example_templates2.yml");
        Map<ItemDescription, List<String>> templates = getTemplates(landscapeDescription);
        templateResolver.processTemplates(landscapeDescription, templates);

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

        LandscapeDescription landscapeDescription = getLandscapeDescription("/src/test/resources/example/example_templates.yml");
        Map<ItemDescription, List<String>> templates = getTemplates(landscapeDescription);
        templateResolver.processTemplates(landscapeDescription, templates);


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
    public void assignsOnlyToGivenTargets() {

        LandscapeDescription landscapeDescription = getLandscapeDescription("/src/test/resources/example/example_templates.yml");
        Map<ItemDescription, List<String>> templates = getTemplates(landscapeDescription);
        templateResolver.processTemplates(landscapeDescription, templates);

        ItemDescription redis = (ItemDescription) pick("redis", null, landscapeDescription.getItemDescriptions());
        assertNotNull(redis);
        assertNull(redis.getSoftware());
    }

    private Map<ItemDescription, List<String>> getTemplates(LandscapeDescription landscapeDescription) {
        ItemDescriptionFormatFactory ff = new ItemDescriptionFormatFactory(
                new ArrayList<>(Arrays.asList(ItemDescriptionFactoryNivio.forTesting(), ItemDescriptionFactoryCompose2.forTesting()))
        );
        SourceReferencesResolver sourceReferencesResolver = new SourceReferencesResolver(ff, log);
        Map<ItemDescription, List<String>> templateAndTargets = new HashMap<>();
        sourceReferencesResolver.resolve(landscapeDescription, templateAndTargets);
        return templateAndTargets;
    }

    private LandscapeDescription getLandscapeDescription(String s) {
        File file = new File(RootPath.get() + s);
        return LandscapeDescriptionFactory.fromYaml(file);
    }

}