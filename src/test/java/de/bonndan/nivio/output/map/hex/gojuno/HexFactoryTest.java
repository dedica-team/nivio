package de.bonndan.nivio.output.map.hex.gojuno;

import de.bonndan.nivio.output.map.hex.Hex;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Copied from https://github.com/gojuno/hexgrid-java/blob/master/src/test/java/com/gojuno/hexgrid/HexGridTest.java
 */
class HexFactoryTest {

    @Test
    void testFlat() {
        HexFactory grid = new HexFactory(Orientation.FLAT, new Point2D.Double(10, 20), new Point2D.Double(20, 10));
        assertEquals(new Hex(0, 37), grid.hexAt(new Point2D.Double(13, 666)));
        assertEquals(new Hex(22, -11), grid.hexAt(new Point2D.Double(666, 13)));
        assertEquals(new Hex(-1, -39), grid.hexAt(new Point2D.Double(-13, -666)));
        assertEquals(new Hex(-22, 9), grid.hexAt(new Point2D.Double(-666, -13)));
    }

    @Test
    void testCoordinatesFlat() {
        HexFactory grid = new HexFactory(Orientation.FLAT, new Point2D.Double(10, 20), new Point2D.Double(20, 10));
        Hex hex = grid.hexAt(new Point2D.Double(666, 666));
        Point2D.Double e = new Point2D.Double(670.00000, 660.85880);
        Point2D.Double r1 = grid.hexCenter(hex);
        assertEquals(e.getX(), r1.getX(), 0.00001);
        assertEquals(e.getY(), r1.getY(), 0.00001);

        Point2D.Double[] expectedCorners = new Point2D.Double[]{
                new Point2D.Double(690.00000, 660.85880),
                new Point2D.Double(680.00000, 669.51905),
                new Point2D.Double(660.00000, 669.51905),
                new Point2D.Double(650.00000, 660.85880),
                new Point2D.Double(660.00000, 652.19854),
                new Point2D.Double(680.00000, 652.19854)};
        var corners = grid.hexCorners(hex);
        for (int i = 0; i < 6; i++) {
            Point2D.Double r = corners.get(i);
            assertEquals(expectedCorners[i].getX(), r.getX(), 0.00001);
            assertEquals(expectedCorners[i].getY(), r.getY(), 0.00001);
        }
    }

}