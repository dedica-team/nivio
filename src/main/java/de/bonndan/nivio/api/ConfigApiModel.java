package de.bonndan.nivio.api;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.net.URL;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConfigApiModel {

    public final String baseUrl;
    public final String version;
    public final String brandingForeground;
    public final String brandingBackground;
    public final String brandingSecondary;
    public final URL brandingLogoUrl;
    public final String brandingMessage;
    public final String loginMode;

    public ConfigApiModel(String baseUrl,
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
