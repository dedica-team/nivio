package de.bonndan.nivio.output.layout;

import java.awt.geom.Point2D;
import java.util.List;

/**
 * Strategy to place components to be layouted at the beginning.
 *
 * Uses a circular arrangement to allow optimal force effects (original was a line, leading to parallel vectors).
 */
public class InitialPlacementStrategy {

    private final List<LayoutedComponent> bounds;
    private final Point2D.Double origin = new Point2D.Double(0, 0);
    private final int radius = 50;

    public InitialPlacementStrategy(List<LayoutedComponent> bounds) {
        this.bounds = bounds;
    }

    public Point2D.Double place(int i) {

        double t = 2 * Math.PI * i / bounds.size();
        int x = (int) Math.round(origin.x + radius * Math.cos(t));
        int y = (int) Math.round(origin.y + radius * Math.sin(t));

        return new Point2D.Double(x, y);
    }
}
