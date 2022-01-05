package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.model.ItemFactory;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;
import java.util.ArrayList;
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
        assertThat(radius).isEqualTo(270, Offset.offset(1D));
    }

    @Test
    void getRadiusFromFarthest() {

        //given
        var child1 = new LayoutedComponent(ItemFactory.getTestItem("bar", "baz"));
        child1.setX(1050);
        child1.setY(1050);

        var child2 = new LayoutedComponent(ItemFactory.getTestItem("bar", "bak"));
        child2.setX(800);
        child2.setY(800);

        component = new LayoutedComponent(ItemFactory.getTestItem("foo", "bar"), List.of(child1,child2), new ArrayList<>());
        component.setWidth(100D);
        component.setHeight(100D);
        component.setX(1000);
        component.setY(1000);

        //when
        double radius = component.getRadius();

        //then
        assertThat(radius).isEqualTo(270, Offset.offset(1D));
    }

    @Test
    void getRadiusFromGreaterChildRadius() {

        //given
        var child1 = new LayoutedComponent(ItemFactory.getTestItem("bar", "baz"));
        child1.setX(1200);
        child1.setY(1200);

        var child2 = new LayoutedComponent(ItemFactory.getTestItem("bar", "bak"));
        child2.setX(900);
        child2.setY(900);
        child2.setWidth(500);
        child2.setHeight(500);

        component = new LayoutedComponent(ItemFactory.getTestItem("foo", "bar"), List.of(child1,child2), new ArrayList<>());
        component.setWidth(100D);
        component.setHeight(100D);
        component.setX(1000);
        component.setY(1000);

        //when
        double radius = component.getRadius();

        //then
        assertThat(radius).isEqualTo(270, Offset.offset(1D));
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