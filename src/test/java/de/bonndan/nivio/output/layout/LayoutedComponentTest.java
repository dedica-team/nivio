package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.model.ItemFactory;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class LayoutedComponentTest {

    private LayoutedComponent component;

    @BeforeEach
    void setUp() {
        component = new LayoutedComponent(ItemFactory.getTestItem("foo", "bar"));
        component.setWidth(100D);
        component.setHeight(100D);
        component.setX(1000);
        component.setY(1000);
    }

    @Test
    void setWidthException() {
        assertThatThrownBy(() -> component.setWidth(0D)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void setHeightException() {
        assertThatThrownBy(() -> component.setWidth(0D)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getRadiusFallback() {

        //when
        double radius = component.getRadius();

        //then
        assertThat(radius).isEqualTo(50);
    }

    @Test
    void getRadius() {

        //given
        var child = new LayoutedComponent(ItemFactory.getTestItem("bar", "baz"));
        child.setX(1050);
        child.setY(1050);
        component.setChildren(List.of(child));

        //when
        double radius = component.getRadius();

        //then
        assertThat(radius).isEqualTo(35, Offset.offset(1D));
    }

    @Test
    void getCenter() {
        //when
        Point2D.Double center = component.getCenter();

        //then
        assertThat(center.x).isEqualTo(1050);
        assertThat(center.y).isEqualTo(1050);
    }
}