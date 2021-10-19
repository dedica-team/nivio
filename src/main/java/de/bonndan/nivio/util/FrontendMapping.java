package de.bonndan.nivio.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "frontendmapping")
@Validated
public class FrontendMapping {
    private Map<String, String> keys;

    private Map<String, String> descriptions;

    public Map<String, String> getKeys() {
        return keys;
    }

    public Map<String, String> getDescriptions() {
        return descriptions;
    }

    public void setKeys(Map<String, String> keys) {
        this.keys = keys;
    }

    public void setDescriptions(Map<String, String> descriptions) {
        this.descriptions = descriptions;
    }

}
