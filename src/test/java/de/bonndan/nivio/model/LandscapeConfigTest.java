package de.bonndan.nivio.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LandscapeConfigTest {

    @Test
    public void testJgraphXConfigIsNotNull() {
        LandscapeConfig landscapeConfig = new LandscapeConfig();
        assertNotNull(landscapeConfig.getJgraphx());
        assertNotNull(landscapeConfig.getJgraphx().getForceConstantFactor());
        assertNotNull(landscapeConfig.getJgraphx().getMinDistanceLimitFactor());
    }
}