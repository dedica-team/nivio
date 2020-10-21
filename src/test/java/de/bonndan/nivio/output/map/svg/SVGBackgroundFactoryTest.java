package de.bonndan.nivio.output.map.svg;

import j2html.tags.ContainerTag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SVGBackgroundFactoryTest {

    @Test
    void generatesHexTemplate() {

        ContainerTag hex = SVGBackgroundFactory.getHex();
        assertThat(hex).isNotNull();
        assertThat(hex.getTagName()).isEqualTo("polygon");
    }

    @Test
    void getBackgroundTiles() {
        List<ContainerTag> backgroundTiles = SVGBackgroundFactory.getBackgroundTiles(-1, 10, -1, 10, -500, 500);
        assertThat(backgroundTiles).isNotNull();
        assertThat(backgroundTiles).isNotEmpty();
        assertThat(backgroundTiles.size()).isEqualTo(57);
        assertThat(backgroundTiles.get(0).getTagName()).isEqualTo("use");
    }

}