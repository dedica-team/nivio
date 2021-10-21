package de.bonndan.nivio.output.layout;

class ForceFactory {

    private ForceFactory(){}

    static Forces getForces(int minDistanceLimit, int maxDistanceLimit, int forceConstant) {
        return new OriginalForces(minDistanceLimit,  maxDistanceLimit, forceConstant);
    }

}
