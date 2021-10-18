package de.bonndan.nivio.output.layout;

class ForceFactory {

    private ForceFactory(){}

    static Forces getForces(int minDistanceLimit, int maxDistanceLimit, int forceConstant, int initialTemp) {
        return new OriginalForces(minDistanceLimit,  maxDistanceLimit, forceConstant, initialTemp);
    }

    static Forces getNewForces(int minDistanceLimit, int maxDistanceLimit, int initialTemperature) {
        return new NewForces(minDistanceLimit,  maxDistanceLimit, initialTemperature);
    }
}
