package de.bonndan.nivio.output.layout;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import static de.bonndan.nivio.model.ItemFactory.getTestItem;
import static org.assertj.core.api.Assertions.assertThat;

class InitialPlacementStrategyTest {

    @Test
    @DisplayName("symmetric placement for four equal elements")
    void fourEquals() {
        ArrayList<LayoutedComponent> layoutedComponents = new ArrayList<>();
        layoutedComponents.add(getLayoutedComponent("a", 100, 100));
        layoutedComponents.add(getLayoutedComponent("b", 100, 100));
        layoutedComponents.add(getLayoutedComponent("c", 100, 100));
        layoutedComponents.add(getLayoutedComponent("d", 100, 100));

        InitialPlacementStrategy initialPlacementStrategy = new InitialPlacementStrategy(layoutedComponents);
        Point2D.Double place1 = initialPlacementStrategy.place(0);
        assertThat(place1.x).isEqualTo(300);
        assertThat(place1.y).isEqualTo(0);

        Point2D.Double place2 = initialPlacementStrategy.place(1);
        assertThat(place2.x).isEqualTo(-1);
        assertThat(place2.y).isEqualTo(300);

        Point2D.Double place3 = initialPlacementStrategy.place(2);
        assertThat(place3.x).isEqualTo(-300);
        assertThat(place3.y).isEqualTo(-2);

        Point2D.Double place4 = initialPlacementStrategy.place(3);
        assertThat(place4.x).isEqualTo(4);
        assertThat(place4.y).isEqualTo(-300);
    }

    @Test
    void placesInCircle() {
        ArrayList<LayoutedComponent> layoutedComponents = new ArrayList<>();
        layoutedComponents.add(getLayoutedComponent("a", 100, 100));
        layoutedComponents.add(getLayoutedComponent("b", 1000, 1000));
        layoutedComponents.add(getLayoutedComponent("c", 200, 400));
        layoutedComponents.add(getLayoutedComponent("d", 100, 100));

        InitialPlacementStrategy initialPlacementStrategy = new InitialPlacementStrategy(layoutedComponents);
        Point2D.Double place1 = initialPlacementStrategy.place(0);
        assertThat(place1.x).isEqualTo(300);
        assertThat(place1.y).isEqualTo(0);

        Point2D.Double place2 = initialPlacementStrategy.place(1);
        assertThat(place2.x).isEqualTo(184);
        assertThat(place2.y).isEqualTo(237);

        Point2D.Double place3 = initialPlacementStrategy.place(2);
        assertThat(place3.x).isEqualTo(-206);
        assertThat(place3.y).isEqualTo(-218);

        Point2D.Double place4 = initialPlacementStrategy.place(3);
        assertThat(place4.x).isEqualTo(186);
        assertThat(place4.y).isEqualTo(-235);
    }

    @Test
    void placesOneAtOrigin() {
        ArrayList<LayoutedComponent> layoutedComponents = new ArrayList<>();
        layoutedComponents.add(getLayoutedComponent("a", 100, 100));

        InitialPlacementStrategy initialPlacementStrategy = new InitialPlacementStrategy(layoutedComponents);
        Point2D.Double place1 = initialPlacementStrategy.place(0);
        assertThat(place1.x).isEqualTo(0);
        assertThat(place1.y).isEqualTo(0);
    }

    LayoutedComponent getLayoutedComponent(String identifier, double width, double height) {
        return new LayoutedComponent(getTestItem("test", identifier), width, height);
    }
}