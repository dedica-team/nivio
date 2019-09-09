package de.bonndan.nivio.landscape;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LandscapeConfigTest {

    @Test
    public void testJgraphXConfigIsNotNull() {
        LandscapeConfig landscapeConfig = new LandscapeConfig();
        assertNotNull(landscapeConfig.getJgraphx());
        assertNotNull(landscapeConfig.getJgraphx().getBorderLineCostFactor());
        assertNotNull(landscapeConfig.getJgraphx().getEdgeLengthCostFactor());
        assertNotNull(landscapeConfig.getJgraphx().getNodeDistributionCostFactor());
        assertNotNull(landscapeConfig.getJgraphx().getTriesPerCell());
    }
}