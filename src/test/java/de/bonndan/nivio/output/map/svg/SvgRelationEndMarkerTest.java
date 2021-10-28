package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.model.RelationType;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;

import static de.bonndan.nivio.output.map.hex.Hex.*;
import static org.assertj.core.api.Assertions.assertThat;

class SvgRelationEndMarkerTest {

    @Test
    void provider() {
        SvgRelationEndMarker red = new SvgRelationEndMarker(new Point2D.Double(0, 0), RelationType.PROVIDER, "red", NORTH);

        String render = red.render().render();

        assertThat(render).isEqualTo("<circle cx=\"0.0\" cy=\"0.0\" r=\"35\" fill=\"red\"></circle>");
    }

    @Test
    void markerHasColor() {
        SvgRelationEndMarker red = new SvgRelationEndMarker(new Point2D.Double(0, 0), RelationType.DATAFLOW, "red", NORTH);

        String render = red.render().render();

        assertThat(render).contains("fill=\"red\"");
    }

    @Test
    void dataflowMarkerPointsUp() {
        SvgRelationEndMarker red = new SvgRelationEndMarker(new Point2D.Double(0, 0), RelationType.DATAFLOW, "red", NORTH);

        String render = red.render().render();

        assertThat(render).contains("rotate(0 ");
    }

    @Test
    void dataflowMarkerPointsSE() {
        SvgRelationEndMarker red = new SvgRelationEndMarker(new Point2D.Double(0, 0), RelationType.DATAFLOW, "red", SOUTH_EAST);

        String render = red.render().render();

        assertThat(render).contains("rotate(-240 ");
    }

    @Test
    void markerSetOutward() {
        SvgRelationEndMarker red = new SvgRelationEndMarker(new Point2D.Double(0, 0), RelationType.DATAFLOW, "red", SOUTH);

        String render = red.render().render();

        assertThat(render).contains("translate(-25.0 -50.0) ");
    }

}