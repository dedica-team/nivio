package de.bonndan.nivio.config;

import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

@Configuration
@ConfigurationProperties("nivio")
@Validated
public class NivioConfigProperties {

    @URL(message = "baseUrl must be a valid URL")
    private String baseUrl;

    private String version;

    @Min(10000)
    private Integer pollingMilliseconds;

    @Pattern(regexp = "#?[a-fA-F0-9]{6}", message = "brandingForeground must be a hex color code")
    private String brandingForeground;

    @Pattern(regexp = "#?[a-fA-F0-9]{6}", message = "brandingBackground must be a hex color code")
    private String brandingBackground;

    @Pattern(regexp = "#?[a-fA-F0-9]{6}", message = "brandingSecondary must be a hex color code")
    private String brandingSecondary;

    @URL(message = "brandingLogoUrl must be a valid URL")
    private String brandingLogoUrl;
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

    public String getBrandingLogoUrl() {
        return brandingLogoUrl;
    }

    public void setBrandingLogoUrl(String brandingLogoUrl) {
        this.brandingLogoUrl = brandingLogoUrl;
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
