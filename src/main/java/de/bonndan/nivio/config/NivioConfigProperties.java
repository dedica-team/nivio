package de.bonndan.nivio.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.net.MalformedURLException;

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

    public String getBrandingMessage() {
        return brandingMessage;
    }

    public void setBrandingMessage(String brandingMessage) {
        this.brandingMessage = brandingMessage;
    }


    public ApiModel getApiModel() {
        java.net.URL brandingLogoUrl = null;
        try {
            brandingLogoUrl = this.brandingLogoUrl != null ? new java.net.URL(getBrandingLogoUrl()) : null;
        } catch (MalformedURLException ignored) {
        }
        return new ApiModel(baseUrl, version, brandingForeground, brandingBackground, brandingSecondary, brandingLogoUrl, brandingMessage);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ApiModel {
        public final String baseUrl;
        public final String version;
        public final String brandingForeground;
        public final String brandingBackground;
        public final String brandingSecondary;
        public final java.net.URL brandingLogoUrl;
        public final String brandingMessage;


        public ApiModel(String baseUrl,
                        String version,
                        String brandingForeground,
                        String brandingBackground,
                        String brandingSecondary,
                        java.net.URL brandingLogoUrl,
                        String brandingMessage

        ) {
            this.baseUrl = baseUrl;
            this.version = version;
            this.brandingForeground = brandingForeground;
            this.brandingBackground = brandingBackground;
            this.brandingSecondary = brandingSecondary;
            this.brandingLogoUrl = brandingLogoUrl;
            this.brandingMessage = brandingMessage;

        }
    }
}
