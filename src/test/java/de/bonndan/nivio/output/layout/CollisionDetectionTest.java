package de.bonndan.nivio.output.layout;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CollisionDetectionTest {

    private CollisionDetection collisionDetection;

    @BeforeEach
    void setup() {
        collisionDetection = new CollisionDetection(50);
    }

    @Test
    @DisplayName("Movement reduced on collision")
    void testNoCollision() {
        //given
        double[][] centerLocations = new double[2][];
        centerLocations[0][0] = 0;
        centerLocations[0][1] = 0;

        centerLocations[1][0] = 1000;
        centerLocations[1][1] = 1000;

        double[] radius = new double[2];
        radius[0] = 50;
        radius[1] = 50;

        //when
        var collisionReductionFactor = collisionDetection.getFreeMovementFactor(centerLocations, radius, 0, 200, 200);

        //then
        assertThat(collisionReductionFactor).isEqualTo(1);
    }
}