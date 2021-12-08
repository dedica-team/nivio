package de.bonndan.nivio.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "nivio")
@ConstructorBinding
public class SeedProperties {

    private final String seed;
    private final String demo; // add this attribute for the environment variable DEMO

    public String getSeed() {
        return seed;
    }

    public String getDemo() {
        return demo;
    }

    public SeedProperties(String seed, String demo) {
        this.seed = seed;
        this.demo = demo;
    }

}
