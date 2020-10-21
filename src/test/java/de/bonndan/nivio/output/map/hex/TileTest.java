package de.bonndan.nivio.output.map.hex;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TileTest {

    @Test
    void noExtraCost() {
        Hex fromHex = new Hex(3,2);
        Tile from = new Tile(fromHex);
        from.moveCosts = 1.0f;

        Hex toHex = new Hex(3,3);
        Tile to = new Tile(toHex);

        //when
        float costs = to.calcMoveCostsFrom(from);
        assertEquals(2.0, costs);
    }

    @Test
    void groupCostMore() {
        Hex fromHex = new Hex(3,2);
        Tile from = new Tile(fromHex);
        from.moveCosts = 1.0f;

        Hex toHex = new Hex(3,3);
        toHex.group = "foo/bar";
        Tile to = new Tile(toHex);

        //when
        float costs = to.calcMoveCostsFrom(from);
        assertEquals(4.0, costs);
    }

    @Test
    void itemsBlock() {
        Hex fromHex = new Hex(3,2);
        Tile from = new Tile(fromHex);
        from.moveCosts = 1.0f;

        Hex toHex = new Hex(3,3);
        toHex.item = "foo/bar/baz";
        toHex.group = "foo/bar";
        Tile to = new Tile(toHex);

        //when
        float costs = to.calcMoveCostsFrom(from);
        assertEquals(11.0, costs);
    }
}