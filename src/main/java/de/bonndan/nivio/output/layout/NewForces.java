package de.bonndan.nivio.output.layout;

import java.awt.geom.Point2D;

class NewForces implements Forces {

    private final int minDistanceLimit;
    private final int maxDistanceLimit;
    private final CollisionDetection collisionDetection;

    NewForces(int minDistanceLimit, int maxDistanceLimit) {
        this.minDistanceLimit = minDistanceLimit;
        this.maxDistanceLimit = maxDistanceLimit;
        collisionDetection = new CollisionDetection(minDistanceLimit);
    }

    public Point2D.Double getAttraction(double[] c1, double[] c2, double r1, double r2) {
        double xDelta = c1[0] - c2[0];
        double yDelta = c1[1] - c2[1];
        double distance = xDelta * xDelta + yDelta * yDelta - (r1 * r1) - (r2 * r2);

        if (distance <= minDistanceLimit) {
            return new Point2D.Double(0, 0);
        }

        var totalDisplacement = Math.min(maxDistanceLimit, distance / minDistanceLimit * minDistanceLimit);
        double v = Math.sqrt(xDelta * xDelta + yDelta * yDelta);
        double displacementX = Math.abs(xDelta) / v * totalDisplacement;
        double displacementY = Math.abs(yDelta) / v * totalDisplacement;

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
        double distanceWithRadius = betweenCenters - r1 -  r2;
        if (distanceWithRadius > maxDistanceLimit) {
            // Ignore vertices too far apart
            return new Point2D.Double(0, 0);
        }

        var dir = -1;
        if (distanceWithRadius < 0) {
            distanceWithRadius = 0.0001;
        }

        var totalDisplacement = Math.min(maxDistanceLimit, minDistanceLimit / distanceWithRadius * maxDistanceLimit);
        double displacementX = Math.abs(yDelta) / betweenCenters * totalDisplacement * dir;
        double displacementY = Math.abs(xDelta) / betweenCenters * totalDisplacement * dir;

        return new Point2D.Double(displacementX, displacementY);
    }

    public Point2D.Double applyDisplacement(double[][] centerLocations, double[] radius, int index, double dispX, double dispY, double temperature) {
        double deltaLength = Geometry.getDistance(dispX, dispY);

        if (deltaLength < 0.001) {
            deltaLength = 0.001;
        }

        // Scale down by the current temperature if less than the
        // displacement distance
        double newXDisp = dispX * Math.min(deltaLength, temperature);
        double newYDisp = dispY * Math.min(deltaLength, temperature);

        // apply collision detection
        double freeMovementFactor = collisionDetection.getFreeMovementFactor(centerLocations, radius, index, newXDisp, newYDisp);
        newXDisp *= freeMovementFactor;
        newYDisp *= freeMovementFactor;

        return new Point2D.Double(newXDisp, newYDisp);
    }
}
