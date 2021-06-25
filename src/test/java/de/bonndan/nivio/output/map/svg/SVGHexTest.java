package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.output.map.hex.Hex;
import j2html.tags.DomContent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SVGHexTest {

    @Test
    public void simpleRenderTest() {

        SVGHex svgHex = new SVGHex(new Hex(0, 0, 0), "#00ff00", "#00ff00");
        DomContent render = svgHex.render();
        assertEquals("<polygon stroke-width=\"1\" points=\"300.0 200.0,250.0 286.6,150.0 286.6,100.0 200.0,150.0 113.4,250.0 113.4\" stroke=\"#00ff00\" fill=\"#00ff00\" fill-opacity=\"0.3\"></polygon>", render.render());
    }
}