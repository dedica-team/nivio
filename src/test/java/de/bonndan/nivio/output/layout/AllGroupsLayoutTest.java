package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.GraphTestSupport;
import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.LayoutConfig;
import de.bonndan.nivio.model.RelationFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AllGroupsLayoutTest {

    private GraphTestSupport graph;

    @BeforeEach
    void setup() {
        graph = new GraphTestSupport();
    }

    @Test
    void testWithARelation() {

        Item bara = graph.getTestItem("a", "bara");
        Item fooa = graph.getTestItem("a", "fooa");

        Item barb = graph.getTestItem("b", "barb");
        Item foob = graph.getTestItem("b", "foob");

        Item barc = graph.getTestItem("c", "barc");
        Item fooc = graph.getTestItem("c", "fooc");


        Map<URI, SubLayout> map = Map.of(
                graph.groupA.getFullyQualifiedIdentifier(), getSubLayout(graph.groupA, Set.of(bara, fooa)),
                graph.groupB.getFullyQualifiedIdentifier(), getSubLayout(graph.groupB, Set.of(barb, foob)),
                graph.groupC.getFullyQualifiedIdentifier(), getSubLayout(graph.groupC, Set.of(barc, fooc))
        );

        //add some inter-group relations
        graph.landscape.getIndexWriteAccess().addOrReplaceRelation(RelationFactory.createForTesting(bara, barb));
        graph.landscape.getIndexWriteAccess().addOrReplaceRelation(RelationFactory.createForTesting(barb, barc));

        //when
        AllGroupsLayout allGroupsLayout = new AllGroupsLayout(true, graph.landscape.getConfig().getLayoutConfig());

        //then
        LayoutedComponent layoutedLandscape = allGroupsLayout.getRendered(graph.landscape, graph.landscape.getGroups(), map);
        assertNotNull(layoutedLandscape);
        assertEquals(graph.landscape, layoutedLandscape.getComponent());
        assertEquals(3, layoutedLandscape.getChildren().size());

        //assert position is always the same
        LayoutedComponent child0 = layoutedLandscape.getChildren().get(0);
        assertEquals(graph.groupA.getFullyQualifiedIdentifier().toString(), child0.getComponent().getFullyQualifiedIdentifier().toString());
        assertEquals(1240, Math.round(child0.getX()));
        assertEquals(1431, Math.round(child0.getY()));
    }

    private SubLayout getSubLayout(Group group, Set<Item> items) {
        SubLayout subLayout = new SubLayout(true, new LayoutConfig());
        subLayout.render(group, items);
        return subLayout;
    }
}