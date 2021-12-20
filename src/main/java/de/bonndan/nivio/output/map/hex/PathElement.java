package de.bonndan.nivio.output.map.hex;

import de.bonndan.nivio.output.map.svg.Component;

import java.awt.geom.Point2D;

public class PathElement {

    private final String cmd;
    private final Point2D.Double pt;

    public static PathElement cmd(String cmd) {
        return new PathElement(cmd);
    }

    public static PathElement pt(Point2D.Double pt) {
        return new PathElement(pt);
    }

    PathElement(String cmd) {
        this.cmd = cmd;
        this.pt = null;
    }

    PathElement(Point2D.Double pt) {
        this.cmd = null;
        this.pt = pt;
    }

    public PathElement shift(Point2D.Double offset) {
        if (pt != null) {
            pt.x = pt.x + offset.x;
            pt.y = pt.y + offset.y;
        }
        return this;
    }

    @Override
    public String toString() {
        return cmd != null ? cmd : String.format("%s %s", Component.round(pt.getX()), Component.round(pt.getY()));
    }
}
