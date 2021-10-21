package de.bonndan.nivio.output.layout;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;

import static org.assertj.core.api.Assertions.assertThat;

class OriginalForcesTest {

    private OriginalForces originalForces;
    private double[][] centerLocations = new double[2][2];
    private double[] radius = new double[2];

    @BeforeEach
    void setup() {
        originalForces = new OriginalForces(SubLayout.MIN_DISTANCE_LIMIT, SubLayout.MAX_DISTANCE_LIMIT, SubLayout.FORCE_CONSTANT);
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

        Point2D.Double repulsionDisplacement = originalForces.getRepulsion(centerLocations[0], centerLocations[1], radius[0], radius[1]);

        //then
        assertThat(repulsionDisplacement.x).isEqualTo(-399, Offset.offset(1D));
        assertThat(repulsionDisplacement.y).isEqualTo(0, Offset.offset(1D));
    }


    @Test
    void calcRepulsionAlongVector() {

        //when
        centerLocations[0][0] = 0;
        centerLocations[0][1] = 0;

        centerLocations[1][0] = 100;
        centerLocations[1][1] = 5;


        //when

        Point2D.Double repulsionDisplacement = originalForces.getRepulsion(centerLocations[0], centerLocations[1], radius[0], radius[1]);

        //then
        assertThat(repulsionDisplacement.x / repulsionDisplacement.y).isEqualTo(100D / 5D, Offset.offset(0.1D));
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
        Point2D.Double repulsionDisplacement = originalForces.getRepulsion(centerLocations[0], centerLocations[1], radius[0], radius[1]);

        //then
        assertThat(repulsionDisplacement.x).isEqualTo(0);
        assertThat(repulsionDisplacement.y).isEqualTo(0);
    }
}