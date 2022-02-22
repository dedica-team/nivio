package de.bonndan.nivio.input;

import de.bonndan.nivio.IntegrationTestSupport;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.RelationDescription;
import de.bonndan.nivio.model.RelationType;
import de.bonndan.nivio.util.RootPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class RelationEndpointResolverTest {

    private RelationEndpointResolver relationEndpointResolver;

    private IntegrationTestSupport testSupport;


    @BeforeEach
    public void setup() {
        ProcessLog log = new ProcessLog(mock(Logger.class), "test");
        relationEndpointResolver = new RelationEndpointResolver(log);
        testSupport = new IntegrationTestSupport();
    }

    @Test
    void assignTemplateWithRegex() {

        LandscapeDescription landscapeDescription = getLandscapeDescriptionWithAppliedTemplates();
        relationEndpointResolver.resolve(landscapeDescription);

        ItemDescription one = landscapeDescription.getIndexReadAccess()
                .matchOneByIdentifiers("crappy_dockername-78345", null, ItemDescription.class)
                .orElseThrow();
        assertNotNull(one);
        assertEquals("alpha", one.getGroup());

        ItemDescription two = landscapeDescription.getIndexReadAccess()
                .matchOneByIdentifiers("crappy_dockername-2343a", null, ItemDescription.class)
                .orElseThrow();

        assertNotNull(two);
        assertEquals("alpha", two.getGroup());

        ItemDescription three = landscapeDescription.getIndexReadAccess()
                .matchOneByIdentifiers("crappy_dockername-2343a", null, ItemDescription.class)
                .orElseThrow();

        assertNotNull(three);
        assertEquals("beta", three.getGroup());
    }


    @Test
    void resolvesTemplatePlaceholdersInProviders() {

        LandscapeDescription landscapeDescription = getLandscapeDescriptionWithAppliedTemplates();
        relationEndpointResolver.resolve(landscapeDescription);

        //the provider has been resolved using a query instead of naming a service
        ItemDescription providedbyBar = landscapeDescription.getIndexReadAccess()
                .matchOneByIdentifiers("crappy_dockername-78345", null, ItemDescription.class)
                .orElseThrow();
        assertNotNull(providedbyBar);

        List<RelationDescription> relations = providedbyBar.getRelations().stream()
                .filter(relation -> RelationType.PROVIDER.equals(relation.getType()))
                .collect(Collectors.toUnmodifiableList());
        assertNotNull(relations);
        assertEquals(1, relations.size());
        RelationDescription s = relations.iterator().next();
        assertEquals("nivio:templates2/alpha/crappy_dockername-2343a", s.getSource());
    }

    @Test
    void resolvesTemplatePlaceholdersInDataflow() {

        LandscapeDescription landscapeDescription = getLandscapeDescriptionWithAppliedTemplates();

        //when
        relationEndpointResolver.resolve(landscapeDescription);

        ItemDescription hasdataFlow = landscapeDescription.getIndexReadAccess()
                .matchOneByIdentifiers("crappy_dockername-78345", null, ItemDescription.class)
                .orElseThrow();
        assertNotNull(hasdataFlow);
        assertNotNull(hasdataFlow.getRelations());
        List<RelationDescription> relations = hasdataFlow.getRelations().stream()
                .filter(relation -> RelationType.DATAFLOW.equals(relation.getType()))
                .collect(Collectors.toUnmodifiableList());
        assertFalse(relations.isEmpty());
        RelationDescription next = relations.iterator().next();
        assertEquals("nivio:templates2/beta/other_crappy_name-2343a", next.getTarget());
    }

    private LandscapeDescription getLandscapeDescriptionWithAppliedTemplates() {
        File file = new File(RootPath.get() + "/src/test/resources/example/example_templates2.yml");
        LandscapeDescription resolved = testSupport.getFirstLandscapeDescription(file);
        ProcessLog logger = new ProcessLog(mock(Logger.class), "test");
        assertThat(resolved.getTemplates()).isNotEmpty();
        new TemplateResolver(logger).resolve(resolved);
        return resolved;
    }

}
