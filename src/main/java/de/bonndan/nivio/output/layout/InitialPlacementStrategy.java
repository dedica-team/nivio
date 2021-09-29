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
        double[] diameter = new double[bounds.size()];
        var sum = 0;
        for (int i = 0, boundsSize = bounds.size(); i < boundsSize; i++) {
            LayoutedComponent layoutedComponent = bounds.get(i);
            double width = layoutedComponent.getWidth();
            double height = layoutedComponent.getHeight();
            diameter[i] = Math.max(width, height); //using diameter
            sum += diameter[i];
        }
        double approxRadiusSum = sum;

        double angle = 0;
        for (int i = 0, boundsSize = bounds.size(); i < boundsSize; i++) {
            Point2D.Double origin = new Point2D.Double(0, 0);
            int x = (int) Math.round(origin.x + diameter[i] * Math.cos(angle));
            int y = (int) Math.round(origin.y + diameter[i] * Math.sin(angle));
            places.add(new Point2D.Double(x, y));

            var share = diameter[i] / approxRadiusSum;
            angle += 2 * Math.PI * share;
        }
    }

    public Point2D.Double place(int i) {
        return places.get(i);
    }
}
