package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.assessment.StatusValue;
import j2html.tags.DomContent;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

class SVGStatusCircleTest {

    @Test
    void render() {

        //given
        SVGStatusCircle svgStatusCircle = new SVGStatusCircle(20, "blue", new StatusValue(URI.create("foo://bar"), "test", Status.RED, "broken"));

        //when
        DomContent render = svgStatusCircle.render();
        assertThat(render).isNotNull();

        String svg = render.render();
        assertThat(svg).contains("circle")
                .contains("stroke=\"blue\"")
                .contains("fill=\"" + Status.RED.getColor() + "\"");
    }
}