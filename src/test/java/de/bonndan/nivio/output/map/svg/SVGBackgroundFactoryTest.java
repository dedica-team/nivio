package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.output.map.hex.Hex;
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
        Set<Hex> hexes = Set.of(new Hex(-3, -3), new Hex(10, 10));
        SVGGroupArea svgGroupArea = new SVGGroupArea(g, hexes, List.of(), new StatusValue("foo", Status.GREEN));
        SVGDimension dimension = SVGDimensionFactory.getDimension(List.of(svgGroupArea), List.of());

        List<ContainerTag> backgroundTiles = SVGBackgroundFactory.getBackgroundTiles(dimension);
        assertThat(backgroundTiles).isNotNull();
        assertThat(backgroundTiles).isNotEmpty();
        assertThat(backgroundTiles.size()).isEqualTo(343);
        assertThat(backgroundTiles.get(0).getTagName()).isEqualTo("use");
    }

}