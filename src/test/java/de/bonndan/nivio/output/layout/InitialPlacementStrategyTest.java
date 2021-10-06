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
        assertThat(place1.x).isEqualTo(100);
        assertThat(place1.y).isEqualTo(0);

        Point2D.Double place2 = initialPlacementStrategy.place(1);
        assertThat(place2.x).isEqualTo(0);
        assertThat(place2.y).isEqualTo(100);

        Point2D.Double place3 = initialPlacementStrategy.place(2);
        assertThat(place3.x).isEqualTo(-100);
        assertThat(place3.y).isEqualTo(-0);

        Point2D.Double place4 = initialPlacementStrategy.place(3);
        assertThat(place4.x).isEqualTo(0);
        assertThat(place4.y).isEqualTo(-100);
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
        assertThat(place1.x).isEqualTo(100);
        assertThat(place1.y).isEqualTo(0);

        Point2D.Double place2 = initialPlacementStrategy.place(1);
        assertThat(place2.x).isEqualTo(92);
        assertThat(place2.y).isEqualTo(38);

        Point2D.Double place3 = initialPlacementStrategy.place(2);
        assertThat(place3.x).isEqualTo(-38);
        assertThat(place3.y).isEqualTo(-92);

        Point2D.Double place4 = initialPlacementStrategy.place(3);
        assertThat(place4.x).isEqualTo(92);
        assertThat(place4.y).isEqualTo(-38);
    }

    LayoutedComponent getLayoutedComponent(String identifier, double width, double height) {
        LayoutedComponent layoutedComponent = new LayoutedComponent(getTestItem("test", identifier));
        layoutedComponent.setWidth(width);
        layoutedComponent.setHeight(height);
        return layoutedComponent;
    }
}