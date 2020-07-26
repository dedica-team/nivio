package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.output.map.hex.Hex;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SVGGroupAreaFactoryTest {

    /**
     * https://www.redblobgames.com/grids/hexagons/#coordinates
     */
    @Test
    public void getBridges() {
        Set<Hex> inArea = new HashSet<>();
        //vertical with one hex gap
        inArea.add(new Hex(3, 1, -4));
        inArea.add(new Hex(3, 3, -6));

        //when
        Set<Hex> bridges = SVGGroupAreaFactory.getBridges(inArea);
        assertEquals(1, bridges.size());
        assertEquals(new Hex(3,2,-5), bridges.iterator().next());
    }
}