package de.bonndan.nivio.input.kubernetes;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class K8sConfigTest {
    @Test
    void testConstructor() {
        var testConfig = new K8sConfig(true, 2, Map.of());
        assertThat(testConfig.isActive()).isTrue();
        assertThat(testConfig.getMinMatchingLabel()).isEqualTo(2);
        assertThat(testConfig.getLevel()).isEmpty();
    }
}