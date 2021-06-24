package de.bonndan.nivio.input.external.gitlab;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "gitlab")

public class GitLabProperties {

    private String hostUrl;
    private String personalAccessToken;
    private String username;
    private String password;

    public GitLabProperties() {

    }

    public GitLabProperties(String hostUrl, String personalAccessToken, String username, String password) {
        this.hostUrl = hostUrl;
        this.personalAccessToken = personalAccessToken;
        this.username = username;
        this.password = password;
    }

    public String getHostUrl() {
        return hostUrl;
    }

    public void setHostUrl(String hostUrl) {
        this.hostUrl = hostUrl;
    }

    public String getPersonalAccessToken() {
        return personalAccessToken;
    }

    public void setPersonalAccessToken(String personalAccessToken) {
        this.personalAccessToken = personalAccessToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
