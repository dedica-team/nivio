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
        assertEquals("<polygon stroke=\"#00ff00\" fill=\"#00ff00\" fill-opacity=\"0.1\" stroke-width=\"1\" data-hex-coords=\"0,0\" points=\"300 200,250 287,150 287,100 200,150 113,250 113\"></polygon>", render.render());
    }
}