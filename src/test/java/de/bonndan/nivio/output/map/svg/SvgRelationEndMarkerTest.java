package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.model.RelationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;

import static de.bonndan.nivio.output.map.hex.Hex.*;
import static org.assertj.core.api.Assertions.assertThat;

class SvgRelationEndMarkerTest {

    private BezierPath.PointWithAngle endPoint;

    @BeforeEach
    void setup() {
        endPoint = new BezierPath.PointWithAngle(new Point2D.Float(0, 0), 90);
    }

    @Test
    void provider() {
        SvgRelationEndMarker red = new SvgRelationEndMarker(endPoint, RelationType.PROVIDER, "red", NORTH);

        String render = red.render().render();

        assertThat(render).isEqualTo("<circle cx=\"0.0\" cy=\"0.0\" r=\"35\" fill=\"red\"></circle>");
    }

    @Test
    void markerHasColor() {
        SvgRelationEndMarker red = new SvgRelationEndMarker(endPoint, RelationType.DATAFLOW, "red", NORTH);

        String render = red.render().render();

        assertThat(render).contains("fill=\"red\"");
    }

    @Test
    void dataflowMarkerPointsUp() {
        SvgRelationEndMarker red = new SvgRelationEndMarker(endPoint, RelationType.DATAFLOW, "red", NORTH);

        String render = red.render().render();

        assertThat(render).contains("rotate(180 ");
    }

    @Test
    void dataflowMarkerPointsSE() {
        SvgRelationEndMarker red = new SvgRelationEndMarker(endPoint, RelationType.DATAFLOW, "red", SOUTH_EAST);

        String render = red.render().render();

        assertThat(render).contains("rotate(180 ");
    }

    @Test
    void markerSetOutward() {
        SvgRelationEndMarker red = new SvgRelationEndMarker(endPoint, RelationType.DATAFLOW, "red", SOUTH);

        String render = red.render().render();

        assertThat(render).contains("translate(-25.0 -25.0) ");
    }

}