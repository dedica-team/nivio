package de.bonndan.nivio.output.layout;

public class Geometry {

    private Geometry() {}
    /**
     * @param origin       point
     * @param target       point
     * @param newXDisp     x displacement
     * @param newYDisp     y displacement
     * @param radiusOrigin r1
     * @param radiusTarget r2
     * @return absolute distance
     */
    static double getDistance(double[] origin, double[] target, double newXDisp, double newYDisp, double radiusOrigin, double radiusTarget) {
        var future0 = origin[0] + newXDisp;
        var future1 = origin[1] + newYDisp;
        var xDelta = future0 - target[0];
        var yDelta = future1 - target[1];
        return Math.sqrt((xDelta * xDelta) + (yDelta * yDelta)) - radiusOrigin - radiusTarget;
    }

    static double getDistance(double xDelta, double yDelta) {
        return Math.sqrt((xDelta * xDelta) + (yDelta * yDelta));
    }
}
