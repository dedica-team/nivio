package de.bonndan.nivio.output.map.hex;

import de.bonndan.nivio.output.map.svg.Component;
import org.springframework.lang.NonNull;

import java.awt.geom.Point2D;
import java.util.Objects;

public class PathElement {

    private final String cmd;
    private final Point2D.Double pt;

    public static PathElement cmd(String cmd) {
        return new PathElement(cmd);
    }

    public static PathElement pt(Point2D.Double pt) {
        return new PathElement(pt);
    }

    PathElement(@NonNull final String cmd) {
        this.cmd = Objects.requireNonNull(cmd);
        this.pt = null;
    }

    PathElement(@NonNull final Point2D.Double pt) {
        this.cmd = null;
        this.pt = Objects.requireNonNull(pt);
    }

    public String shifted(@NonNull final Point2D.Double offset) {
        if (cmd != null) {
            return cmd;
        }

        Objects.requireNonNull(pt);
        return String.format("%s %s", Component.round(pt.x + offset.x), Component.round(pt.y + offset.y));
    }

    @Override
    public String toString() {
        return cmd != null ? cmd : String.format("%s %s", Component.round(pt.getX()), Component.round(pt.getY()));
    }
}
