package de.bonndan.nivio.output.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import de.bonndan.nivio.util.FrontendMapping;

import java.util.Map;

@JsonInclude
public class MappingApiModel {

    private final Map<String, String> mapping;


    public MappingApiModel(FrontendMapping frontendMapping) {
        mapping = frontendMapping.getKeys();
    }

    public Map<String, String> getMapping() {
        return mapping;
    }
}
