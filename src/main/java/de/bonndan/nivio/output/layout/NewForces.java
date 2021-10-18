package de.bonndan.nivio.output.layout;

import java.awt.geom.Point2D;

class NewForces implements Forces {

    private final int minDistanceLimit;
    private final int maxDistanceLimit;
    private final int initialTemperature;
    private final CollisionDetection collisionDetection;

    /**
     * @param minDistanceLimit   minimum distance to be kept in units/pixels
     * @param maxDistanceLimit   distance in units/pixels of max movement and repulsion limit
     * @param initialTemperature temp at start
     */
    NewForces(int minDistanceLimit, int maxDistanceLimit, int initialTemperature) {
        this.minDistanceLimit = minDistanceLimit;
        this.maxDistanceLimit = maxDistanceLimit;
        collisionDetection = new CollisionDetection(minDistanceLimit);
        this.initialTemperature = initialTemperature;
    }

    public Point2D.Double getAttraction(double[] c1, double[] c2, double r1, double r2) {
        double xDelta = c1[0] - c2[0];
        double yDelta = c1[1] - c2[1];
        double distance = Geometry.getDistance(xDelta, yDelta);

        if (distance <= minDistanceLimit) {
            return new Point2D.Double(0, 0);
        }

        var totalDisplacement = Math.min(maxDistanceLimit, distance / minDistanceLimit * minDistanceLimit) / 2;
        double displacementX = Math.abs(xDelta) / distance * totalDisplacement;
        double displacementY = Math.abs(yDelta) / distance * totalDisplacement;

        if (xDelta > 0)
            displacementX *= -1;
        if (yDelta > 0)
            displacementY *= -1;

        return new Point2D.Double(displacementX, displacementY);
    }

    public Point2D.Double getRepulsion(double[] c1, double[] c2, double r1, double r2) {
        double xDelta = c1[0] - c2[0];
        double yDelta = c1[1] - c2[1];
        double betweenCenters = Geometry.getDistance(xDelta, yDelta);
        double distanceWithRadius = betweenCenters - r1 - r2;
        if (distanceWithRadius > maxDistanceLimit) {
            // Ignore vertices too far apart
            return new Point2D.Double(0, 0);
        }

        if (distanceWithRadius < 0) {
            distanceWithRadius = 0.0001;
        }

        var totalDisplacement = Math.min(maxDistanceLimit, minDistanceLimit / distanceWithRadius * maxDistanceLimit);
        double displacementX = Math.abs(yDelta) / betweenCenters * totalDisplacement;
        double displacementY = Math.abs(xDelta) / betweenCenters * totalDisplacement;

        return new Point2D.Double(displacementX, displacementY);
    }

    public Point2D.Double applyDisplacement(double[][] centerLocations, double[] radius, int index, double dispX, double dispY, double temperature) {
        double deltaLength = Geometry.getDistance(dispX, dispY);

        if (deltaLength < 0.001) {
            deltaLength = 0.001;
        }

        // Scale down by the current temperature if less than the
        // displacement distance
        double newXDisp = dispX * temperature / initialTemperature;
        double newYDisp = dispY * temperature / initialTemperature;

        // apply collision detection
        double freeMovementFactor = collisionDetection.getFreeMovementFactor(centerLocations, radius, index, newXDisp, newYDisp);
        newXDisp *= freeMovementFactor;
        newYDisp *= freeMovementFactor;

        return new Point2D.Double(newXDisp, newYDisp);
    }
}
