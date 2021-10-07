package de.bonndan.nivio.observation;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ObserverConfigTest {

    @Test
    void testObserver() {
        var map = Map.of("test", 1);
        var observerConfig = new ObserverConfig(map);
        assertThat(observerConfig.getDelay()).isEqualTo(map);
    }
}