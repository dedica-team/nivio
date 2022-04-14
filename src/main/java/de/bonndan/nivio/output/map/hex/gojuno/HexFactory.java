package de.bonndan.nivio.output.map.hex.gojuno;

import de.bonndan.nivio.output.map.hex.Hex;
import org.springframework.lang.NonNull;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;

/**
 * Factory for {@link Hex} objects created from coordinates.
 *
 * Copied from https://github.com/gojuno/hexgrid-java/blob/master/src/main/java/com/gojuno/hexgrid/HexGrid.java
 */
public class HexFactory {

    //was layout origin
    private final Point2D.Double origin;
    private final Orientation orientation;
    private final Point2D.Double size;

    private static final HexFactory instance = new HexFactory(Orientation.FLAT, new Point2D.Double(200, 200), new Point2D.Double(Hex.HEX_SIZE, Hex.HEX_SIZE));

    public static HexFactory getInstance() {
        return instance;
    }

    HexFactory(Orientation orientation, Point2D.Double origin, Point2D.Double size) {
        this.orientation = orientation;
        this.origin = origin;
        this.size = size;
    }

    public Hex hexAt(@NonNull final Point2D.Double point) {
        double x = (point.getX() - origin.getX()) / size.getX();
        double y = (point.getY() - origin.getY()) / size.getY();
        double q = orientation.getB()[0] * x + orientation. getB()[1] * y;
        double r = orientation.getB()[2] * x + orientation.getB()[3] * y;
        return (new FractionalHex(q, r)).toHex();
    }

    public Point2D.Double hexCenter(@NonNull final Hex hex) {
        double x = (orientation.getF()[0] * hex.getQ() + orientation.getF()[1] * hex.getR()) * size.getX() + origin.getX();
        double y = (orientation.getF()[2] * hex.getQ() + orientation.getF()[3] * hex.getR()) * size.getY() + origin.getY();
        return new Point2D.Double(x, y);
    }

    public List<Point2D.Double> hexCorners(Hex hex) {
        Point2D.Double[] corners = new Point2D.Double[6];
        Point2D.Double center = hexCenter(hex);
        for (int i = 0; i < 6; i++) {
            double x = size.getX() * orientation.getCosinuses()[i] + center.getX();
            double y = size.getY() * orientation.getSinuses()[i] + center.getY();
            corners[i] = new Point2D.Double(x, y);
        }
        return Arrays.asList(corners);
    }


}