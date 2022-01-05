package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.output.map.hex.Hex;
import de.bonndan.nivio.output.map.hex.MapTile;
import j2html.tags.ContainerTag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class SVGBackgroundFactoryTest {

    @Test
    void generatesHexTemplate() {

        ContainerTag hex = SVGBackgroundFactory.getHex();
        assertThat(hex).isNotNull();
        assertThat(hex.getTagName()).isEqualTo("polygon");
    }

    @Test
    void getBackgroundTiles() {

        Group g = new Group("a", "landscapeIdentifier");
        Set<MapTile> hexes = Set.of(new MapTile(new Hex(-3, -3)), new MapTile(new Hex(10, 10)));
        SVGGroupArea svgGroupArea = new SVGGroupArea(g, hexes, List.of());
        SVGDimension dimension = SVGDimensionFactory.getDimension(List.of(svgGroupArea), List.of());

        List<ContainerTag> backgroundTiles = SVGBackgroundFactory.getBackgroundTiles(dimension);
        assertThat(backgroundTiles).isNotNull();
        assertThat(backgroundTiles).isNotEmpty();
        assertThat(backgroundTiles.size()).isEqualTo(312);
        assertThat(backgroundTiles.get(0).getTagName()).isEqualTo("use");
    }

}