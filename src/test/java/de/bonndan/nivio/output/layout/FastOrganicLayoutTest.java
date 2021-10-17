package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.model.*;
import de.bonndan.nivio.util.RootPath;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.*;

class FastOrganicLayoutTest {

    private ArrayList<LayoutedComponent> layoutedComponents;
    private LayoutedComponent a;
    private LayoutedComponent b;
    private LayoutedComponent c;
    private FastOrganicLayout layout;

    @BeforeEach
    void setup() {
        layoutedComponents = new ArrayList<>();
        Item testItemA = ItemFactory.getTestItem("test", "a");
        Item testItemB = ItemFactory.getTestItem("test", "b");


        a = new LayoutedComponent(testItemA, List.of(testItemB));
        a.setX(0);
        a.setY(0);
        a.setWidth(100);
        a.setHeight(100);
        layoutedComponents.add(a);

        b = new LayoutedComponent(testItemB, List.of(testItemA));
        b.setX(0);
        b.setY(200);
        b.setWidth(100);
        b.setHeight(100);
        layoutedComponents.add(b);

        layout = new FastOrganicLayout(layoutedComponents, SubLayout.FORCE_CONSTANT, SubLayout.MIN_DISTANCE_LIMIT, SubLayout.MAX_DISTANCE_LIMIT, SubLayout.INITIAL_TEMP, null);
        layout.setDebug(true);
    }

    @Test
    void preventsCloseItems() {

        setupThree();

        //when
        layout.execute();

        //then
        var xDelta1 = a.x - b.x;
        var yDelta1 = a.y - b.y;
        var dist1 = Math.sqrt((xDelta1 * xDelta1) + (yDelta1 * yDelta1)) - 50 - 50; //two times the radius

        assertThat(Math.abs(dist1))
                .isGreaterThan((long) SubLayout.MIN_DISTANCE_LIMIT);

        var xDelta = b.x - c.x;
        var yDelta = b.y - c.y;
        var dist = Math.sqrt((xDelta * xDelta) + (yDelta * yDelta)) - 50 - 50; //two times the radius

        assertThat(Math.abs(dist))
                .isGreaterThan((long) SubLayout.MIN_DISTANCE_LIMIT);
    }

    @Test
    void calcRepulsionDisplacement() {

        //when
        layout.setup();

        layout.centerLocations[0][0] = 0;
        layout.centerLocations[0][1] = 0;

        layout.centerLocations[1][0] = 200;
        layout.centerLocations[1][1] = 0;

        layout.calcPositions(); //recalc distances

        //when
        Point2D.Double repulsionDisplacement = layout.getRepulsionDisplacement(0, 1);

        //then
        assertThat(repulsionDisplacement.x).isEqualTo(0, Offset.offset(1D));
        assertThat(repulsionDisplacement.y).isEqualTo(-125, Offset.offset(1D));
    }

    @Test
    void calcRepulsionAlongVector() {

        //when
        layout.setup();

        layout.centerLocations[0][0] = 0;
        layout.centerLocations[0][1] = 0;

        layout.centerLocations[1][0] = 100;
        layout.centerLocations[1][1] = 5;

        layout.calcPositions(); //recalc distances

        //when
        Point2D.Double repulsionDisplacement = layout.getRepulsionDisplacement(0, 1);

        //then
        assertThat(repulsionDisplacement.x/repulsionDisplacement.y).isEqualTo(5D/100D, Offset.offset(0.1D));
    }


    @Test
    @DisplayName("Repulsion is greater on closer distances")
    void calcRepulsionDisplacementComp() {

        //when
        layout.setup();

        //create overlap
        layout.centerLocations[0][0] = 0;
        layout.centerLocations[0][1] = 0;

        layout.centerLocations[1][0] = 200;
        layout.centerLocations[1][1] = 100;

        layout.calcPositions(); //recalc distances

        //when
        Point2D.Double repulsionDisplacement = layout.getRepulsionDisplacement(0, 1);

        //then
        assertThat(Math.abs(repulsionDisplacement.y)).isGreaterThan(Math.abs(repulsionDisplacement.x));
    }


