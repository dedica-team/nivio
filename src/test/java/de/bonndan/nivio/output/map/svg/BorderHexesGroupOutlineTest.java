package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.output.map.hex.Hex;
import de.bonndan.nivio.output.map.hex.PathElement;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


class BorderHexesGroupOutlineTest {

    @Test
    void generatesOutline() {

        //given
        Hex h1 = new Hex(0,1);
        Hex h2 = new Hex(0,2);
        Set<Hex> groupArea = Set.of(h1, h2);

        LinkedHashMap<Hex, SVGGroupAreaOutlineFactory.Position> borderHexes = SVGGroupAreaOutlineFactory.getBorderHexes(h1, groupArea);


        //when
        List<PathElement> path = BorderHexesGroupOutline.getPath(borderHexes, groupArea);

        //then
        assertThat(path).isNotEmpty();

        SVGPath svgPath = new SVGPath(path, null);
        assertThat(svgPath.render().render()).contains("M 150.0 459.8 100.0 373.2 150.0 286.6 250.0 286.6 300.0 373.2 250.0 459.8 300.0 546.4 250.0 633.0 150.0 633.0 100.0 546.4 Z");
    }
}