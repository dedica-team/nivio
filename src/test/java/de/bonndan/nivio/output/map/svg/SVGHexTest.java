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
        assertEquals("<polygon stroke=\"#00ff00\" fill=\"#00ff00\" fill-opacity=\"0.1\" stroke-width=\"2\" data-hex-coords=\"0,0\" points=\"299.0 200.0,250.0 286.0,151.0 286.0,101.0 200.0,150.0 114.0,250.0 114.0\"></polygon>", render.render());
    }
}