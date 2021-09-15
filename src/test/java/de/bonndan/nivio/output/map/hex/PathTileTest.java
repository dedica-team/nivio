package de.bonndan.nivio.output.map.hex;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PathTileTest {

    @Test
    void noExtraCost() {
        Hex fromHex = new Hex(3,2);
        PathTile from = new PathTile(fromHex);
        from.moveCosts = 1;

        Hex toHex = new Hex(3,3);
        PathTile to = new PathTile(toHex);

        //when
        int costs = to.calcMoveCostsFrom(from);
        assertEquals(2, costs);
    }

    @Test
    void groupCostMore() {
        Hex fromHex = new Hex(3,2);
        PathTile from = new PathTile(fromHex);
        from.moveCosts = 1;

        Hex toHex = new Hex(3,3);
        toHex.group = "foo/bar";
        PathTile to = new PathTile(toHex);

        //when
        int costs = to.calcMoveCostsFrom(from);
        assertEquals(4, costs);
    }

    @Test
    void itemsBlock() {
        Hex fromHex = new Hex(3,2);
        PathTile from = new PathTile(fromHex);
        from.moveCosts = 1;

        Hex toHex = new Hex(3,3);
        toHex.item = "foo/bar/baz";
        toHex.group = "foo/bar";
        PathTile to = new PathTile(toHex);

        //when
        int costs = to.calcMoveCostsFrom(from);
        assertEquals(1001, costs);
    }
}
