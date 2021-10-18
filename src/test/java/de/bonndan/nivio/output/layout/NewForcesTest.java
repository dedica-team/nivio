package de.bonndan.nivio.output.layout;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;

import static org.assertj.core.api.Assertions.assertThat;

class NewForcesTest {

    private static final int MIN_DISTANCE_LIMIT = 50;
    private static final int MAX_DISTANCE_LIMIT = 250;
    private NewForces forces;
    private double[][] centerLocations = new double[2][2];
    private double[] radius = new double[2];

    @BeforeEach
    void setup() {
        forces = new NewForces(MIN_DISTANCE_LIMIT, MAX_DISTANCE_LIMIT);
        radius[0] = 50;
        radius[1] = 50;
    }

    @Test
    void calcRepulsionDisplacement() {

        //when
        centerLocations[0][0] = 0;
        centerLocations[0][1] = 0;

        centerLocations[1][0] = 200;
        centerLocations[1][1] = 0;

        //when

        Point2D.Double repulsion = forces.getRepulsion(centerLocations[0], centerLocations[1], radius[0], radius[1]);

        //then
        assertThat(repulsion.x).isEqualTo(0, Offset.offset(1D));
        assertThat(repulsion.y).isEqualTo(-125, Offset.offset(1D));
    }


    @Test
    void calcRepulsionAlongVector() {

        //when
        centerLocations[0][0] = 0;
        centerLocations[0][1] = 0;

        centerLocations[1][0] = 100;
        centerLocations[1][1] = 5;


        //when

        Point2D.Double repulsionDisplacement = forces.getRepulsion(centerLocations[0], centerLocations[1], radius[0], radius[1]);

        //then
        assertThat(repulsionDisplacement.x / repulsionDisplacement.y).isEqualTo(5D / 100D, Offset.offset(0.1D));
    }


    @Test
    @DisplayName("Repulsion is greater on closer distances")
    void calcRepulsionDisplacementComp() {

        //when
        //create overlap
        centerLocations[0][0] = 0;
        centerLocations[0][1] = 0;

        centerLocations[1][0] = 200;
        centerLocations[1][1] = 100;

        //when
        Point2D.Double repulsionDisplacement = forces.getRepulsion(centerLocations[0], centerLocations[1], radius[0], radius[1]);

        //then
        assertThat(Math.abs(repulsionDisplacement.y)).isGreaterThan(Math.abs(repulsionDisplacement.x));
    }


    @Test
    void calcRepulsionDisplacementTooFar() {

        //when

        //create overlap
        centerLocations[0][0] = 0;
        centerLocations[0][1] = 0;

        centerLocations[1][0] = 2000;
        centerLocations[1][1] = 0;


        //when
        Point2D.Double repulsionDisplacement = forces.getRepulsion(centerLocations[0], centerLocations[1], radius[0], radius[1]);

        //then
        assertThat(repulsionDisplacement.x).isEqualTo(0);
        assertThat(repulsionDisplacement.y).isEqualTo(0);
    }
    

    @Test
    @DisplayName("Attraction moves nodes closer along vector")
    void calcAttractionDisplacement() {
        //given

        centerLocations[0][0] = 0;
        centerLocations[0][1] = 0;

        centerLocations[1][0] = 400;
        centerLocations[1][1] = 200;

        //when
        var displacement = forces.getAttraction(centerLocations[0], centerLocations[1], radius[0], radius[1]);

        //then
        //displacement is subtracted from i and added to j
        assertThat(displacement.x).isGreaterThan(0).isEqualTo(223, Offset.offset(1D)); //shift in x direction
        assertThat(displacement.y).isGreaterThan(0).isLessThan(displacement.x); //shift in y direction

        assertThat(displacement.x / displacement.y).isEqualTo(400 / 200);

        //when
        var reverse  = forces.getAttraction(centerLocations[1], centerLocations[0], radius[1], radius[0]);

        //then
        assertThat(reverse.x).isLessThan(0).isEqualTo(displacement.x * -1); //shift in x direction
        assertThat(reverse.y).isLessThan(0).isEqualTo(displacement.y * -1); //shift in x direction
    }


}