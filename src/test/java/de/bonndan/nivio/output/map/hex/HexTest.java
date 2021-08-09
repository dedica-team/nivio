package de.bonndan.nivio.output.map.hex;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HexTest {

    @Test
    void topLeft() {
        Hex center = new Hex(0,0, 0);
        List<Hex> area = new ArrayList<>();
        area.add(center);
        area.addAll(center.neighbours());

        //when
        Hex hex = Hex.topLeft(area);

        //then
        assertThat(hex).isNotNull().isEqualTo(new Hex(3,2));
    }

    @Test
    void getNeighboursDirections() {
        //given
        Hex center = new Hex(4,3);
        List<Hex> neighbours = center.neighbours();

        //when
        assertThat(center.getDirectionTo(neighbours.get(0))).isEqualTo(Hex.SOUTH_EAST);
    }
}