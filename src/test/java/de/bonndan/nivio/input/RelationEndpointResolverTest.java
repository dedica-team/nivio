package de.bonndan.nivio.input;

import de.bonndan.nivio.assessment.Assessment;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.RelationDescription;
import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import de.bonndan.nivio.model.RelationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class RelationEndpointResolverTest {

    private RelationEndpointResolver relationEndpointResolver;

    private ItemDescription crappy2343a;
    private ItemDescription crappy78345;
    private ItemDescription other2343a;
    private LandscapeDescription landscapeDescription;


    @BeforeEach
    public void setup() {

        landscapeDescription = new LandscapeDescription("test");

        crappy2343a = new ItemDescription();
        crappy2343a.setIdentifier("crappy_dockername-2343a");
        crappy2343a.setGroup("alpha");
        crappy2343a.setName("foo");
        var rel = new RelationDescription();
        rel.setTarget("identifier:other_crappy_name*");
        rel.setType(RelationType.DATAFLOW);
        crappy2343a.getRelations().add(rel);
        landscapeDescription.getWriteAccess().addOrReplaceChild(crappy2343a);

        crappy78345 = new ItemDescription();
        crappy78345.setIdentifier("crappy_dockername-78345");
        crappy78345.setGroup("alpha");
        crappy78345.setName("bar");
        crappy78345.getProvidedBy().add("identifier:crappy_dockername-23*");
        landscapeDescription.getWriteAccess().addOrReplaceChild(crappy78345);

        other2343a = new ItemDescription();
        other2343a.setIdentifier("other_crappy_name-2343a");
        other2343a.setGroup("beta");
        other2343a.setName("baz");
        landscapeDescription.getWriteAccess().addOrReplaceChild(other2343a);
        landscapeDescription.getReadAccess().indexForSearch(Assessment.empty());
        landscapeDescription.setProcessLog(new ProcessLog(mock(Logger.class), "test"));

        relationEndpointResolver = new RelationEndpointResolver();
    }

    @Test
    void assignTemplateWithRegex() {

        //when
        relationEndpointResolver.resolve(landscapeDescription);

        //then
        ItemDescription one = landscapeDescription.getReadAccess()
                .matchOneByIdentifiers("crappy_dockername-78345", null, ItemDescription.class)
                .orElseThrow();
        assertNotNull(one);
        assertEquals("alpha", one.getGroup());

        ItemDescription two = landscapeDescription.getReadAccess()
                .matchOneByIdentifiers("crappy_dockername-2343a", null, ItemDescription.class)
                .orElseThrow();

        assertNotNull(two);
        assertEquals("alpha", two.getGroup());

        ItemDescription three = landscapeDescription.getReadAccess()
                .matchOneByIdentifiers("other_crappy_name-2343a", null, ItemDescription.class)
                .orElseThrow();

        assertNotNull(three);
        assertEquals("beta", three.getGroup());
    }


    @Test
    void resolvesTemplatePlaceholdersInProviders() {

        //when
        relationEndpointResolver.resolve(landscapeDescription);

        //the provider has been resolved using a query instead of naming a service
        ItemDescription providedbyBar = landscapeDescription.getReadAccess()
                .matchOneByIdentifiers("crappy_dockername-78345", null, ItemDescription.class)
                .orElseThrow();
        assertNotNull(providedbyBar);
        assertThat(providedbyBar.getProvidedBy()).isNotEmpty();

        List<RelationDescription> relations = providedbyBar.getRelations().stream()
                .filter(relation -> RelationType.PROVIDER.equals(relation.getType()))
                .collect(Collectors.toUnmodifiableList());
        assertThat(relations).isNotNull().hasSize(1);
        RelationDescription s = relations.iterator().next();
        assertEquals("item://_/_/_/alpha/crappy_dockername-2343a", s.getSource());
    }

    @Test
    void resolvesTemplatePlaceholdersInDataflow() {

        //when
        relationEndpointResolver.resolve(landscapeDescription);

        //then
        ItemDescription hasdataFlow = landscapeDescription.getReadAccess()
                .matchOneByIdentifiers(crappy2343a.getIdentifier(), null, ItemDescription.class)
                .orElseThrow();
        assertNotNull(hasdataFlow);
        Set<RelationDescription> allRelations = hasdataFlow.getRelations();
        assertThat(allRelations).hasSize(1);
        List<RelationDescription> relations = hasdataFlow.getRelations().stream()
                .filter(relation -> RelationType.DATAFLOW.equals(relation.getType()))
                .collect(Collectors.toUnmodifiableList());
        assertThat(relations).hasSize(1);
        RelationDescription next = relations.get(0);
        assertEquals(other2343a.getFullyQualifiedIdentifier().toString(), next.getTarget());
    }

    @Test
    void resolvesMatchersInDataflow() {

        //given
        crappy2343a.getRelations().clear();
        var rel = new RelationDescription();
        rel.setTarget(other2343a.getGroup() + FullyQualifiedIdentifier.SEPARATOR + other2343a.getIdentifier());
        rel.setType(RelationType.DATAFLOW);
        crappy2343a.getRelations().add(rel);

        //when
        relationEndpointResolver.resolve(landscapeDescription);

        //then
        ItemDescription hasdataFlow = landscapeDescription.getReadAccess()
                .matchOneByIdentifiers(crappy2343a.getIdentifier(), null, ItemDescription.class)
                .orElseThrow();
        assertNotNull(hasdataFlow);
        Set<RelationDescription> allRelations = hasdataFlow.getRelations();
        assertThat(allRelations).hasSize(1);
        List<RelationDescription> relations = hasdataFlow.getRelations().stream()
                .filter(relation -> RelationType.DATAFLOW.equals(relation.getType()))
                .collect(Collectors.toUnmodifiableList());
        assertThat(relations).hasSize(1);
        RelationDescription next = relations.get(0);
        assertEquals(other2343a.getFullyQualifiedIdentifier().toString(), next.getTarget());
    }

    @Test
    void prefersSameGroupIProviderIfAmbiguous() {

        //given, new landscape dto
        landscapeDescription = new LandscapeDescription("test");

        var foo = new ItemDescription();
        foo.setIdentifier("foo");
        foo.setGroup("alpha");
        foo.getProvidedBy().add("identifier:bar*");
        landscapeDescription.getWriteAccess().addOrReplaceChild(foo);

        var bar1 = new ItemDescription();
        bar1.setIdentifier("bar");
        bar1.setGroup("alpha");
        landscapeDescription.getWriteAccess().addOrReplaceChild(bar1);

        var bar2 = new ItemDescription();
        bar2.setIdentifier("bar");
        bar2.setGroup("beta");
        landscapeDescription.getWriteAccess().addOrReplaceChild(bar2);
        landscapeDescription.getReadAccess().indexForSearch(Assessment.empty());

        //when
        relationEndpointResolver.resolve(landscapeDescription);

        //then
        List<RelationDescription> relations = foo.getRelations().stream()
                .filter(relation -> RelationType.PROVIDER.equals(relation.getType()))
                .collect(Collectors.toUnmodifiableList());
        assertThat(relations).hasSize(1);

        // should pick "other_crappy_name..." from own group (alpha)
        RelationDescription next = relations.get(0);
        assertEquals(bar1.getFullyQualifiedIdentifier().toString(), next.getSource());
    }

    @Test
    void prefersSameGroupIRelationsIfAmbiguous() {

        //given, new landscape dto
        landscapeDescription = new LandscapeDescription("test");

        var foo = new ItemDescription();
        foo.setIdentifier("foo");
        foo.setGroup("alpha");
        var rel = new RelationDescription();
        rel.setTarget("identifier:bar*");
        rel.setType(RelationType.DATAFLOW);
        foo.getRelations().add(rel);
        landscapeDescription.getWriteAccess().addOrReplaceChild(foo);

        var bar1 = new ItemDescription();
        bar1.setIdentifier("bar");
        bar1.setGroup("alpha");
        landscapeDescription.getWriteAccess().addOrReplaceChild(bar1);

        var bar2 = new ItemDescription();
        bar2.setIdentifier("bar");
        bar2.setGroup("beta");
        landscapeDescription.getWriteAccess().addOrReplaceChild(bar2);
        landscapeDescription.getReadAccess().indexForSearch(Assessment.empty());

        //when
        relationEndpointResolver.resolve(landscapeDescription);

        //then

        List<RelationDescription> relations = foo.getRelations().stream()
                .filter(relation -> RelationType.DATAFLOW.equals(relation.getType()))
                .collect(Collectors.toUnmodifiableList());
        assertThat(relations).hasSize(1);

        // should pick "other_crappy_name..." from own group (alpha)
        RelationDescription next = relations.get(0);
        assertEquals(bar1.getFullyQualifiedIdentifier().toString(), next.getTarget());
    }
}
