package de.bonndan.nivio.output.map.hex;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MapTileTest {

    @Test
    void incrementPortCount() {
        MapTile mapTile = new MapTile(new Hex(0, 0));
        assertThat(mapTile.incrementPortCount()).isEqualTo(0);
        assertThat(mapTile.incrementPortCount()).isEqualTo(1);
    }
}