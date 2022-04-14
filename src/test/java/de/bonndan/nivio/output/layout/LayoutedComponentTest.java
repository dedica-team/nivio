package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.model.ItemFactory;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LayoutedComponentTest {

    private LayoutedComponent component;

    @BeforeEach
    void setUp() {
        component = new LayoutedComponent(ItemFactory.getTestItem("foo", "bar"), 100D ,100D);
        component.setCenterX(1000);
        component.setCenterY(1000);
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
        var child1 = new LayoutedComponent(ItemFactory.getTestItem("bar", "baz"), 50D, 50D);
        child1.setCenterX(1050);
        child1.setCenterY(1050);

        var child2 = new LayoutedComponent(ItemFactory.getTestItem("bar", "bak"), 50D, 50D);
        child2.setCenterX(800);
        child2.setCenterY(800);

        component =  LayoutedComponent.from(ItemFactory.getTestItem("foo", "bar"), List.of(child1,child2));
        component.setCenterX(1000);
        component.setCenterY(1000);

        //when
        double radius = component.getRadius();

        //then
        assertThat(radius).isEqualTo(412, Offset.offset(1D));
    }

    @Test
    void getRadiusFromGreaterChildRadius() {

        //given
        var child1 = new LayoutedComponent(ItemFactory.getTestItem("bar", "baz"),50D, 50D);
        child1.setCenterX(1200);
        child1.setCenterY(1200);

        var child2 = new LayoutedComponent(ItemFactory.getTestItem("bar", "bak"), 500, 500);
        child2.setCenterX(900);
        child2.setCenterY(900);

        component = LayoutedComponent.from(ItemFactory.getTestItem("foo", "bar"), List.of(child1,child2));
        component.setCenterX(1000);
        component.setCenterY(1000);

        //when
        double radius = component.getRadius();

        //then
        assertThat(radius).isEqualTo(447, Offset.offset(1D));
    }

    @Test
    void getCenterFromSingleChild() {

        //given
        var child1 = new LayoutedComponent(ItemFactory.getTestItem("bar", "baz"), 1050, 1050);
        child1.setCenterX(800);
        child1.setCenterY(800);

        component =  LayoutedComponent.from(ItemFactory.getTestItem("foo", "bar"), List.of(child1));


        //when
        Point2D.Double center = component.getCenter();

        //then
        assertThat(center.x).isEqualTo(800);
        assertThat(center.y).isEqualTo(800);
    }

    @Test
    void getCenter() {

        //given
        var child1 = new LayoutedComponent(ItemFactory.getTestItem("bar", "baz"), 1050, 1050);
        child1.setCenterX(200);
        child1.setCenterY(3400);

        var child2 = new LayoutedComponent(ItemFactory.getTestItem("bar", "bak"), 50D, 50D);
        child2.setCenterX(800);
        child2.setCenterY(800);

        component =  LayoutedComponent.from(ItemFactory.getTestItem("foo", "bar"), List.of(child1,child2));


        //when
        Point2D.Double center = component.getCenter();

        //then
        assertThat(center.x).isEqualTo(500);
        assertThat(center.y).isEqualTo(2100);
    }
}