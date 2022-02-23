package de.bonndan.nivio.input;

import de.bonndan.nivio.IntegrationTestSupport;
import de.bonndan.nivio.assessment.Assessment;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.model.Tagged;
import de.bonndan.nivio.util.RootPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class TemplateResolverTest {

    private TemplateResolver templateResolver;
    private IntegrationTestSupport testSupport;

    @BeforeEach
    public void setup() {
        ProcessLog log = new ProcessLog(LoggerFactory.getLogger(TemplateResolver.class), "test");
        templateResolver = new TemplateResolver(log);
        testSupport = new IntegrationTestSupport();
    }


    @Test
    void assignTemplateToAll() {

        LandscapeDescription landscapeDescription = getIndexedLandscapeDescription("/src/test/resources/example/example_templates.yml");

        //when
        templateResolver.resolve(landscapeDescription);

        //then
        ItemDescription redis = landscapeDescription.getIndexReadAccess().matchOneByIdentifiers("redis", null, ItemDescription.class).orElseThrow();
        assertNotNull(redis);
        assertEquals("foo", redis.getGroup());

        ItemDescription datadog = landscapeDescription.getIndexReadAccess().matchOneByIdentifiers("datadog", null, ItemDescription.class).orElseThrow();
        assertNotNull(datadog);
        assertEquals("foo", datadog.getGroup());

        //web has previously been assigned to group "content" and will not be overwritten by further templates
        ItemDescription web = landscapeDescription.getIndexReadAccess().matchOneByIdentifiers("web", null, ItemDescription.class).orElseThrow();
        assertNotNull(web);
        assertEquals("content", web.getGroup());
    }


    @Test
    void assignTemplateWithRegex() {

        LandscapeDescription landscapeDescription = getIndexedLandscapeDescription("/src/test/resources/example/example_templates2.yml");

        //when
        templateResolver.resolve(landscapeDescription);

        ItemDescription one = landscapeDescription.getIndexReadAccess().matchOneByIdentifiers("crappy_dockername-78345", null, ItemDescription.class).orElseThrow();
        assertNotNull(one);
        assertEquals("alpha", one.getGroup());

        ItemDescription two = landscapeDescription.getIndexReadAccess().matchOneByIdentifiers("crappy_dockername-2343a", null, ItemDescription.class).orElseThrow();
        assertNotNull(two);
        assertEquals("alpha", two.getGroup());

        ItemDescription three = landscapeDescription.getIndexReadAccess().matchOneByIdentifiers("other_crappy_name-2343a", null, ItemDescription.class).orElseThrow();
        assertNotNull(three);
        assertEquals("beta", three.getGroup());
    }

    @Test
    void assignsAllValues() {

        LandscapeDescription landscapeDescription = getIndexedLandscapeDescription("/src/test/resources/example/example_templates.yml");

        //when
        templateResolver.resolve(landscapeDescription);


        //web has previously been assigned to group "content" and will not be overwritten by further templates
        ItemDescription web = landscapeDescription.getIndexReadAccess().matchOneByIdentifiers("web", null, ItemDescription.class).orElseThrow();
        assertEquals("content", web.getGroup());

        //other values from template
        assertNull(web.getName());
        assertEquals("Wordpress", web.getLabel(Label.software));
        assertEquals("alphateam", web.getLabel(Label.team));
        assertEquals("alphateam@acme.io", web.getContact());
        assertEquals(1, web.getLabels(Tagged.LABEL_PREFIX_TAG).size());
    }

    @Test
    void assignsRelations() {

        LandscapeDescription landscapeDescription = getIndexedLandscapeDescription("/src/test/resources/example/example_templates.yml");

        //when
        templateResolver.resolve(landscapeDescription);


        //web has previously been assigned to group "content" and will not be overwritten by further templates
        ItemDescription web = landscapeDescription.getIndexReadAccess().matchOneByIdentifiers("web", null, ItemDescription.class).orElseThrow();
        assertEquals("content", web.getGroup());

        //other values from template
        assertNull(web.getName());
        assertThat(web.getProvidedBy()).isNotEmpty().hasSize(3);
        assertThat(web.getRelations()).isNotEmpty();
    }

    @Test
    void assignsOnlyToGivenTargets() {

        LandscapeDescription landscapeDescription = getIndexedLandscapeDescription("/src/test/resources/example/example_templates.yml");

        //when
        templateResolver.resolve(landscapeDescription);

        ItemDescription redis = landscapeDescription.getIndexReadAccess().matchOneByIdentifiers("redis", null, ItemDescription.class).orElseThrow();
        assertNotNull(redis);
        assertNull(redis.getLabel(Label.software));
    }

    private LandscapeDescription getIndexedLandscapeDescription(String s) {
        File file = new File(RootPath.get() + s);
        LandscapeDescription firstLandscapeDescription = testSupport.getFirstLandscapeDescription(file);

        firstLandscapeDescription.getIndexReadAccess().indexForSearch(Assessment.empty());

        return firstLandscapeDescription;
    }

}