    @Test
    void calcRepulsionDisplacementTooFar() {

        //when
        layout.setup();

        //create overlap
        layout.centerLocations[0][0] = 0;
        layout.centerLocations[0][1] = 0;

        layout.centerLocations[1][0] = 2000;
        layout.centerLocations[1][1] = 0;

        layout.calcPositions(); //recalc distances

        //when
        Point2D.Double repulsionDisplacement = layout.getRepulsionDisplacement(0, 1);

        //then
        assertThat(repulsionDisplacement.x).isEqualTo(0);
        assertThat(repulsionDisplacement.y).isEqualTo(0);
    }

    @Test
    @DisplayName("Repulsion is applied to both nodes along vector")
    void calcRepulsion() {

        //when
        layout.setup();

        //create overlap
        layout.centerLocations[0][0] = 0;
        layout.centerLocations[0][1] = 0;

        layout.centerLocations[1][0] = 100;
        layout.centerLocations[1][1] = 200;

        layout.calcPositions(); //recalc distances

        //when
        layout.calcRepulsion();

        //then
        double dispX01 = layout.dispX[0];
        double dispY01 = layout.dispY[0];
        assertThat(dispX01).isLessThan(0);

        assertThat(dispX01/dispY01).isEqualTo(200/100, Offset.offset(1D));

        //greater distance means less (absolute) displacement
        assertThat(dispY01).isLessThan(0).isGreaterThan(dispX01);

        double dispX10 = layout.dispX[1];
        double dispY11 = layout.dispY[1];
        assertThat(dispX10).isGreaterThan(0);
        assertThat(dispX10/dispY11).isEqualTo(200/100, Offset.offset(1D));
    }

    @Test
    void calcRepulsionOnOverlap() {

        //when
        layout.setup();

        //create overlap
        layout.centerLocations[0][0] = 0;
        layout.centerLocations[0][1] = 0;

        layout.centerLocations[1][0] = 10;
        layout.centerLocations[1][1] = 200;

        layout.calcPositions(); //recalc

        //when
        layout.calcRepulsion();

        //then
        double overlapRepulsion = layout.dispX[0];
        double distanceRepulsion = layout.dispY[0];
        assertThat(overlapRepulsion).isEqualTo(-124, Offset.offset(1D));
        assertThat(distanceRepulsion)
                .isLessThan(0)
                .isGreaterThan(overlapRepulsion)
                .isEqualTo(-6, Offset.offset(1D));
    }

    @Test
    void reduceTempRegularly() {
        //given
        layout.setup();
        layout.temperature = 1000;

        //when
        layout.reduceTemperature();

        //then
        assertThat(layout.temperature).isEqualTo(900);
    }

    @Test
    void calcPositionsChangesLocation() {
        //given
        layout.setup();

        layout.centerLocations[0][0] = 0;
        layout.centerLocations[0][1] = 0;

        layout.centerLocations[1][0] = 100;
        layout.centerLocations[1][1] = 50;


        //when
        layout.calcPositions();

        //then
        assertThat(layout.distances[0][1]).isEqualTo(11, Offset.offset(1D));
        assertThat(layout.distances[1][0]).isEqualTo(11, Offset.offset(1D));
    }

    @Test
    void calcPositionsWithDisplacement() {
        //given
        layout.setup();

        layout.centerLocations[0][0] = 0;
        layout.centerLocations[0][1] = 0;

        layout.centerLocations[1][0] = 1000;
        layout.centerLocations[1][1] = 500;

        layout.dispX[0] = 100;
        layout.dispY[0] = 100;


        //when
        layout.calcPositions();

        //then
        assertThat(layout.distances[0][1]).isEqualTo(884, Offset.offset(1D));
        assertThat(layout.distances[1][0]).isEqualTo(884, Offset.offset(1D));
    }

