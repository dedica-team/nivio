package de.bonndan.nivio.output.layout;

import java.awt.geom.Point2D;

/**
 * Attraction and repulsion in a graph layout.
 */
interface Forces {

    /**
     * Calculate attraction between two nodes.
     *
     * @param c1 location 1
     * @param c2 location 2
     * @param r1 radius 1
     * @param r2 radius 2
     * @return a vector with displacements (not necessary pixels)
     */
    Point2D.Double getAttraction(double[] c1, double[] c2, double r1, double r2);

    /**
     * Calculate repulsion between two nodes.
     *
     * The result is added to c1 and subtracted from c2.
     *
     * @param c1 location 1
     * @param c2 location 2
     * @param r1 radius 1
     * @param r2 radius 2
     * @return a vector with displacements (not necessary pixels)
     */
    Point2D.Double getRepulsion(double[] c1, double[] c2, double r1, double r2);

    Point2D.Double applyDisplacement(double[][] centerLocations, double[] radius, int index, double dispX, double dispY, double temperature);
}
