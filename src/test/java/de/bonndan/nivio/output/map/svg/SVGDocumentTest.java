package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.model.LandscapeImpl;
import de.bonndan.nivio.output.RenderingTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class SVGDocumentTest  extends RenderingTest {

    @BeforeEach
    public void setup() {
        super.setup();
    }

    @Test
    public void renderInout() throws IOException {
        String path = "/src/test/resources/example/inout";
        LandscapeImpl landscape = getLandscape(path + ".yml");
        String svg = renderLandscape(path, landscape);
        assertTrue(svg.contains("svg version=\"1.1\""));
        assertTrue(svg.contains("<image xlink:href=\"https://dedica.team/images/logo_orange_weiss.png\""));
        assertTrue(svg.contains("class=\"title\">Input and Output</text>"));
        assertTrue(svg.contains("<g class=\"hexagon-group\""));
        assertTrue(svg.contains("alignment-baseline=\"middle\" text-anchor=\"middle\">Docker Compose files</text>"));

    }
}