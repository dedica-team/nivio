package de.bonndan.nivio.input.external.github;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "github")


public class GitHubProperties {

    private String login;
    private String password;
    private String oauth;
    private String jwt;

    public GitHubProperties(String login, String password, String oauth, String jwt) {
        this.login = login;
        this.password = password;
        this.oauth = oauth;
        this.jwt = jwt;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOauth() {
        return oauth;
    }

    public void setOauth(String oauth) {
        this.oauth = oauth;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }


}
