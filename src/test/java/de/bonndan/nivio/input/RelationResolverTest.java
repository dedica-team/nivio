package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.RelationItem;
import de.bonndan.nivio.model.RelationType;
import de.bonndan.nivio.util.RootPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static de.bonndan.nivio.model.Items.pick;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RelationResolverTest {

    private RelationResolver relationResolver;

    @Mock
    ProcessLog log;

    @BeforeEach
    public void setup() {
        log = new ProcessLog(Mockito.mock(Logger.class));
        relationResolver = new RelationResolver(log);
    }

    @Test
    public void assignTemplateWithRegex() {

        LandscapeDescription landscapeDescription = getLandscapeDescriptionWithAppliedTemplates("/src/test/resources/example/example_templates2.yml");
        relationResolver.processRelations(landscapeDescription);

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
    public void resolvesTemplatePlaceholdersInProviders() {

        LandscapeDescription landscapeDescription = getLandscapeDescriptionWithAppliedTemplates("/src/test/resources/example/example_templates2.yml");
        relationResolver.processRelations(landscapeDescription);

        //the provider has been resolved using a query instead of naming a service
        ItemDescription providedbyBar = (ItemDescription) pick("crappy_dockername-78345", null, landscapeDescription.getItemDescriptions());
        assertNotNull(providedbyBar);
        Set<RelationItem<String>> relations = providedbyBar.getRelations(RelationType.PROVIDER);
        assertNotNull(relations);
        assertEquals(1, relations.size());
        RelationItem s = relations.iterator().next();
        assertEquals("nivio:templates2/alpha/crappy_dockername-2343a", s.getSource());
    }

    @Test
    public void resolvesTemplatePlaceholdersInDataflow() {

        LandscapeDescription landscapeDescription = getLandscapeDescriptionWithAppliedTemplates("/src/test/resources/example/example_templates2.yml");
        relationResolver.processRelations(landscapeDescription);

        ItemDescription hasdataFlow = (ItemDescription) pick("crappy_dockername-78345", null, landscapeDescription.getItemDescriptions());
        assertNotNull(hasdataFlow);
        assertNotNull(hasdataFlow.getRelations());
        Set<RelationItem<String>> relations = hasdataFlow.getRelations(RelationType.DATAFLOW);
        assertFalse(relations.isEmpty());
        RelationItem next = relations.iterator().next();
        assertEquals("nivio:templates2/beta/other_crappy_name-2343a", next.getTarget());
    }

    private Map<ItemDescription, List<String>> getTemplates(LandscapeDescription landscapeDescription) {
        SourceReferencesResolver sourceReferencesResolver = new SourceReferencesResolver(log);
        Map<ItemDescription, List<String>> templateAndTargets = new HashMap<>();
        sourceReferencesResolver.resolve(landscapeDescription, templateAndTargets);
        return templateAndTargets;
    }

    private LandscapeDescription getLandscapeDescriptionWithAppliedTemplates(String s) {
        File file = new File(RootPath.get() + s);
        LandscapeDescription landscapeDescription = LandscapeDescriptionFactory.fromYaml(file);

        new TemplateResolver().processTemplates(landscapeDescription, getTemplates(landscapeDescription));

        return landscapeDescription;
    }

}