package de.bonndan.nivio.model;

import de.bonndan.nivio.GraphTestSupport;
import de.bonndan.nivio.input.ProcessLog;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.search.NullSearchIndex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

class ParentResolverTest {

    private ParentResolver parentResolver;
    private GraphTestSupport graph;

    @BeforeEach
    void setup() {
        graph = new GraphTestSupport();
        parentResolver = new ParentResolver(
                graph.landscape.getReadAccess(),
                graph.landscape.getWriteAccess()
        );
    }

    @Test
    void createParentInstantlyWithItem() {

        String identifier = "foo";

        //when
        Group instantParent = parentResolver.createParentInstantly(identifier, Group.class);

        //then
        assertThat(instantParent).isNotNull();
        assertThat(instantParent.getIdentifier()).isEqualTo(identifier);
        assertThat(instantParent.getParent()).isEqualTo(graph.context);
    }

    @Test
    void createParentInstantlyWithGroup() {

        String identifier = "foo";

        //when
        Context instantParent = parentResolver.createParentInstantly(identifier, Context.class);

        //then
        assertThat(instantParent).isNotNull();
        assertThat(instantParent.getIdentifier()).isEqualTo(identifier);
        assertThat(instantParent.getParent()).isEqualTo(graph.unit);
    }

    @Test
    void createParentInstantlyWithContext() {

        String identifier = "foo";

        //when
        Unit instantParent = parentResolver.createParentInstantly(identifier, Unit.class);

        //then
        assertThat(instantParent).isNotNull();
        assertThat(instantParent.getIdentifier()).isEqualTo(identifier);
        assertThat(instantParent.getParent()).isEqualTo(graph.landscape);
    }

    @Test
    void GroupWouldFallBackToExisting() {

        String identifier = graph.groupA.identifier;

        //when
        Group instantParent = parentResolver.createParentInstantly(identifier, Group.class);

        //then
        assertThat(instantParent).isEqualTo(graph.groupA);
        assertThat(instantParent.getIdentifier()).isEqualTo(identifier);
    }

    @Test
    void createsNewDefaults() {

        var index = new Index<GraphComponent>(new NullSearchIndex());
        var landscape = LandscapeFactory.createForTesting("test", "test").withIndex(index).build();
        landscape.setLog(new ProcessLog(LoggerFactory.getLogger(GraphTestSupport.class), "test"));

        String identifier = "group1";

        //when
        Group instantParent = parentResolver.createParentInstantly(identifier, Group.class);

        //then
        assertThat(instantParent).isNotNull();
        assertThat(instantParent.getParent()).isNotNull();
        assertThat(instantParent.getParent().getParent()).isNotNull();
    }

    @Test
    void getParent() {

        var index = new Index<GraphComponent>(new NullSearchIndex());
        var landscape = LandscapeFactory.createForTesting("test", "test").withIndex(index).build();
        landscape.setLog(new ProcessLog(LoggerFactory.getLogger(GraphTestSupport.class), "test"));

        String identifier = "group1";
        ItemDescription description = new ItemDescription("anItem");
        description.setGroup(identifier);

        //when
        Group instantParent = parentResolver.getParent(description, Group.class);

        //then
        assertThat(instantParent).isNotNull();
        assertThat(instantParent.getParent()).isNotNull();
        assertThat(instantParent.getParent().getParent()).isNotNull();
    }

    @Test
    void getParentWithUndefinedGroup() {

        var index = new Index<GraphComponent>(new NullSearchIndex());
        var landscape = LandscapeFactory.createForTesting("test", "test").withIndex(index).build();
        landscape.setLog(new ProcessLog(LoggerFactory.getLogger(GraphTestSupport.class), "test"));

        ItemDescription description = new ItemDescription("anItem");
        description.setGroup(FullyQualifiedIdentifier.UNDEFINED);

        //when
        Group instantParent = parentResolver.getParent(description, Group.class);

        //then
        assertThat(instantParent).isNotNull();
        assertThat(instantParent.getIdentifier()).isEqualTo(Layer.domain.name());
    }

    @Test
    void getParentWithAmbiguousIdentifier() {

        var index = new Index<GraphComponent>(new NullSearchIndex());
        var landscape = LandscapeFactory.createForTesting("test", "test").withIndex(index).build();
        landscape.setLog(new ProcessLog(LoggerFactory.getLogger(GraphTestSupport.class), "test"));

        var domain = graph.getTestGroup(Layer.domain.name());
        graph.getTestItem(Layer.domain.name(), graph.itemAA.identifier);

        ItemDescription description = new ItemDescription(graph.itemAA.identifier);
        description.setGroup(Layer.domain.name());

        //when
        Group instantParent = parentResolver.getParent(description, Group.class);

        //then
        assertThat(instantParent).isNotNull().isEqualTo(domain);
    }

    @Test
    void getParentWithAmbiguousIdentifierWithParentIdentifier() {

        var index = new Index<GraphComponent>(new NullSearchIndex());
        var landscape = LandscapeFactory.createForTesting("test", "test").withIndex(index).build();
        landscape.setLog(new ProcessLog(LoggerFactory.getLogger(GraphTestSupport.class), "test"));

        var domain = graph.getTestGroup(Layer.domain.name());
        graph.getTestItem(Layer.domain.name(), graph.itemAA.identifier);

        ItemDescription description = new ItemDescription(graph.itemAA.identifier);
        description.setGroup("");

        //when
        Group instantParent = parentResolver.getParent(description, Group.class);

        //then
        assertThat(instantParent).isNotNull().isEqualTo(domain);
    }
}