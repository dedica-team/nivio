package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.output.map.hex.Hex;
import j2html.tags.DomContent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SVGHexTest {

    @Test
    public void simpleRenderTest() {

        SVGHex svgHex = new SVGHex(new Hex(0, 0, 0), "#00ff00");
        DomContent render = svgHex.render();
        assertEquals("<polygon stroke=\"#00ff00\" fill=\"#00ff00\" fill-opacity=\"0.1\" stroke-width=\"2\" points=\"299.0 200.0,249.5 285.7365149746594,150.50000000000003 285.7365149746594,101.0 200.0,150.49999999999994 114.2634850253406,249.5 114.26348502534059\"></polygon>", render.render());
    }
}