package de.bonndan.nivio.output.layout;

import java.awt.geom.Point2D;

/**
 * The original forces used in {@link FastOrganicLayout}.
 *
 *
 */
class OriginalForces implements Forces {

    private final int minDistanceLimit;
    private final int minDistanceLimitSquared;
    private final int maxDistanceLimit;

    /**
     * The force constant by which the attractive forces are divided and the
     * repulsive forces are multiple by the square of. The value equates to the
     * average radius there is of free space around each node. Default is 50.
     */
    private final int forceConstant;

    OriginalForces(int minDistanceLimit, int maxDistanceLimit, int forceConstant, int initialTemp) {
        this.minDistanceLimit = minDistanceLimit;
        this.minDistanceLimitSquared = minDistanceLimit * minDistanceLimit;
        this.maxDistanceLimit = maxDistanceLimit;
        this.forceConstant = forceConstant;
    }

    public Point2D.Double getAttraction(double[] c1, double[] c2, double r1, double r2) {
        double xDelta = c1[0] - c2[0];
        double yDelta = c1[1] - c2[1];

        // The distance between the nodes
        double deltaLengthSquared = xDelta * xDelta + yDelta * yDelta - (r1*r1) - (r2*r2);

        if (deltaLengthSquared < minDistanceLimitSquared) {
            deltaLengthSquared = minDistanceLimitSquared;
        }

        double deltaLength = Math.sqrt(deltaLengthSquared);
        double force = (deltaLengthSquared) / forceConstant;

        double displacementX = (xDelta / deltaLength) * force;
        double displacementY = (yDelta / deltaLength) * force;

        return new Point2D.Double(displacementX, displacementY);
    }

    public Point2D.Double getRepulsion(double[] c1, double[] c2, double r1, double r2) {
        double xDelta = c1[0] - c2[0];
        double yDelta = c1[1] - c2[1];

        if (xDelta == 0) {
            xDelta = 0.01;
        }

        if (yDelta == 0) {
            yDelta = 0.01;
        }

        // Distance between nodes
        double deltaLength = Geometry.getDistance(xDelta, yDelta);

        double deltaLengthWithRadius = deltaLength - r1 - r2;

        if (deltaLengthWithRadius > maxDistanceLimit) {
            // Ignore vertices too far apart
            return new Point2D.Double(0,0);
        }

        if (deltaLengthWithRadius < minDistanceLimit) {
            deltaLengthWithRadius = minDistanceLimit;
        }

        double force = forceConstant*forceConstant / deltaLengthWithRadius;

        double displacementX = (xDelta / deltaLength) * force;
        double displacementY = (yDelta / deltaLength) * force;

        return new Point2D.Double(displacementX, displacementY);
    }

    public Point2D.Double applyDisplacement(double[][] centerLocations, double[]radius, int index, double dispX, double dispY, double temperature) {
        double deltaLength = Geometry.getDistance(dispX, dispY);

        if (deltaLength < 0.001) {
            deltaLength = 0.001;
        }

        // Scale down by the current temperature if less than the
        // displacement distance
        double newXDisp = dispX / deltaLength * Math.min(deltaLength, temperature);
        double newYDisp = dispY / deltaLength * Math.min(deltaLength, temperature);

        return new Point2D.Double(newXDisp, newYDisp);
    }
}
