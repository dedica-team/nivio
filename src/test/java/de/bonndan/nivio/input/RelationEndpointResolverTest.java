package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.http.HttpService;
import de.bonndan.nivio.input.nivio.ItemDescriptionFactoryNivio;
import de.bonndan.nivio.model.RelationItem;
import de.bonndan.nivio.model.RelationType;
import de.bonndan.nivio.util.RootPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.springframework.context.ApplicationEventPublisher;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class RelationEndpointResolverTest {

    private RelationEndpointResolver relationEndpointResolver;
    private LandscapeDescriptionFactory factory;

    @Mock
    ProcessLog log;


    @BeforeEach
    public void setup() {
        log = new ProcessLog(Mockito.mock(Logger.class));
        relationEndpointResolver = new RelationEndpointResolver(log);
        FileFetcher fileFetcher = new FileFetcher(mock(HttpService.class));
        factory = new LandscapeDescriptionFactory(mock(ApplicationEventPublisher.class), fileFetcher);
    }

    @Test
    public void assignTemplateWithRegex() {

        LandscapeDescription landscapeDescription = getLandscapeDescriptionWithAppliedTemplates("/src/test/resources/example/example_templates2.yml");
        relationEndpointResolver.processRelations(landscapeDescription);

        ItemDescription one = landscapeDescription.getItemDescriptions().pick("crappy_dockername-78345", null);
        assertNotNull(one);
        assertEquals("alpha", one.getGroup());

        ItemDescription two = landscapeDescription.getItemDescriptions().pick("crappy_dockername-2343a", null);
        assertNotNull(two);
        assertEquals("alpha", two.getGroup());

        ItemDescription three = landscapeDescription.getItemDescriptions().pick("other_crappy_name-2343a", null);
        assertNotNull(three);
        assertEquals("beta", three.getGroup());
    }


    @Test
    public void resolvesTemplatePlaceholdersInProviders() {

        LandscapeDescription landscapeDescription = getLandscapeDescriptionWithAppliedTemplates("/src/test/resources/example/example_templates2.yml");
        relationEndpointResolver.processRelations(landscapeDescription);

        //the provider has been resolved using a query instead of naming a service
        ItemDescription providedbyBar = landscapeDescription.getItemDescriptions().pick("crappy_dockername-78345", null);
        assertNotNull(providedbyBar);
        List<RelationItem> relations = RelationType.PROVIDER.filter(providedbyBar.getRelations());
        assertNotNull(relations);
        assertEquals(1, relations.size());
        RelationItem s = relations.iterator().next();
        assertEquals("nivio:templates2/alpha/crappy_dockername-2343a", s.getSource());
    }

    @Test
    public void resolvesTemplatePlaceholdersInDataflow() {

        LandscapeDescription landscapeDescription = getLandscapeDescriptionWithAppliedTemplates("/src/test/resources/example/example_templates2.yml");
        relationEndpointResolver.processRelations(landscapeDescription);

        ItemDescription hasdataFlow = landscapeDescription.getItemDescriptions().pick("crappy_dockername-78345", null);
        assertNotNull(hasdataFlow);
        assertNotNull(hasdataFlow.getRelations());
        List<RelationItem> relations = RelationType.DATAFLOW.filter(hasdataFlow.getRelations());
        assertFalse(relations.isEmpty());
        RelationItem next = relations.iterator().next();
        assertEquals("nivio:templates2/beta/other_crappy_name-2343a", next.getTarget());
    }

    private Map<ItemDescription, List<String>> getTemplates(LandscapeDescription landscapeDescription) {
        SourceReferencesResolver sourceReferencesResolver = new SourceReferencesResolver(ItemDescriptionFormatFactory.with(ItemDescriptionFactoryNivio.forTesting()), log);
        Map<ItemDescription, List<String>> templateAndTargets = new HashMap<>();
        sourceReferencesResolver.resolve(landscapeDescription, templateAndTargets);
        return templateAndTargets;
    }

    private LandscapeDescription getLandscapeDescriptionWithAppliedTemplates(String s) {
        File file = new File(RootPath.get() + s);
        LandscapeDescription landscapeDescription = factory.fromYaml(file);

        new TemplateResolver().processTemplates(landscapeDescription, getTemplates(landscapeDescription));

        return landscapeDescription;
    }

}