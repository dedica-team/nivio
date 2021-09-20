package de.bonndan.nivio.output.layout;

import org.springframework.lang.NonNull;

import java.awt.geom.Point2D;
import java.util.List;

/**
 * Strategy to place components to be layouted at the beginning.
 *
 * Uses a circular arrangement to allow optimal force effects (original was a line, leading to parallel vectors).
 */
public class InitialPlacementStrategy {

    private final Point2D.Double origin = new Point2D.Double(0, 0);
    private final double[] approxRadius;
    private final double approxRadiusSum;

    public InitialPlacementStrategy(@NonNull final List<LayoutedComponent> bounds) {
        approxRadius = new double[bounds.size()];
        var sum = 0;
        for (int i = 0, boundsSize = bounds.size(); i < boundsSize; i++) {
            LayoutedComponent layoutedComponent = bounds.get(i);
            double width = layoutedComponent.getWidth();
            double height = layoutedComponent.getHeight();
            approxRadius[i] = Math.max(width, height);
            sum += approxRadius[i];
        }
        approxRadiusSum = sum;
    }

    public Point2D.Double place(int i) {

        var share = approxRadius[i] / approxRadiusSum;
        double slice = 2 * Math.PI * i / share;

        int x = (int) Math.round(origin.x + approxRadius[i] * Math.cos(slice));
        int y = (int) Math.round(origin.y + approxRadius[i] * Math.sin(slice));

        return new Point2D.Double(x, y);
    }
}
