package de.bonndan.nivio.output.dto;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


class FrontendMappingApiModelTest {

    @Test
    void neverNull() {
        FrontendMappingApiModel frontendMappingApiModel = new FrontendMappingApiModel(null, null);

        assertThat(frontendMappingApiModel).isNotNull();
        assertThat(frontendMappingApiModel.getKeys()).isNotNull();
        assertThat(frontendMappingApiModel.getDescriptions()).isNotNull();
    }

    @Test
    void returnsValues() {
        FrontendMappingApiModel frontendMappingApiModel = new FrontendMappingApiModel(
                Map.of("foo", "bar"),
                Map.of("foo", "baz")
        );

        assertThat(frontendMappingApiModel).isNotNull();
        assertThat(frontendMappingApiModel.getKeys()).isNotNull().hasSize(1);
        assertThat(frontendMappingApiModel.getDescriptions()).isNotNull().hasSize(1);
    }

}