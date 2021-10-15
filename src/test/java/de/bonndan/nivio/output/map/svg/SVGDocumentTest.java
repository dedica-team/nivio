package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.input.http.CachedResponse;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.output.RenderingTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SVGDocumentTest extends RenderingTest {

    @BeforeEach
    public void setup() throws URISyntaxException {
        super.setup();
    }

    @Test
    void renderInout() throws IOException {
        String path = "/src/test/resources/example/inout";
        Landscape landscape = getLandscape(path + ".yml");
        String svg = renderLandscape(path, landscape);
        assertTrue(svg.contains("svg version=\"1.1\""));
        assertTrue(svg.contains("class=\"title\">Input and Output</text>"));
        assertThat(svg).contains("<g data-identifier=\"inout/output/svg\" class=\"item");
        assertTrue(svg.contains(">Docker Compose files</text>"));
    }


    @Test
    void renderCustomFill() throws IOException {

        String path = "/src/test/resources/example/dedica";
        Landscape landscape = getLandscape(path + ".yml");

        //when
        String svg = renderLandscape(path, landscape);

        //then
        assertTrue(svg.contains("svg version=\"1.1\""));

        //external icon
        assertThat(svg).contains("fill=\"url(#Wm05dg==)\""); //pattern for "foo" response

    }

    @Test
    void embedsExternalImages() throws IOException, URISyntaxException {
        String path = "/src/test/resources/example/dedica";
        CachedResponse response = mock(CachedResponse.class);
        when(response.getBytes()).thenReturn("foo".getBytes());
        when(httpService.getResponse(any(URL.class))).thenReturn(response);

        Landscape landscape = getLandscape(path + ".yml");

        //when
        String svg = renderLandscape(path, landscape);

        //then
        assertThat(svg)
                .doesNotContain("https://dedica.team/images/logo_orange_weiss.png") //external image, to be replaced
                .doesNotContain("https://dedica.team/images/logo.png") //map logo
                .doesNotContain("danielpozzi.jpg") //external image, to be replaced
                .contains("fill=\"url(#Wm05dg==)\""); //pattern for "foo" response

    }



}