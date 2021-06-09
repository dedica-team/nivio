package de.bonndan.nivio.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "nivio")
@Validated
public class ApplicationProperties {

    private String seed; // define a new attribute of the environment variable SEED

    public String getSeed() {
        return seed;
    }

    public void setSeed(String seed) {
        this.seed = seed;
    }

}
