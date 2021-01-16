package de.bonndan.nivio.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("nivio")
public class NivioConfigProperties {

    private String baseUrl;
    private String version;
    private Integer pollingMilliseconds;
    private String brandingForeground;
    private String brandingBackground;
    private String brandingSecondary;
    //iconFolder: /a/local/path


    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getPollingMilliseconds() {
        return pollingMilliseconds;
    }

    public void setPollingMilliseconds(Integer pollingMilliseconds) {
        this.pollingMilliseconds = pollingMilliseconds;
    }

    public String getBrandingForeground() {
        return brandingForeground;
    }

    public void setBrandingForeground(String brandingForeground) {
        this.brandingForeground = brandingForeground;
    }

    public String getBrandingBackground() {
        return brandingBackground;
    }

    public void setBrandingBackground(String brandingBackground) {
        this.brandingBackground = brandingBackground;
    }

    public String getBrandingSecondary() {
        return brandingSecondary;
    }

    public void setBrandingSecondary(String brandingSecondary) {
        this.brandingSecondary = brandingSecondary;
    }

    public ApiModel getApiModel() {
        return new ApiModel(baseUrl, version, brandingForeground, brandingBackground, brandingSecondary);
    }

    public static class ApiModel {
        public final String baseUrl;
        public final String version;
        public final String brandingForeground;
        public final String brandingBackground;
        public final String brandingSecondary;

        public ApiModel(String baseUrl, String version, String brandingForeground, String brandingBackground, String brandingSecondary) {
            this.baseUrl = baseUrl;
            this.version = version;
            this.brandingForeground = brandingForeground;
            this.brandingBackground = brandingBackground;
            this.brandingSecondary = brandingSecondary;
        }
    }
}