    @Test
    @DisplayName("positioning avoids collisions")
    void calcPositionsAvoidsCollision() {
        //given
        layout.setup();

        layout.centerLocations[0][0] = 0;
        layout.centerLocations[0][1] = 0;

        layout.centerLocations[1][0] = 100;
        layout.centerLocations[1][1] = 100;


        layout.radius[0] = 25;
        layout.radius[1] = 25;

        layout.calcPositions(); //recalc

        layout.dispX[0] = 100;
        layout.dispY[0] = 100;


        //when
        layout.calcPositions();

        //then
        assertThat(layout.distances[0][1]).isGreaterThan(layout.minDistanceLimit);
        assertThat(layout.distances[1][0]).isGreaterThan(layout.minDistanceLimit);
    }

    @Test
    @DisplayName("difficult positioning avoids collisions")
    void doesNotMoveIfCollides() {
        //given
        layout.setup();

        layout.centerLocations[0][0] = 0;
        layout.centerLocations[0][1] = 0;

        layout.centerLocations[1][0] = 100;
        layout.centerLocations[1][1] = 50;

        layout.radius[0] = 50;
        layout.radius[1] = 50;

        layout.calcPositions();

        layout.dispX[0] = 100;
        layout.dispY[0] = 100;

        //when
        layout.calcPositions();

        //then
        assertThat(layout.centerLocations[0][0]).isEqualTo(0);
        assertThat(layout.centerLocations[0][1]).isEqualTo(0);
        assertThat(layout.centerLocations[1][0]).isEqualTo(100);
        assertThat(layout.centerLocations[1][1]).isEqualTo(50);
    }

    @Test
    @DisplayName("can move away from overlap")
    void canMoveAway() {
        //given
        layout.setup();

        layout.centerLocations[0][0] = 0;
        layout.centerLocations[0][1] = 0;

        layout.centerLocations[1][0] = -20;
        layout.centerLocations[1][1] = -20;

        layout.radius[0] = 50;
        layout.radius[1] = 50;

        layout.calcPositions();

        layout.dispX[0] = 200;
        layout.dispY[0] = 200;

        //when
        layout.calcPositions();

        //then
        assertThat(layout.centerLocations[0][0]).isEqualTo(200);
        assertThat(layout.centerLocations[0][1]).isEqualTo(200);
    }

    /*
    @Test
    void getDistanceWithOverlapsAndDirection() {
        //given
        layout.setup();

        layout.centerLocations[0][0] = 0;
        layout.centerLocations[0][1] = 0;

        layout.centerLocations[1][0] = 1;
        layout.centerLocations[1][1] = -50;

        //then
        assertThat(layout.getDimDistanceBetweenCenters(0, 1, 0)).isEqualTo(-1);
        assertThat(layout.getDimDistanceBetweenCenters(0, 1, 1)).isEqualTo(50);
    }

    @Test
    @DisplayName("Attraction moves nodes closer along vector")
    void calcAttractionDisplacement() {
        //given
        layout.setup();

        layout.centerLocations[0][0] = 0;
        layout.centerLocations[0][1] = 0;

        layout.centerLocations[1][0] = 400;
        layout.centerLocations[1][1] = 200;

        layout.calcPositions(); //recalc

        //when
        var displacement = layout.getAttractionDisplacement(0, 1);

        //then
        //displacement is subtracted from i and added to j
        assertThat(displacement.x).isGreaterThan(0).isEqualTo(2000); //shift in x direction
        assertThat(displacement.y).isGreaterThan(0).isLessThan(displacement.x); //shift in y direction

        assertThat(displacement.x/displacement.y).isEqualTo(400/200);

        //when
        var reverse = layout.getAttractionDisplacement(1, 0);

        //then
        assertThat(reverse.x).isLessThan(0); //shift in x direction
        assertThat(reverse.y).isLessThan(0); //shift in x direction
    }

     */


