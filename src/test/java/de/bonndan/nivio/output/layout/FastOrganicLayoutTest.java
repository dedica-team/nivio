package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.ItemFactory;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FastOrganicLayoutTest {

    int radius = 500;

    @Test
    void preventsCloseItems() {

        List<LayoutedComponent> layoutedComponents = new ArrayList<>();
        Item testItemA = ItemFactory.getTestItem("test", "a");
        Item testItemB = ItemFactory.getTestItem("test", "b");
        Item testItemC = ItemFactory.getTestItem("test", "c");


        LayoutedComponent a = new LayoutedComponent(testItemA, List.of(testItemB, testItemC));
        a.setX(100);
        a.setY(100);
        a.setHeight(radius);
        layoutedComponents.add(a);

        LayoutedComponent b = new LayoutedComponent(testItemB, List.of(testItemA, testItemC));
        b.setX(101);
        b.setY(101);
        b.setHeight(radius);
        layoutedComponents.add(b);

        LayoutedComponent c = new LayoutedComponent(testItemC, List.of(testItemA, testItemB));
        c.setX(102);
        c.setY(102);
        c.setHeight(radius);
        layoutedComponents.add(c);


        FastOrganicLayout fastOrganicLayout = new FastOrganicLayout(layoutedComponents, SubLayout.FORCE_CONSTANT, SubLayout.MIN_DISTANCE_LIMIT, SubLayout.MAX_DISTANCE_LIMIT, SubLayout.INITIAL_TEMP, null);

        //when
        fastOrganicLayout.execute();

        //then
        assertAboveMinDistance(a,b);
        assertAboveMinDistance(b,c);
        assertAboveMinDistance(a,c);

    }

    void assertAboveMinDistance(LayoutedComponent a, LayoutedComponent b) {
        var xDelta = a.x - b.x;
        var yDelta = a.y - b.y;
        var dist = Math.sqrt((xDelta * xDelta) + (yDelta * yDelta)) - radius - radius; //two times the radius

        assertThat(Math.abs(dist))
                .isGreaterThan((long) SubLayout.MIN_DISTANCE_LIMIT);
    }

}