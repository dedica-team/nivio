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

    public Map<String, String> getLabelsToMap() {
        return labelsToMap;
    }

    public void setLabelsToMap(Map<String, String> labelsToMap) {
        this.labelsToMap = labelsToMap;
    }

}
