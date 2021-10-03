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
    private FastOrganicLayout fastOrganicLayout;

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

        fastOrganicLayout = new FastOrganicLayout(layoutedComponents, SubLayout.FORCE_CONSTANT, SubLayout.MIN_DISTANCE_LIMIT, SubLayout.MAX_DISTANCE_LIMIT, SubLayout.INITIAL_TEMP, null);

    }

    @Test
    void preventsCloseItems() {

        //when
        fastOrganicLayout.execute();

        //then
        assertAboveMinDistance(a, b);
        assertAboveMinDistance(b, c);
        assertAboveMinDistance(a, c);
    }


    @Test
    void calcRepulsionOnOverlap() {

        //when
        fastOrganicLayout.setup();

        //create overlap
        fastOrganicLayout.centerLocations[0][0] = 0;
        fastOrganicLayout.centerLocations[0][1] = 0;

        fastOrganicLayout.centerLocations[1][0] = 10;
        fastOrganicLayout.centerLocations[1][1] = 10;

        fastOrganicLayout.centerLocations[2][0] = 10000000;
        fastOrganicLayout.centerLocations[2][1] = 10000000;

        fastOrganicLayout.radius[0] = 50;
        fastOrganicLayout.radius[1] = 50;
        fastOrganicLayout.radius[2] = 50;


        //when
        fastOrganicLayout.calcRepulsion();

        //then
        assertThat(fastOrganicLayout.disp[1][0]).isEqualTo(-275D, Offset.offset(1D));
    }

    @Test
    void reduceTempRegularly() {
        //given
        fastOrganicLayout.temperature = 1000;

        //when
        fastOrganicLayout.reduceTemperature();

        //then
        assertThat(fastOrganicLayout.temperature).isEqualTo(800);
    }

    @Test
    void reduceTempShortfallIsSlower() {
        //given
        fastOrganicLayout.temperature = 1000;
        fastOrganicLayout.minDistanceShortfall = true;

        //when
        fastOrganicLayout.reduceTemperature();

        //then
        assertThat(fastOrganicLayout.temperature).isEqualTo(970);
    }

    @Test
    void getDistance() {
        //given
        fastOrganicLayout.setup();

        fastOrganicLayout.centerLocations[0][0] = 0;
        fastOrganicLayout.centerLocations[0][1] = 0;
        fastOrganicLayout.radius[0] = 50;

        fastOrganicLayout.centerLocations[1][0] = 100;
        fastOrganicLayout.centerLocations[1][1] = 50;
        fastOrganicLayout.radius[1] = 50;

        fastOrganicLayout.centerLocations[2][0] = -200;
        fastOrganicLayout.centerLocations[2][1] = 200;
        fastOrganicLayout.radius[2] = 50;

        //then
        assertThat(fastOrganicLayout.getDimDistanceBetween(0, 1, 0)).isEqualTo(0);
        assertThat(fastOrganicLayout.getDimDistanceBetween(1, 0, 0)).isEqualTo(0); //revert

        assertThat(fastOrganicLayout.getDimDistanceBetween(0, 1, 1)).isEqualTo(50);
        assertThat(fastOrganicLayout.getDimDistanceBetween(1, 0, 1)).isEqualTo(-50); //revert

        assertThat(fastOrganicLayout.getDimDistanceBetween(0, 2, 0)).isEqualTo(100);
        assertThat(fastOrganicLayout.getDimDistanceBetween(0, 2, 1)).isEqualTo(-100);
    }

    @Test
    void getDistanceWithOverlaps() {
        //given
        fastOrganicLayout.setup();

        fastOrganicLayout.centerLocations[0][0] = 0;
        fastOrganicLayout.centerLocations[0][1] = 0;
        fastOrganicLayout.radius[0] = 50;

        fastOrganicLayout.centerLocations[1][0] = 1;
        fastOrganicLayout.centerLocations[1][1] = -50;
        fastOrganicLayout.radius[1] = 50;

        fastOrganicLayout.centerLocations[2][0] = -99;
        fastOrganicLayout.centerLocations[2][1] = -500;
        fastOrganicLayout.radius[2] = 50;

        //then
        assertThat(fastOrganicLayout.getDimDistanceBetween(0, 1, 0)).isEqualTo(99);
        assertThat(fastOrganicLayout.getDimDistanceBetween(0, 1, 1)).isEqualTo(-50);

        assertThat(fastOrganicLayout.getDimDistanceBetween(0, 2, 0)).isEqualTo(-1);
        assertThat(fastOrganicLayout.getDimDistanceBetween(0, 2, 1)).isEqualTo(400);
    }

    @Test
    void calcAttractionDisplacement() {
        //given
        fastOrganicLayout.setup();

        fastOrganicLayout.centerLocations[0][0] = 0;
        fastOrganicLayout.centerLocations[0][1] = 0;
        fastOrganicLayout.radius[0] = 50;

        fastOrganicLayout.centerLocations[1][0] = 1000;
        fastOrganicLayout.centerLocations[1][1] = 50;
        fastOrganicLayout.radius[1] = 50;

        var xDist = fastOrganicLayout.getDimDistanceBetween(0, 1, 0);
        var yDist = fastOrganicLayout.getDimDistanceBetween(0, 1, 1);

        //when
        Point2D.Double attractionDisplacement = fastOrganicLayout.getAttractionDisplacement(0, 1);

        //then
        boolean xOutsideRadius = attractionDisplacement.x < xDist - fastOrganicLayout.minDistanceLimit || attractionDisplacement.x > xDist + fastOrganicLayout.minDistanceLimit;
        assertThat(xOutsideRadius).isTrue();
        boolean yOutsideRadius = attractionDisplacement.y < yDist - fastOrganicLayout.minDistanceLimit || attractionDisplacement.y > yDist + fastOrganicLayout.minDistanceLimit;
        assertThat(yOutsideRadius).isTrue();
    }

    @Test
    void calcRepulsionDisplacementWithOverlap() {
        //given
        fastOrganicLayout.setup();

        fastOrganicLayout.centerLocations[0][0] = 0;
        fastOrganicLayout.centerLocations[0][1] = 0;
        fastOrganicLayout.radius[0] = 50;

        fastOrganicLayout.centerLocations[1][0] = -50;
        fastOrganicLayout.centerLocations[1][1] = 50;
        fastOrganicLayout.radius[1] = 50;

        //when
        Point2D.Double repulsionDisplacement = fastOrganicLayout.getRepulsionDisplacement(0, 1);

        //then
        assertThat(repulsionDisplacement.x).isEqualTo(-1311, Offset.offset(1D));
        assertThat(repulsionDisplacement.y).isEqualTo(1311, Offset.offset(1D));
    }

    @Test
    void hasXDistanceAfterSetup() {

        //given
        fastOrganicLayout.setup();

        //when
        double xDist = fastOrganicLayout.getDimDistanceBetween(0, 1, 0);

        //then
        assertThat(xDist).isEqualTo(250);
    }

    @Test
    void hasYDistanceAfterSetup() {

        //given
        fastOrganicLayout.setup();

        //when
        double yDist = fastOrganicLayout.getDimDistanceBetween(0, 1, 1);

        //then
        assertThat(yDist).isEqualTo(67);
    }

    @Test
    void assertMinDistanceOK() {
        //given
        fastOrganicLayout.setup();

        //when
        assertThatCode(() -> fastOrganicLayout.assertMinDistanceIsKept()).doesNotThrowAnyException();
    }

    @Test
    void assertMinDistance() {

        //given
        fastOrganicLayout.setup();
        fastOrganicLayout.centerLocations[0][0] = 1;
        fastOrganicLayout.centerLocations[1][0] = 1;

        //when
        assertThatThrownBy(() -> fastOrganicLayout.assertMinDistanceIsKept()).isInstanceOf(IllegalStateException.class);
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
        fastOrganicLayout = new FastOrganicLayout(components, SubLayout.FORCE_CONSTANT, SubLayout.MIN_DISTANCE_LIMIT, SubLayout.MAX_DISTANCE_LIMIT, SubLayout.INITIAL_TEMP, null);

        //when
        fastOrganicLayout.execute();

        //then
        fastOrganicLayout.assertMinDistanceIsKept();
    }

    void assertAboveMinDistance(LayoutedComponent a, LayoutedComponent b) {
        var xDelta = a.x - b.x;
        var yDelta = a.y - b.y;
        var dist = Math.sqrt((xDelta * xDelta) + (yDelta * yDelta)) - radius - radius; //two times the radius

        assertThat(Math.abs(dist))
                .isGreaterThan((long) SubLayout.MIN_DISTANCE_LIMIT);
    }

}