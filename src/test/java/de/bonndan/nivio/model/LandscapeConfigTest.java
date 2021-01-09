package de.bonndan.nivio.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LandscapeConfigTest {

    @Test
    public void testGroupLayoutConfigIsNotNull() {
        LandscapeConfig landscapeConfig = new LandscapeConfig();
        assertNotNull(landscapeConfig.getGroupLayoutConfig());
        assertNotNull(landscapeConfig.getGroupLayoutConfig().getForceConstantFactor());
        assertNotNull(landscapeConfig.getGroupLayoutConfig().getMinDistanceLimitFactor());
    }

    @Test
    public void testItemLayoutConfigIsNotNull() {
        LandscapeConfig landscapeConfig = new LandscapeConfig();
        assertNotNull(landscapeConfig.getItemLayoutConfig());
        assertNotNull(landscapeConfig.getItemLayoutConfig().getForceConstantFactor());
        assertNotNull(landscapeConfig.getItemLayoutConfig().getMinDistanceLimitFactor());
    }
}