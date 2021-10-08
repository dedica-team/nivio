package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.ItemFactory;
import de.bonndan.nivio.model.RelationFactory;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.*;

class FastOrganicLayoutTest {

    int radius = 500;
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
        Item testItemC = ItemFactory.getTestItem("test", "c");


        a = new LayoutedComponent(testItemA, List.of(testItemB, testItemC));
        a.setX(100);
        a.setY(100);
        a.setHeight(radius);
        layoutedComponents.add(a);

        b = new LayoutedComponent(testItemB, List.of(testItemA, testItemC));
        b.setX(101);
        b.setY(101);
        b.setHeight(radius);
        layoutedComponents.add(b);

        c = new LayoutedComponent(testItemC, List.of(testItemA, testItemB));
        c.setX(102);
        c.setY(102);
        c.setHeight(radius);
        layoutedComponents.add(c);

        layout = new FastOrganicLayout(layoutedComponents, SubLayout.MIN_DISTANCE_LIMIT, SubLayout.MAX_DISTANCE_LIMIT, SubLayout.INITIAL_TEMP, null);
        layout.setDebug(true);
    }

    @Test
    void preventsCloseItems() {

        //when
        layout.execute();

        //then
        assertAboveMinDistance(a, b);
        assertAboveMinDistance(b, c);
        assertAboveMinDistance(a, c);
    }

    @Test
    void calcRepulsionDisplacement() {

        //when
        layout.setup();

        //create overlap
        layout.centerLocations[0][0] = 0;
        layout.centerLocations[0][1] = 0;

        layout.centerLocations[1][0] = 200;
        layout.centerLocations[1][1] = 0;

        layout.centerLocations[2][0] = 10000000;
        layout.centerLocations[2][1] = 10000000;

        layout.radius[0] = 50;
        layout.radius[1] = 50;
        layout.radius[2] = 50;

        layout.calcPositions(); //recalc distances

        //when
        Point2D.Double repulsionDisplacement = layout.getRepulsionDisplacement(0, 1);

        //then
        assertThat(repulsionDisplacement.x).isEqualTo(-75, Offset.offset(1D));
        assertThat(repulsionDisplacement.y).isEqualTo(0, Offset.offset(1D));
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

        layout.centerLocations[2][0] = 10000000;
        layout.centerLocations[2][1] = 10000000;

        layout.radius[0] = 50;
        layout.radius[1] = 50;
        layout.radius[2] = 50;

        layout.calcPositions(); //recalc distances

        //when
        Point2D.Double repulsionDisplacement = layout.getRepulsionDisplacement(0, 1);

        //then
        assertThat(repulsionDisplacement.x).isEqualTo(0);
        assertThat(repulsionDisplacement.y).isEqualTo(0);
    }

    @Test
    void calcRepulsionDisplacementOverlap() {

        //when
        layout.setup();

        //create overlap
        layout.centerLocations[0][0] = 0;
        layout.centerLocations[0][1] = 0;

        layout.centerLocations[1][0] = 20;
        layout.centerLocations[1][1] = 0;

        layout.centerLocations[2][0] = 10000000;
        layout.centerLocations[2][1] = 10000000;

        layout.radius[0] = 50;
        layout.radius[1] = 50;
        layout.radius[2] = 50;

        layout.calcPositions(); //recalc distances

        //when
        Point2D.Double repulsionDisplacement = layout.getRepulsionDisplacement(0, 1);

        //then
        assertThat(repulsionDisplacement.x).isLessThan(0).isEqualTo(-10, Offset.offset(1D));
        assertThat(repulsionDisplacement.y).isEqualTo(0, Offset.offset(1D));
    }

    @Test
    void calcRepulsion() {

        //when
        layout.setup();

        //create overlap
        layout.centerLocations[0][0] = 0;
        layout.centerLocations[0][1] = 0;

        layout.centerLocations[1][0] = 100;
        layout.centerLocations[1][1] = 200;

        layout.centerLocations[2][0] = 10000000;
        layout.centerLocations[2][1] = 10000000;

        layout.radius[0] = 50;
        layout.radius[1] = 50;
        layout.radius[2] = 50;

        layout.calcPositions(); //recalc distances

        //when
        layout.calcRepulsion();

        //then
        assertThat(layout.disp[0][0]).isLessThan(0).isEqualTo(-25, Offset.offset(1D));
        assertThat(layout.disp[0][1]).isLessThan(0).isEqualTo(-51, Offset.offset(1D));
        assertThat(layout.disp[1][0]).isGreaterThan(0).isEqualTo(25, Offset.offset(1D));
        assertThat(layout.disp[1][1]).isEqualTo(51, Offset.offset(1D));
    }

    @Test
    void calcRepulsionOnOverlap() {

        //when
        layout.setup();

        //create overlap
        layout.centerLocations[0][0] = 0;
        layout.centerLocations[0][1] = 0;

        layout.centerLocations[1][0] = 10;
        layout.centerLocations[1][1] = 50;

        layout.centerLocations[2][0] = 10000000;
        layout.centerLocations[2][1] = 10000000;

        layout.radius[0] = 50;
        layout.radius[1] = 50;
        layout.radius[2] = 50;

        layout.calcPositions(); //recalc

        //when
        layout.calcRepulsion();

        //then
        assertThat(layout.disp[0][0]).isLessThan(0).isEqualTo(-10, Offset.offset(1D));
        assertThat(layout.disp[0][1]).isLessThan(0).isEqualTo(-51, Offset.offset(1D));
        assertThat(layout.disp[1][0]).isGreaterThan(0).isEqualTo(10, Offset.offset(1D));
        assertThat(layout.disp[1][1]).isEqualTo(51, Offset.offset(1D));
    }

    @Test
    void reduceTempRegularly() {
        //given
        layout.temperature = 1000;

        //when
        layout.reduceTemperature();

        //then
        assertThat(layout.temperature).isEqualTo(800);
    }

    @Test
    void reduceTempShortfallIsSlower() {
        //given
        layout.temperature = 1000;
        layout.minDistanceShortfall = true;

        //when
        layout.reduceTemperature();

        //then
        assertThat(layout.temperature).isEqualTo(970);
    }

    @Test
    void calcPositions() {
        //given
        layout.setup();

        layout.centerLocations[0][0] = 0;
        layout.centerLocations[0][1] = 0;
        layout.radius[0] = 50;

        layout.centerLocations[1][0] = 100;
        layout.centerLocations[1][1] = 50;
        layout.radius[1] = 50;

        layout.centerLocations[2][0] = -200;
        layout.centerLocations[2][1] = 200;
        layout.radius[2] = 50;

        //when
        layout.calcPositions();

        //then
        assertThat(layout.distances[0][1]).isEqualTo(11, Offset.offset(1D));
        assertThat(layout.distances[1][0]).isEqualTo(11, Offset.offset(1D));

        assertThat(layout.distances[0][2]).isEqualTo(182, Offset.offset(1D));
        assertThat(layout.distances[2][0]).isEqualTo(182, Offset.offset(1D));
    }

    @Test
    void getDistance() {
        //given
        layout.setup();

        layout.centerLocations[0][0] = 0;
        layout.centerLocations[0][1] = 0;
        layout.radius[0] = 50;

        layout.centerLocations[1][0] = 100;
        layout.centerLocations[1][1] = 50;
        layout.radius[1] = 50;

        layout.centerLocations[2][0] = -200;
        layout.centerLocations[2][1] = 200;
        layout.radius[2] = 50;

        //then
        assertThat(layout.getDimDistanceBetweenCenters(0, 1, 0)).isEqualTo(-100);
        assertThat(layout.getDimDistanceBetweenCenters(1, 0, 0)).isEqualTo(100); //revert

        assertThat(layout.getDimDistanceBetweenCenters(0, 1, 1)).isEqualTo(-50);
        assertThat(layout.getDimDistanceBetweenCenters(1, 0, 1)).isEqualTo(50); //revert

        assertThat(layout.getDimDistanceBetweenCenters(0, 2, 0)).isEqualTo(200);
        assertThat(layout.getDimDistanceBetweenCenters(0, 2, 1)).isEqualTo(-200);
    }

    @Test
    void getDistanceWithOverlaps() {
        //given
        layout.setup();

        layout.centerLocations[0][0] = 0;
        layout.centerLocations[0][1] = 0;
        layout.radius[0] = 50;

        layout.centerLocations[1][0] = 1;
        layout.centerLocations[1][1] = -50;
        layout.radius[1] = 50;

        layout.centerLocations[2][0] = -99;
        layout.centerLocations[2][1] = -500;
        layout.radius[2] = 50;

        //then
        assertThat(layout.getDimDistanceBetweenCenters(0, 1, 0)).isEqualTo(-1);
        assertThat(layout.getDimDistanceBetweenCenters(0, 1, 1)).isEqualTo(50);

        assertThat(layout.getDimDistanceBetweenCenters(0, 2, 0)).isEqualTo(99);
        assertThat(layout.getDimDistanceBetweenCenters(0, 2, 1)).isEqualTo(500);
    }

    @Test
    void calcAttractionDisplacement() {
        //given
        layout.setup();

        layout.centerLocations[0][0] = 0;
        layout.centerLocations[0][1] = 0;
        layout.radius[0] = 50;

        layout.centerLocations[1][0] = 1000;
        layout.centerLocations[1][1] = 50;
        layout.radius[1] = 50;

        layout.calcPositions(); //recalc

        //when
        var displacement = layout.getAttractionDisplacement(0, 1);

        //then
        //displacement is subtracted from i and added to j
        assertThat(displacement.x).isGreaterThan(0); //shift in x direction
        assertThat(displacement.y).isGreaterThan(0); //shift in x direction

        //when
        var reverse = layout.getAttractionDisplacement(1, 0);

        //then
        assertThat(reverse.x).isLessThan(0); //shift in x direction
        assertThat(reverse.y).isLessThan(0); //shift in x direction
    }

    @Test
    void calcAttractionOfTwo() {
        //given
        Item testItemA = ItemFactory.getTestItem("test", "a");
        Item testItemB = ItemFactory.getTestItem("test", "b");

        layoutedComponents = new ArrayList<>();
        a = new LayoutedComponent(testItemA, List.of(testItemB));
        a.setX(100);
        a.setY(100);
        a.setHeight(radius);
        layoutedComponents.add(a);

        b = new LayoutedComponent(testItemB, List.of(testItemA));
        b.setX(101);
        b.setY(101);
        b.setHeight(radius);
        layoutedComponents.add(b);

        layout = new FastOrganicLayout(layoutedComponents, SubLayout.MIN_DISTANCE_LIMIT, SubLayout.MAX_DISTANCE_LIMIT, SubLayout.INITIAL_TEMP, null);
        layout.setup();

        layout.centerLocations[0][0] = 0;
        layout.centerLocations[0][1] = 0;
        layout.radius[0] = 50;

        layout.centerLocations[1][0] = 1000;
        layout.centerLocations[1][1] = 50;
        layout.radius[1] = 50;

        layout.calcPositions();

        var distance1 = layout.distances[0][1];

        //when
        layout.calcStrongAttraction();
        layout.calcPositions();

        //then
        var distance2 = layout.distances[0][1];
        assertThat(distance2).isLessThan(distance1).isGreaterThan(layout.minDistanceLimit);
    }

    @Test
    void calcWeakAttraction() {
        //given
        Item testItemA = ItemFactory.getTestItem("test", "a");
        Item testItemB = ItemFactory.getTestItem("test", "b");

        layoutedComponents = new ArrayList<>();
        a = new LayoutedComponent(testItemA, List.of(testItemB));
        layoutedComponents.add(a);

        b = new LayoutedComponent(testItemB, List.of(testItemA));
        layoutedComponents.add(b);

        layout = new FastOrganicLayout(layoutedComponents, SubLayout.MIN_DISTANCE_LIMIT, SubLayout.MAX_DISTANCE_LIMIT, SubLayout.INITIAL_TEMP, null);
        layout.setup();

        layout.centerLocations[0][0] = 0;
        layout.centerLocations[0][1] = 0;
        layout.radius[0] = 50;

        layout.centerLocations[1][0] = SubLayout.MAX_DISTANCE_LIMIT * 3;
        layout.centerLocations[1][1] = SubLayout.MAX_DISTANCE_LIMIT * 3;
        layout.radius[1] = 50;

        layout.calcPositions(); //recalc after setting center locations

        var distance1 = layout.distances[0][1];

        //when
        layout.calcWeakAttraction();
        layout.calcPositions();

        //then
        var distance2 = layout.distances[0][1];
        assertThat(distance2).isLessThan(distance1).isGreaterThan(layout.maxDistanceLimit);
    }

    @Test
    void weakAttractionNotEffective() {
        //given
        Item testItemA = ItemFactory.getTestItem("test", "a");
        Item testItemB = ItemFactory.getTestItem("test", "b");

        layoutedComponents = new ArrayList<>();
        a = new LayoutedComponent(testItemA, List.of(testItemB));
        layoutedComponents.add(a);

        b = new LayoutedComponent(testItemB, List.of(testItemA));
        layoutedComponents.add(b);

        layout = new FastOrganicLayout(layoutedComponents, SubLayout.MIN_DISTANCE_LIMIT, SubLayout.MAX_DISTANCE_LIMIT, SubLayout.INITIAL_TEMP, null);
        layout.setup();

        var distance1 = layout.distances[0][1];

        //when
        layout.calcWeakAttraction();
        layout.calcPositions();

        //then
        var distance2 = layout.distances[0][1];
        assertThat(distance2).isEqualTo(distance1);
    }

    @Test
    void hasDistanceAfterSetup() {

        //given
        layout.setup();

        //when
        double xDist = layout.getDimDistanceBetweenCenters(0, 1, 0);
        double yDist = layout.getDimDistanceBetweenCenters(0, 1, 1);

        //then
        assertThat(Math.abs(xDist)).isGreaterThan(0);
        assertThat(Math.abs(yDist)).isGreaterThan(0);
    }

    @Test
    void assertMinDistanceOK() {
        //given
        layout.setup();

        //when
        assertThatCode(() -> layout.assertMinDistanceIsKept()).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("prevent that dense placement is prevented")
    void singleLargeGroupWithRelations() {

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

        List<LayoutedComponent> components = SubLayout.getComponents(group, new HashSet<>(items));
        layout = new FastOrganicLayout(components, SubLayout.MIN_DISTANCE_LIMIT, SubLayout.MAX_DISTANCE_LIMIT, SubLayout.INITIAL_TEMP, null);

        //when
        layout.execute();

        //then
        layout.assertMinDistanceIsKept();
    }

    @Test
    void completeExecution() {
        //given
        layout.setup();

        //when
        layout.execute();

        //then
        assertThat(layout.getDistanceBetween(0,1)).isLessThan(500D);
        assertThat(layout.getDistanceBetween(1,2)).isLessThan(500D);
        assertThat(layout.getDistanceBetween(2,0)).isLessThan(500D);
    }

    void assertAboveMinDistance(LayoutedComponent a, LayoutedComponent b) {
        var xDelta = a.x - b.x;
        var yDelta = a.y - b.y;
        var dist = Math.sqrt((xDelta * xDelta) + (yDelta * yDelta)) - radius - radius; //two times the radius

        assertThat(Math.abs(dist))
                .isGreaterThan((long) SubLayout.MIN_DISTANCE_LIMIT);
    }

}