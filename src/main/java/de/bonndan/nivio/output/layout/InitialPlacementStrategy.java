package de.bonndan.nivio.output.layout;

import org.springframework.lang.NonNull;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Strategy to place components to be layouted at the beginning.
 *
 * Uses a circular arrangement to allow optimal force effects (original was a line, leading to parallel vectors).
 */
public class InitialPlacementStrategy {

    private final List<Point2D.Double> places = new ArrayList<>();

    public InitialPlacementStrategy(@NonNull final List<LayoutedComponent> bounds) {
        int size = bounds.size();
        if (size == 0) return;
        if (size == 1) {
            places.add(new Point2D.Double(0, 0));
            return;
        }

        double[] radius = new double[size];
        var sum = 0;
        for (int i = 0; i < size; i++) {
            LayoutedComponent layoutedComponent = bounds.get(i);
            radius[i] = layoutedComponent.getRadius();
            sum += radius[i];
        }
        double approxRadiusSum = sum;

        double angle = 0;
        int r = 300;
        for (int i = 0; i < size; i++) {
            Point2D.Double origin = new Point2D.Double(0, 0);
            int x = (int) Math.round(origin.x + r * Math.cos(angle));
            int y = (int) Math.round(origin.y + r * Math.sin(angle));
            places.add(new Point2D.Double(x, y));

            var share = radius[i] / approxRadiusSum;
            angle += 2 * Math.PI * share;
        }
    }

    public Point2D.Double place(int i) {
        return places.get(i);
    }
}
