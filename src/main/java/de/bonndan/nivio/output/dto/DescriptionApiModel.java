package de.bonndan.nivio.output.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import de.bonndan.nivio.util.FrontendMapping;

import java.util.Map;

@JsonInclude
public class DescriptionApiModel {


    private final Map<String, String> description;

    public DescriptionApiModel(FrontendMapping frontendMapping) {
        this.description = frontendMapping.getLabelsToDescription();
    }

    public Map<String, String> getDescription() {
        return description;
    }
}
