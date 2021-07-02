package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.model.Item;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import static de.bonndan.nivio.model.ItemFactory.getTestItem;
import static org.junit.jupiter.api.Assertions.*;

class InitialPlacementStrategyTest {

    @Test
    public void placesInCircle() {
        ArrayList<LayoutedComponent> layoutedComponents = new ArrayList<>();
        layoutedComponents.add(new LayoutedComponent(getTestItem("test", "a")));
        layoutedComponents.add(new LayoutedComponent(getTestItem("test", "b")));
        layoutedComponents.add(new LayoutedComponent(getTestItem("test", "c")));
        layoutedComponents.add(new LayoutedComponent(getTestItem("test", "d")));

        InitialPlacementStrategy initialPlacementStrategy = new InitialPlacementStrategy(layoutedComponents);
        Point2D.Double place1 = initialPlacementStrategy.place(0);
        assertEquals(50, place1.x);
        assertEquals(0, place1.y);

        Point2D.Double place2 = initialPlacementStrategy.place(1);
        assertEquals(0, place2.x);
        assertEquals(50, place2.y);

        Point2D.Double place3 = initialPlacementStrategy.place(2);
        assertEquals(-50, place3.x);
        assertEquals(0, place3.y);

        Point2D.Double place4 = initialPlacementStrategy.place(3);
        assertEquals(0, place4.x);
        assertEquals(-50, place4.y);
    }
}