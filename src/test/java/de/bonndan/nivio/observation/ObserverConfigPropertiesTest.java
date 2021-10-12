package de.bonndan.nivio.observation;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
class ObserverConfigPropertiesTest {

    @Autowired
    ObserverConfigProperties observerConfigProperties;

    @Test
    void testObserver() {
        var map = Map.of("KubernetesObserver", 1);
        assertThat(observerConfigProperties.getScanDelay()).isEqualTo(map);
    }
}