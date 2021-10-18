package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.ItemFactory;
import de.bonndan.nivio.model.RelationFactory;
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

import static org.assertj.core.api.Assertions.assertThat;

class NewForcesTest {

    private static final int MIN_DISTANCE_LIMIT = 50;
    private static final int MAX_DISTANCE_LIMIT = 250;
    private static final int INITIAL_TEMP = 300;
    private NewForces forces;
    private double[][] centerLocations = new double[2][2];
    private double[] radius = new double[2];

    @BeforeEach
    void setup() {
        forces = new NewForces(MIN_DISTANCE_LIMIT, MAX_DISTANCE_LIMIT, INITIAL_TEMP);
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

        //when

        Point2D.Double reverse = forces.getRepulsion(centerLocations[1], centerLocations[0], radius[1], radius[0]);

        //then
        assertThat(reverse.x).isEqualTo(0, Offset.offset(1D));
        assertThat(reverse.y).isEqualTo(125, Offset.offset(1D));
    }

    @Test
    void calcRepulsionDisplacement2() {

        //when
        centerLocations[0][0] = 100;
        centerLocations[0][1] = 100;

        centerLocations[1][0] = 0;
        centerLocations[1][1] = 0;

        //when

        Point2D.Double repulsion = forces.getRepulsion(centerLocations[0], centerLocations[1], radius[0], radius[1]);

        //then
        assertThat(repulsion.x).isEqualTo(176, Offset.offset(1D));
        assertThat(repulsion.y).isEqualTo(176, Offset.offset(1D));
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
        centerLocations = new double[3][];
        centerLocations[0] = new double[2];
        centerLocations[0][0] = 0;
        centerLocations[0][1] = 0;

        centerLocations[1] = new double[2];
        centerLocations[1][0] = 200;
        centerLocations[1][1] = 200;

        centerLocations[2] = new double[2];
        centerLocations[2][0] = 250;
        centerLocations[2][1] = 250;

        radius = new double[3];
        radius[0] = 50;
        radius[1] = 50;
        radius[2] = 50;

        //when
        Point2D.Double repulsionDisplacement1 = forces.getRepulsion(centerLocations[0], centerLocations[1], radius[0], radius[1]);
        Point2D.Double repulsionDisplacement2 = forces.getRepulsion(centerLocations[0], centerLocations[2], radius[0], radius[2]);

        //then
        assertThat(Math.abs(repulsionDisplacement1.x)).isGreaterThan(Math.abs(repulsionDisplacement2.x));
        assertThat(Math.abs(repulsionDisplacement1.y)).isGreaterThan(Math.abs(repulsionDisplacement2.y));
    }


    @Test
    void calcRepulsionDisplacementTooFar() {

        //given
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
        assertThat(displacement.x).isGreaterThan(0).isEqualTo(111, Offset.offset(1D)); //shift in x direction
        assertThat(displacement.y).isGreaterThan(0).isLessThan(displacement.x); //shift in y direction

        assertThat(displacement.x / displacement.y).isEqualTo(400 / 200f);

        //when
        var reverse = forces.getAttraction(centerLocations[1], centerLocations[0], radius[1], radius[0]);

        //then
        assertThat(reverse.x).isLessThan(0).isEqualTo(displacement.x * -1); //shift in x direction
        assertThat(reverse.y).isLessThan(0).isEqualTo(displacement.y * -1); //shift in x direction
    }

    @Test
    @DisplayName("does not move if collides")
    void doesNotMoveIfCollides() {
        //given
        centerLocations[0][0] = 0;
        centerLocations[0][1] = 0;

        centerLocations[1][0] = 100;
        centerLocations[1][1] = 50;


        //when
        var displacement = forces.applyDisplacement(centerLocations, radius, 0, 100, 100, 300);

        //then
        assertThat(displacement.x).isEqualTo(0);
        assertThat(displacement.y).isEqualTo(0);
    }

    @Test
    @DisplayName("apply displacement")
    void applyDisplacement() {
        //given

        centerLocations[0][0] = 0;
        centerLocations[0][1] = 0;

        centerLocations[1][0] = 400;
        centerLocations[1][1] = 200;

        //when
        var displacement = forces.applyDisplacement(centerLocations, radius, 0, 100, 100, 300);

        //then
        assertThat(displacement.x).isEqualTo(100, Offset.offset(1D));
        assertThat(displacement.y).isEqualTo(100, Offset.offset(1D));

    }

    @Test
    @DisplayName("apply displacement reduced")
    void applyDisplacementWithCollision() {
        //given

        centerLocations[0][0] = 0;
        centerLocations[0][1] = 0;

        centerLocations[1][0] = 400;
        centerLocations[1][1] = 200;

        //when
        var displacement = forces.applyDisplacement(centerLocations, radius, 0, 350, 200, 300);

        //then
        assertThat(displacement.x).isLessThan(350);
        assertThat(displacement.y).isLessThan(200);
        assertThat(displacement.x / displacement.y).isEqualTo(350 / 200f);

    }

    @Test
    @DisplayName("prevent dense placement in extreme layouts")
    void singleLargeGroupWithRelations() throws IOException {

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

        var layout = new FastOrganicLayout(components, forces, SubLayout.INITIAL_TEMP);
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
}