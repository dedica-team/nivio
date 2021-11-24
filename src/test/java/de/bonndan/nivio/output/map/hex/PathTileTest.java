package de.bonndan.nivio.output.map.hex;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PathTileTest {

    @Test
    void testEquals() {
         PathTile one = new PathTile(new MapTile(new Hex(0,0)));
         PathTile two = new PathTile(new MapTile(new Hex(0,0)));

         assertThat(one).isEqualTo(two);
    }
}