    @Test
    void assertMinDistanceOK() {
        //given
        layout.setup();

        //when
        assertThatCode(() -> layout.assertMinDistanceIsKept()).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("prevent dense placement in extreme layouts")
    void singleLargeGroupWithRelations() throws IOException {

        layoutedComponents = new ArrayList<>();
        List<Item> items = new ArrayList<>();
        int i = 0;
        Group group = new Group("group0", "test");
        while (i < 40) {
            Item testItem = ItemFactory.getTestItem("test", "c" + i);
            items.add(testItem);
            i++;
        }

        Random r = new Random();
        int low = 0;
        int high = items.size() - 1;

        // relations
        var descriptionListSize = items.size();
        for (i = 0; i < descriptionListSize; i++) {
            Item item = items.get(i);
            List<Item> targets = new ArrayList<>();
            int j = 0;
            while (j < 5) {
                int rand = r.nextInt(high - low) + low;
                if (rand == i)
                    continue;
                Item other = items.get(j);
                if (!other.equals(item))
                    targets.add(other);
                j++;
            }
            targets.forEach(target -> {
                item.addOrReplace(RelationFactory.createForTesting(item, target));
            });
        }

        var components = SubLayout.getLayoutedComponents(group, new HashSet<>(items));

        layout = new FastOrganicLayout(components, SubLayout.FORCE_CONSTANT, SubLayout.MIN_DISTANCE_LIMIT, SubLayout.MAX_DISTANCE_LIMIT, SubLayout.INITIAL_TEMP, null);
        layout.setDebug(true);

        //when
        try {
            layout.execute();
        } finally {
            layout.getLayoutLogger().traceLocations(new File(RootPath.get() + "/src/test/dump/singleLargeGroupWithRelations.svg"));
            layout.getLayoutLogger().dump(new File(RootPath.get() + "/src/test/dump/singleLargeGroupWithRelations.txt"));
        }

        //then
        layout.assertMinDistanceIsKept();
    }

    /*
    @Test
    void completeExecutionWithThree() throws IOException {
        //given
        setupThree();
        layout.setup();

        //when
        try {
            layout.execute();
        } finally {
            layout.getLayoutLogger().traceLocations(new File(RootPath.get() + "/src/test/dump/three.svg"));
            layout.getLayoutLogger().dump(new File(RootPath.get() + "/src/test/dump/three.txt"));
        }

        //then
        assertThat(layout.getAbsDistanceBetween(0, 1)).isLessThan(600D);
        assertThat(layout.getAbsDistanceBetween(1, 2)).isLessThan(500D);
    }

     */

    void setupThree() {
        layoutedComponents = new ArrayList<>();
        Item testItemA = ItemFactory.getTestItem("test", "a");
        Item testItemB = ItemFactory.getTestItem("test", "b");
        Item testItemC = ItemFactory.getTestItem("test", "c");


        a = new LayoutedComponent(testItemA, List.of(testItemB, testItemC));
        a.setX(100);
        a.setY(100);
        a.setHeight(500);
        layoutedComponents.add(a);

        b = new LayoutedComponent(testItemB, List.of(testItemA, testItemC));
        b.setX(101);
        b.setY(101);
        b.setHeight(500);
        layoutedComponents.add(b);

        c = new LayoutedComponent(testItemC, List.of(testItemA, testItemB));
        c.setX(102);
        c.setY(102);
        c.setHeight(500);
        layoutedComponents.add(c);

        layout = new FastOrganicLayout(layoutedComponents, SubLayout.FORCE_CONSTANT, SubLayout.MIN_DISTANCE_LIMIT, SubLayout.MAX_DISTANCE_LIMIT, SubLayout.INITIAL_TEMP, null);
        layout.setDebug(true);
    }
}