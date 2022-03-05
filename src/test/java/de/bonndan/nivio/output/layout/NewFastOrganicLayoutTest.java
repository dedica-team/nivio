package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.ItemFactory;
import de.bonndan.nivio.model.LayoutConfig;
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


        a = new LayoutedComponent(testItemA, new ArrayList<>(), List.of(testItemB, testItemC), 50, 500D);
        a.setCenterX(100);
        a.setCenterY(100);
        layoutedComponents.add(a);

        b = new LayoutedComponent(testItemB, new ArrayList<>(), List.of(testItemA, testItemC), 50, 500D);
        b.setCenterX(101);
        b.setCenterY(101);
        layoutedComponents.add(b);

        c = new LayoutedComponent(testItemC, new ArrayList<>(), List.of(testItemA, testItemB), 50, 500D);
        c.setCenterX(102);
        c.setCenterY(102);
        layoutedComponents.add(c);

        layout = new FastOrganicLayout(layoutedComponents, new CollisionRegardingForces(50, 150), LayoutConfig.GROUP_LAYOUT_INITIAL_TEMP);
        layout.setDebug(true);
    }

    @Test
    void preventsCloseItems() {

        //when
        layout.execute();

        //then
        var xDelta1 = a.getCenterX() - b.getCenterX();
        var yDelta1 = a.getCenterY() - b.getCenterY();
        var dist1 = Math.sqrt((xDelta1 * xDelta1) + (yDelta1 * yDelta1)) - 50 - 50; //two times the radius

        assertThat(Math.abs(dist1))
                .isGreaterThan((long) LayoutConfig.ITEM_MIN_DISTANCE_LIMIT);

        var xDelta = b.getCenterX() - c.getCenterX();
        var yDelta = b.getCenterY() - c.getCenterY();
        var dist = Math.sqrt((xDelta * xDelta) + (yDelta * yDelta)) - 50 - 50; //two times the radius

        assertThat(Math.abs(dist))
                .isGreaterThan((long) LayoutConfig.ITEM_MIN_DISTANCE_LIMIT);
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