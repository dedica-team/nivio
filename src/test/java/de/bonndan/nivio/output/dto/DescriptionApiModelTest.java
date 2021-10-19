package de.bonndan.nivio.output.dto;

import de.bonndan.nivio.util.FrontendMapping;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DescriptionApiModelTest {

    @Test
    void getDescription() {
        FrontendMapping frontendMapping = Mockito.mock(FrontendMapping.class);
        Mockito.when(frontendMapping.getDescription()).thenReturn(Map.of("testKey", "testValue"));
        var descriptionApiModel = new DescriptionApiModel(frontendMapping);
        assertThat(descriptionApiModel.getDescription()).isEqualTo(Map.of("testKey", "testValue"));
    }
}