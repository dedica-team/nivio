package de.bonndan.nivio.output.layout;

public class CollisionDetection {

    private final int minDistanceLimit;

    public CollisionDetection(int minDistanceLimit) {
        this.minDistanceLimit = minDistanceLimit;
    }

    /**
     * Movement tries full length first (i.e. jumping over nodes), reducing if collides at the endpoint
     *
     * @param index    the index of the moved node
     * @param newXDisp x displacement
     * @param newYDisp y displacement
     * @return max factor how far the move can really move without colliding
     */
    double getFreeMovementFactor(double[][] centerLocations, double[] radius, int index, double newXDisp, double newYDisp) {

        if (newXDisp == 0.0 && newYDisp == 0.0) {
            return 0;
        }

        var vertexCount = centerLocations.length;

        float factor = 1;
        double limit = 0.1;
        while (factor > limit) {
            boolean collides = false;
            for (int j = 0; j < vertexCount; j++) {

                //same or out of range, cannot collide
                if (j == index) {
                    continue;
                }

                var currentDistance = Geometry.getDistance(centerLocations[index], centerLocations[j], 0, 0, radius[index], radius[j]);
                var futureDistance = Geometry.getDistance(
                        centerLocations[index],
                        centerLocations[j],
                        newXDisp * factor,
                        newYDisp * factor,
                        radius[index],
                        radius[j]
                );

                //on collision, we reduce the movement by a percentage
                if (futureDistance < minDistanceLimit && currentDistance>futureDistance) {
                    collides = true;
                    break;
                }
            }
            if (collides) {
                factor *= 0.85;
            } else {
                return factor;
            }
        }

        if (factor <= limit)
            return 0;
        return factor;
    }


}
