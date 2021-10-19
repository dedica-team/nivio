package de.bonndan.nivio.output.dto;

import de.bonndan.nivio.util.FrontendMapping;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class MappingApiModelTest {
    @Test
    void getMapping() {
        FrontendMapping frontendMapping = Mockito.mock(FrontendMapping.class);
        Mockito.when(frontendMapping.getLabelsToMap()).thenReturn(Map.of("testKey", "testValue"));
        var mappingApiModel = new MappingApiModel(frontendMapping);
        assertThat(mappingApiModel.getMapping()).isEqualTo(Map.of("testKey", "testValue"));
    }
}