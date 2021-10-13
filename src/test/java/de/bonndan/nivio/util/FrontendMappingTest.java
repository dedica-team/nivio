package de.bonndan.nivio.util;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FrontendMappingTest {

    @Autowired
    FrontendMapping frontendMapping;

    @Test
    @Order(1)
    void getLabelsToMap() {
        var testMap = Map.of("shortname", "short name", "END_OF_LIFE", "end of life");
        assertThat(frontendMapping.getLabelsToMap()).isEqualTo(testMap);
    }

    @Test
    @Order(2)
    void setLabelsToMap() {
        var testMap = Map.of("testKey", "testValue");
        frontendMapping.setLabelsToMap(testMap);
        assertThat(frontendMapping.getLabelsToMap()).isEqualTo(testMap);
    }
}