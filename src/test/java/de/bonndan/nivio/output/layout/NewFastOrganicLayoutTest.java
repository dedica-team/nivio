package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.ItemFactory;
import de.bonndan.nivio.util.RootPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class NewFastOrganicLayoutTest {

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

        layout = new FastOrganicLayout(layoutedComponents, new CollisionRegardingForces(50, 150), SubLayout.INITIAL_TEMP);
        layout.setDebug(true);
    }

    @Test
    void preventsCloseItems() {

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
    void completeExecutionWithThree() throws IOException {
        //given
        layout.setup();

        //when
        try {
            layout.execute();
        } finally {
            layout.getLayoutLogger().traceLocations(new File(RootPath.get() + "/src/test/dump/three.svg"));
            layout.getLayoutLogger().dump(new File(RootPath.get() + "/src/test/dump/three.txt"));
        }

        //then
        layout.assertMinDistanceIsKept(50);
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