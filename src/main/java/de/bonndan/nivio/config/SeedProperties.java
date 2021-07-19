package de.bonndan.nivio.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Optional;

@ConfigurationProperties(prefix = "nivio")
public class SeedProperties {

    private String seed;
    private String demo; // add this attribute for the environment variable DEMO

    public String getSeed() {
        return seed;
    }

    public void setSeed(String seed) {
        this.seed = seed;
    }

    public String getDemo(){return demo;}

    public void setDemo(String demo){this.demo = demo;}
}
