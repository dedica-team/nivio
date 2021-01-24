package de.bonndan.nivio.config;

import java.util.Optional;

public enum ConfigurableEnvVars {

    DEMO("A non-empty value causes nivio to start in demo mode with prepared data."),
    PORT("The port nivio runs on."),
    NIVIO_BASE_URL("The base url of nivio to be used for frontends if running behind a proxy"),
    NIVIO_MAIL_HOST("The smtp mail host"),
    NIVIO_MAIL_PORT("The smtp mail port"),
    NIVIO_MAIL_USERNAME("The smtp mail username"),
    NIVIO_MAIL_PASSWORD("The smtp mail password"),
    NIVIO_BRANDING_FOREGROUND("branding foreground color (hexadecimal only)"),
    NIVIO_BRANDING_BACKGROUND("branding background color (hexadecimal only)"),
    NIVIO_BRANDING_SECONDARY("accent color used for active elements (hexadecimal only)"),
    NIVIO_BRANDING_LOGO_URL("A URL pointing to a logo"),

    /** see {@link org.kohsuke.github.GitHubBuilder} */
    GITHUB_LOGIN("GitHub user name (can also be used to connect as organisation with oauth"),
    GITHUB_PASSWORD("GitHub password (for username/pw login)"),
    GITHUB_OAUTH("To connect to GitHub via Personal access token"),
    GITHUB_JWT("To connect via JWT token as a GitHub App:"),

    GITLAB_HOST_URL("The full URL to the GitLab API, e.g. http://your.gitlab.server.com"),
    GITLAB_PERSONAL_ACCESS_TOKEN("the personal token to access the GitLab API (optional)"),
    GITLAB_USERNAME("GitLab OAuth login username (optional, if used password is also required)"),
    GITLAB_PASSWORD("GitLab OAuth login password (optional)"),

    KUBERNETES_MASTER("K8s master url (optional), all variables from https://github.com/fabric8io/kubernetes-client#configuring-the-client can be used"),

    SEED("A semicolon-separated list of file paths containing landscape configurations"),

    SONAR_LOGIN("Sonarqube login (username)"),
    SONAR_SERVER_URL("Sonarqube server url"),
    SONAR_PASSWORD("Sonarqube password"),
    SONAR_PROXY_HOST("Sonarqube proxy host (optional)"),
    SONAR_PROXY_PORT("Sonarqube proxy port (optional)");


    private final String description;

    ConfigurableEnvVars(String description) {
        this.description = description;
    }

    /**
     * The description of the variable for the documentation.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the string value of the configuration variable if present in the environment.
     *
     * @return env value
     */
    public Optional<String> value() {
        return Optional.ofNullable(System.getenv(name()));
    }

}
