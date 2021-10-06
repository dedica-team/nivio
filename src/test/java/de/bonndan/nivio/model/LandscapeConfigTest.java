package de.bonndan.nivio.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LandscapeConfigTest {

    @Test
    void testGroupLayoutConfigIsNotNull() {
        LandscapeConfig landscapeConfig = new LandscapeConfig();
        assertNotNull(landscapeConfig.getGroupLayoutConfig());
        assertNotNull(landscapeConfig.getGroupLayoutConfig().getMinDistanceLimitFactor());
    }

    @Test
    void testItemLayoutConfigIsNotNull() {
        LandscapeConfig landscapeConfig = new LandscapeConfig();
        assertNotNull(landscapeConfig.getItemLayoutConfig());
        assertNotNull(landscapeConfig.getItemLayoutConfig().getMinDistanceLimitFactor());
    }
}