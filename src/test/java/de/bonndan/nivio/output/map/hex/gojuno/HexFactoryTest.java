package de.bonndan.nivio.output.map.hex.gojuno;

import de.bonndan.nivio.output.map.hex.Hex;
import org.junit.Test;

import java.awt.geom.Point2D;

import static org.junit.Assert.assertEquals;

/**
 * Copied from https://github.com/gojuno/hexgrid-java/blob/master/src/test/java/com/gojuno/hexgrid/HexGridTest.java
 */
public class HexFactoryTest {

    @Test
    public void testFlat() {
        HexFactory grid = new HexFactory(Orientation.FLAT, new Point2D.Double(10, 20), new Point2D.Double(20, 10));
        assertEquals(new Hex(0, 37), grid.hexAt(new Point2D.Double(13, 666)));
        assertEquals(new Hex(22, -11), grid.hexAt(new Point2D.Double(666, 13)));
        assertEquals(new Hex(-1, -39), grid.hexAt(new Point2D.Double(-13, -666)));
        assertEquals(new Hex(-22, 9), grid.hexAt(new Point2D.Double(-666, -13)));
    }

    private void validatePoint(Point2D.Double e, Point2D.Double r, double precision) {
        assertEquals(e.getX(), r.getX(), precision);
        assertEquals(e.getY(), r.getY(), precision);
    }

    @Test
    public void testCoordinatesFlat() {
        HexFactory grid = new HexFactory(Orientation.FLAT, new Point2D.Double(10, 20), new Point2D.Double(20, 10));
        Hex hex = grid.hexAt(new Point2D.Double(666, 666));
        validatePoint(new Point2D.Double(670.00000, 660.85880), grid.hexCenter(hex), 0.00001);
        Point2D.Double[] expectedCorners = new Point2D.Double[]{
                new Point2D.Double(690.00000, 660.85880),
                new Point2D.Double(680.00000, 669.51905),
                new Point2D.Double(660.00000, 669.51905),
                new Point2D.Double(650.00000, 660.85880),
                new Point2D.Double(660.00000, 652.19854),
                new Point2D.Double(680.00000, 652.19854)};
        Point2D.Double[] corners = grid.hexCorners(hex);
        for (int i = 0; i < 6; i++) {
            validatePoint(expectedCorners[i], corners[i], 0.00001);
        }
    }

    @Test
    public void testNeighbors() {
        HexFactory grid = new HexFactory(Orientation.FLAT, new Point2D.Double(10, 20), new Point2D.Double(20, 10));
        Hex hex = grid.hexAt(new Point2D.Double(666, 666));

        Hex[] neighbors = grid.hexNeighbors(hex, 2);
        for (int i = 0; i < neighbors.length; i++) {
            //assertEquals(expectedNeighbors[i], grid.hexToCode(neighbors[i]));
        }
    }
}