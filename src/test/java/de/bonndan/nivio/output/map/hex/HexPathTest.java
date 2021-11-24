package de.bonndan.nivio.output.map.hex;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;
import java.util.List;

import static de.bonndan.nivio.output.map.hex.Hex.SOUTH;
import static de.bonndan.nivio.output.map.hex.Hex.SOUTH_EAST;
import static org.assertj.core.api.Assertions.assertThat;

class HexPathTest {

    private List<PathTile> hexes;
    private HexPath hexpath;

    @BeforeEach
    void setup() {
        hexes = List.of(
                new PathTile(new MapTile(new Hex(0, 1))),
                new PathTile(new MapTile(new Hex(0, 2)), SOUTH),
                new PathTile(new MapTile(new Hex(0, 3)), SOUTH),
                new PathTile(new MapTile(new Hex(1, 3)), SOUTH_EAST)
        );
        hexpath = new HexPath(hexes);
    }

    @Test
    void getHexes() {
        assertThat(hexpath.getTiles()).isEqualTo(hexes);
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
    void setsDirections() {
        List<Integer> directions = hexpath.getDirections();
        assertThat(directions).isNotEmpty().hasSize(4)
                .isEqualTo(List.of(SOUTH, SOUTH, SOUTH_EAST, SOUTH_EAST));
    }
}