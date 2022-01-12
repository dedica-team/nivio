package de.bonndan.nivio.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

@Configuration
@ConfigurationProperties("nivio")
@Validated
public class NivioConfigProperties {

    public static final String URL_REGEX = "^(http|https)://.*$|";

    @Pattern(regexp = URL_REGEX, message = "baseUrl must be a valid URL")
    private String baseUrl;

    private String version;

    @Min(10000)
    private Integer pollingMilliseconds;

    @Pattern(regexp = "#?[a-fA-F0-9]{6}|", message = "brandingForeground must be a hex color code")
    private String brandingForeground;

    @Pattern(regexp = "#?[a-fA-F0-9]{6}|", message = "brandingBackground must be a hex color code")
    private String brandingBackground;

    @Pattern(regexp = "#?[a-fA-F0-9]{6}|", message = "brandingSecondary must be a hex color code")
    private String brandingSecondary;

    @Pattern(regexp = URL_REGEX, message = "brandingLogoUrl must be a valid URL")
    private String brandingLogoUrl;

    @Pattern(regexp = "[a-zA-Z ]+", message = "brandingMessage must be a valid string")
    private String brandingMessage;

    private String iconFolder;

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

    public String getBrandingMessage() {
        return brandingMessage;
    }

    public void setBrandingMessage(String brandingMessage) {
        this.brandingMessage = brandingMessage;
    }

    public String getIconFolder() {
        return iconFolder;
    }

    public void setIconFolder(String iconFolder) {
        this.iconFolder = iconFolder;
    }


}
