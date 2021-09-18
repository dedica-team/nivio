package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.output.map.hex.Hex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.bonndan.nivio.output.map.hex.Hex.*;
import static org.assertj.core.api.Assertions.assertThat;

class HexPathTest {

    private List<Hex> hexes;
    private HexPath hexpath;

    @BeforeEach
    void setup() {
        hexes = List.of(
                new Hex(0,1),
                new Hex(0,2),
                new Hex(0,3),
                new Hex(1,3)
        );
        hexpath = new HexPath(hexes);
    }

    @Test
    void getHexes() {
        assertThat(hexpath.getHexes()).isEqualTo(hexes);
    }

    @Test
    void getPoints() {

        assertThat(hexpath.getPoints()).isNotEmpty().hasSize(29);
    }

    @Test
    void getEndPoint() {
        assertThat(hexpath.getEndPoint()).isEqualTo(new Point2D.Double(278.57142857142856, 764.9784777069909));
    }

    @Test
    void calcBends() {
        List<Hex> bends = hexpath.getBends();
        assertThat(bends).isNotEmpty()
                .hasSize(1)
                .contains(new Hex(0,3));
    }

    @Test
    void setsDirections() {
        List<Integer> directions = hexpath.getHexes().stream().map(Hex::getPathDirection).collect(Collectors.toList());
        assertThat(directions).isNotEmpty().hasSize(4)
                .isEqualTo(List.of(SOUTH, SOUTH, SOUTH_EAST, SOUTH_EAST));
    }
}