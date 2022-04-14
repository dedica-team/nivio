package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.assessment.StatusValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;
import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

class SvgRelationLabelTest {

    private SvgRelationLabel svgRelationLabel;

    @BeforeEach
    void setup() {
         svgRelationLabel = new SvgRelationLabel(
                "foo",
                new BezierPath.PointWithAngle(new Point2D.Float(10f, 10f), 90),
                "blue",
                new StatusValue(URI.create("relation://bar"), "cost", Status.RED, "")
        );
    }

    @Test
    void render() {
        assertThat(svgRelationLabel).isNotNull();

        String render = svgRelationLabel.render().render();
        assertThat(render)
                .contains("text-anchor=\"middle\"")
                .contains("transform=\"rotate(90.0 0 0)\"")
                .contains("foo")
                .contains("fill=\"blue\"");
    }

    @Test
    void statusCircle() {

        //when
        String render = svgRelationLabel.render().render();

        //when
        assertThat(render)
                .contains("foo")
                .contains("fill=\"red\"")
                .contains("stroke=\"blue\"");
    }

}