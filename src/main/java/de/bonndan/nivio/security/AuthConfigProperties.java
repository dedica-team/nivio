package de.bonndan.nivio.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Configuration of authentication and related matters.
 */
@Configuration
@ConfigurationProperties("auth")
@Validated
public class AuthConfigProperties {

    @NotEmpty
    @Pattern(regexp = "none|optional|required", message = "Login mode must be one of none|optional|required")
    private String loginMode = SecurityConfig.LOGIN_MODE_NONE;

    private String githubNameAttribute = "name";

    private String githubAliasAttribute = "login";

    private List<String> allowedOriginPatterns;

    public String getLoginMode() {
        return loginMode;
    }

    public void setLoginMode(String loginMode) {
        this.loginMode = loginMode;
    }

    public List<String> getAllowedOriginPatterns() {
        return allowedOriginPatterns.stream().filter(StringUtils::hasLength).collect(Collectors.toList());
    }

    public void setAllowedOriginPatterns(String allowedOriginPatterns) {
        this.allowedOriginPatterns = List.of(allowedOriginPatterns.split(";"));
    }

    public String getGithubNameAttribute() {
        return githubNameAttribute;
    }

    public void setGithubNameAttribute(String githubNameAttribute) {
        this.githubNameAttribute = githubNameAttribute;
    }

    public String getGithubAliasAttribute() {
        return githubAliasAttribute;
    }

    public void setGithubAliasAttribute(String githubAliasAttribute) {
        this.githubAliasAttribute = githubAliasAttribute;
    }
}
