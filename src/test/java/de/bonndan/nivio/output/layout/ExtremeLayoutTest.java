package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.GraphTestSupport;
import de.bonndan.nivio.model.*;
import de.bonndan.nivio.model.LayoutConfig;
import de.bonndan.nivio.util.RootPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExtremeLayoutTest {

    private static final int MIN_DISTANCE_LIMIT = 50;
    private static final int MAX_DISTANCE_LIMIT = 250;
    private CollisionRegardingForces collisionRegardingForces;
    private OriginalForces originalForces;
    private GraphTestSupport graph;

    @BeforeEach
    void setup() {
        graph = new GraphTestSupport();
        collisionRegardingForces = new CollisionRegardingForces(MIN_DISTANCE_LIMIT, MAX_DISTANCE_LIMIT);
        originalForces = new OriginalForces(2, MAX_DISTANCE_LIMIT, OriginalForces.FORCE_CONSTANT);
    }

    @Test
    void originalSimple() throws IOException {
        testSimple(originalForces, "original");
    }

    @Test
    void newSimple() throws IOException {
        testSimple(collisionRegardingForces, "new");
    }

    @Test
    void originalExtremeFails() {
        assertThatThrownBy(() -> testSingleLargeGroupWithRelations(originalForces, "original")).isInstanceOf(LayoutException.class);
    }

    @Test
    void newExtreme() throws IOException {
        testSingleLargeGroupWithRelations(collisionRegardingForces, "new");
    }

    void testSimple(Forces forces, String filePrefix) throws IOException {

        List<Item> items = new ArrayList<>();
        int i = 0;
        Group group = graph.groupA;
        while (i < 3) {
            Item testItem = graph.getTestItem(group.getIdentifier(), "c" + i);
            items.add(testItem);
            i++;
        }

        // relations
        var descriptionListSize = items.size();
        for (i = 0; i < descriptionListSize; i++) {
            Item item = items.get(i);
            List<Item> targets = new ArrayList<>();
            int j = 0;
                    Item other = items.get(j);
                    if (!other.equals(item)) {
                        targets.add(other);
                    }
            targets.forEach(target -> graph.landscape.getIndexWriteAccess().addOrReplaceRelation(RelationFactory.createForTesting(item, target)));
        }

        var components = SubLayout.getLayoutedComponents(group, new HashSet<>(items));

        var layout = new FastOrganicLayout(components, forces, LayoutConfig.GROUP_LAYOUT_INITIAL_TEMP);
        layout.setDebug(true);

        //when
        try {
            layout.execute();
        } finally {
            layout.getLayoutLogger().traceLocations(new File(RootPath.get() + "/src/test/dump/" + filePrefix + "-simple.svg"));
            layout.getLayoutLogger().dump(new File(RootPath.get() + "/src/test/dump/" + filePrefix + "-simple.txt"));
        }

        //then
        layout.assertMinDistanceIsKept(MIN_DISTANCE_LIMIT);
    }

    void testSingleLargeGroupWithRelations(Forces forces, String filePrefix) throws IOException {

        List<Item> items = new ArrayList<>();
        int i = 0;
        Group group = graph.getTestGroup("test");
        while (i < 20) {
            Item testItem = graph.getTestItem(group.getIdentifier(), "c" + i);
            items.add(testItem);
            i++;
        }

        // relations
        var descriptionListSize = items.size();
        for (i = 0; i < descriptionListSize; i++) {
            Item item = items.get(i);
            List<Item> targets = new ArrayList<>();
            int j = 0;
            while (j < 5) {
                if (j != i) {
                    Item other = items.get(j);
                    if (!other.equals(item))
                        targets.add(other);
                }

                j++;
            }
            targets.forEach(target -> {
                graph.landscape.getIndexWriteAccess().addOrReplaceRelation(RelationFactory.createForTesting(item, target));
            });
        }

        var components = SubLayout.getLayoutedComponents(group, new HashSet<>(items));

        var layout = new FastOrganicLayout(components, forces, LayoutConfig.GROUP_LAYOUT_INITIAL_TEMP);
        layout.setDebug(true);

        //when
        try {
            layout.execute();
        } finally {
            layout.getLayoutLogger().traceLocations(new File(RootPath.get() + "/src/test/dump/" + filePrefix + "-singleLargeGroupWithRelations.svg"));
            layout.getLayoutLogger().dump(new File(RootPath.get() + "/src/test/dump/" + filePrefix + "-singleLargeGroupWithRelations.txt"));
        }

        //then
        layout.assertMinDistanceIsKept(MIN_DISTANCE_LIMIT);
    }
}
