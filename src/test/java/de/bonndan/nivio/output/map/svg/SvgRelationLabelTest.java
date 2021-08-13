package de.bonndan.nivio.output.map.svg;

import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;

import static org.assertj.core.api.Assertions.assertThat;

class SvgRelationLabelTest {

    @Test
    void render() {
        SvgRelationLabel svgRelationLabel = new SvgRelationLabel("foo",
                new Point2D.Float(10f, 10f),
                new Point2D.Float(20f, 20f),
                "red",
                true
        );

        assertThat(svgRelationLabel).isNotNull();

        String render = svgRelationLabel.render().render();
        assertThat(render)
                .contains("text-anchor=\"middle\"")
                .contains("transform=\"translate(10.0 0.0) rotate(45.0 0 0)\"")
                .contains("foo")
                .contains("fill=\"red\"");
    }
}