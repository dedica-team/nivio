package de.bonndan.nivio.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "mapping")
@Validated
public class FrontendMapping {
    private Map<String, String> labelsToMap;

    private Map<String, String> labelsToDescription;

    public Map<String, String> getLabelsToMap() {
        return labelsToMap;
    }

    public Map<String, String> getLabelsToDescription() {
        return labelsToDescription;
    }

    public void setLabelsToMap(Map<String, String> labelsToMap) {
        this.labelsToMap = labelsToMap;
    }

    public void setLabelsToDescription(Map<String, String> labelsToDescription) {
        this.labelsToDescription = labelsToDescription;
    }

}
