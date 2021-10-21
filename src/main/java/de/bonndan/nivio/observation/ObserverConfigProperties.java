package de.bonndan.nivio.observation;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "observerconfig")
@Validated
public class ObserverConfigProperties {

    private Map<String, Integer> scanDelay;

    public Map<String, Integer> getScanDelay() {
        return scanDelay;
    }

    public void setScanDelay(Map<String, Integer> scanDelay) {
        this.scanDelay = scanDelay;
    }
}
