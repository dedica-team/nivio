package de.bonndan.nivio.input;

import de.bonndan.nivio.input.compose2.InputFormatHandlerCompose2;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.RelationDescription;
import de.bonndan.nivio.input.http.HttpService;
import de.bonndan.nivio.input.nivio.InputFormatHandlerNivio;
import de.bonndan.nivio.model.RelationType;
import de.bonndan.nivio.util.RootPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.springframework.context.ApplicationEventPublisher;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class RelationEndpointResolverTest {

    private RelationEndpointResolver relationEndpointResolver;
    private LandscapeDescriptionFactory factory;

    @Mock
    ProcessLog log;


    @BeforeEach
    public void setup() {
        log = new ProcessLog(Mockito.mock(Logger.class), "test");
        relationEndpointResolver = new RelationEndpointResolver(log);
        FileFetcher fileFetcher = new FileFetcher(mock(HttpService.class));
        factory = new LandscapeDescriptionFactory(fileFetcher);
    }

    @Test
    public void assignTemplateWithRegex() {

        LandscapeDescription landscapeDescription = getLandscapeDescriptionWithAppliedTemplates("/src/test/resources/example/example_templates2.yml");
        relationEndpointResolver.resolve(landscapeDescription);

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
        relationEndpointResolver.resolve(landscapeDescription);

        //the provider has been resolved using a query instead of naming a service
        ItemDescription providedbyBar = landscapeDescription.getItemDescriptions().pick("crappy_dockername-78345", null);
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
    public void resolvesTemplatePlaceholdersInDataflow() {

        LandscapeDescription landscapeDescription = getLandscapeDescriptionWithAppliedTemplates("/src/test/resources/example/example_templates2.yml");
        relationEndpointResolver.resolve(landscapeDescription);

        ItemDescription hasdataFlow = landscapeDescription.getItemDescriptions().pick("crappy_dockername-78345", null);
        assertNotNull(hasdataFlow);
        assertNotNull(hasdataFlow.getRelations());
        List<RelationDescription> relations = hasdataFlow.getRelations().stream()
                .filter(relation -> RelationType.DATAFLOW.equals(relation.getType()))
                .collect(Collectors.toUnmodifiableList());
        assertFalse(relations.isEmpty());
        RelationDescription next = relations.iterator().next();
        assertEquals("nivio:templates2/beta/other_crappy_name-2343a", next.getTarget());
    }

    private LandscapeDescription getLandscapeDescriptionWithAppliedTemplates(String s) {
        File file = new File(RootPath.get() + s);
        LandscapeDescription landscapeDescription = factory.fromYaml(file);

        InputFormatHandlerFactory formatFactory = new InputFormatHandlerFactory(
                new ArrayList<>(Arrays.asList(new InputFormatHandlerNivio(new FileFetcher(new HttpService())), InputFormatHandlerCompose2.forTesting()))
        );
        ProcessLog logger = new ProcessLog(mock(Logger.class), "test");
        SourceReferencesResolver sourceReferencesResolver = new SourceReferencesResolver(formatFactory, logger, mock(ApplicationEventPublisher.class));
        sourceReferencesResolver.resolve(landscapeDescription);

        new TemplateResolver(logger).resolve(landscapeDescription);

        return landscapeDescription;
    }

}
