package de.bonndan.nivio.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.bonndan.nivio.security.SecurityConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

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

    @NotEmpty
    @Pattern(regexp = "none|optional|required", message = "Login mode must be one of none|optional|required")
    private String loginMode = SecurityConfig.LOGIN_MODE_NONE;

    private List<String> allowedOriginPatterns;

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

    public String getLoginMode() {
        return loginMode;
    }

    public void setLoginMode(String loginMode) {
        this.loginMode = loginMode;
    }

    public ApiModel getApiModel() {
        java.net.URL url = null;
        try {
            url = this.brandingLogoUrl != null ? new java.net.URL(getBrandingLogoUrl()) : null;
        } catch (MalformedURLException ignored) {
        }
        return new ApiModel(baseUrl, version, brandingForeground, brandingBackground, brandingSecondary, url, brandingMessage, loginMode);
    }

    public List<String> getAllowedOriginPatterns() {
        return allowedOriginPatterns.stream().filter(StringUtils::hasLength).collect(Collectors.toList());
    }

    public void setAllowedOriginPatterns(String allowedOriginPatterns) {
        this.allowedOriginPatterns = List.of(allowedOriginPatterns.split(";"));
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ApiModel {
        public final String baseUrl;
        public final String version;
        public final String brandingForeground;
        public final String brandingBackground;
        public final String brandingSecondary;
        public final URL brandingLogoUrl;
        public final String brandingMessage;
        public final String loginMode;

        public ApiModel(String baseUrl,
                        String version,
                        String brandingForeground,
                        String brandingBackground,
                        String brandingSecondary,
                        URL brandingLogoUrl,
                        String brandingMessage,
                        String loginMode
        ) {
            this.baseUrl = baseUrl;
            this.version = version;
            this.brandingForeground = brandingForeground;
            this.brandingBackground = brandingBackground;
            this.brandingSecondary = brandingSecondary;
            this.brandingLogoUrl = brandingLogoUrl;
            this.brandingMessage = brandingMessage;
            this.loginMode = loginMode;
        }
    }
}
