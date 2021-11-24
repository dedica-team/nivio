package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.ItemFactory;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FastOrganicLayoutTest {

    private ArrayList<LayoutedComponent> layoutedComponents;
    private LayoutedComponent a;
    private LayoutedComponent b;
    private LayoutedComponent c;
    private FastOrganicLayout layout;
    private Forces forces;

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

        forces = new OriginalForces(SubLayout.MIN_DISTANCE_LIMIT, SubLayout.MAX_DISTANCE_LIMIT, SubLayout.FORCE_CONSTANT);
        layout = new FastOrganicLayout(layoutedComponents, forces, SubLayout.INITIAL_TEMP);
        layout.setDebug(true);
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

        assertThat(dispX01 / dispY01).isEqualTo(100 / 200, Offset.offset(1D));

        double dispX10 = layout.dispX[1];
        double dispY11 = layout.dispY[1];
        assertThat(dispX10).isGreaterThan(0);
        assertThat(dispX10 / dispY11).isEqualTo(100 / 200, Offset.offset(1D));
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
        double dispX = layout.dispX[0];
        double dispY = layout.dispY[0];
        assertThat(dispX).isEqualTo(-11, Offset.offset(1D));
        assertThat(dispY)
                .isLessThan(0)
                .isLessThan(dispX)
                .isEqualTo(-224, Offset.offset(1D));
    }

    @Test
    void reduceTempRegularly() {
        //given
        layout.setup();
        layout.iteration = 1;

        //when
        layout.reduceTemperature();

        //then
        assertThat(layout.temperature).isLessThan(layout.initialTemp);
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


}