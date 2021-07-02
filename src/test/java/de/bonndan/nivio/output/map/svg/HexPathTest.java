package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.output.map.hex.Hex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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
        assertThat(hexpath.getEndPoint()).isEqualTo(new Point2D.Double(275.0, 762.9165124598851));
    }

    @Test
    void calcBends() {
        List<Hex> bends = HexPath.calcBends(this.hexes);
        assertThat(bends).isNotEmpty().hasSize(1);
        assertThat(bends).contains(new Hex(0,3));
    }
}