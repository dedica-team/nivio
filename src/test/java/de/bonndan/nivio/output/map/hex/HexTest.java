package de.bonndan.nivio.output.map.hex;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HexTest {

    @Test
    public void topLeft() {
        Hex center = new Hex(0,0, 0);
        List<Hex> area = new ArrayList<>();
        area.add(center);
        area.addAll(center.neighbours());

        //when
        Hex hex = Hex.topLeft(area);

        //then
        assertThat(hex).isNotNull();
        assertThat(hex.q).isEqualTo(0);
        assertThat(hex.r).isEqualTo(-1);
    }

}