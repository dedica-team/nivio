package de.bonndan.nivio.config;

import java.util.Optional;

public enum ConfigurableEnvVars {

    DEMO("A non-empty value causes Nivio to start in demo mode with prepared data. Use the value 'all' to load more landscapes."),
    PORT("The port Nivio runs on."),
    SEED("A semicolon-separated list of file paths containing landscape configurations."),

    NIVIO_BASE_URL("The base URL of Nivio to be used for frontends if running behind a proxy."),
    NIVIO_ICON_FOLDER("A folder containing icons named similar to material design icons"),
    NIVIO_MAIL_HOST("SMTP mail host."),
    NIVIO_MAIL_PORT("SMTP mail port."),
    NIVIO_MAIL_USERNAME("SMTP mail username."),
    NIVIO_MAIL_PASSWORD("SMTP mail password."),
    NIVIO_BRANDING_FOREGROUND("Branding foreground color (hexadecimal only)."),
    NIVIO_BRANDING_BACKGROUND("Branding background color (hexadecimal only)."),
    NIVIO_BRANDING_SECONDARY("Accent color used for active elements (hexadecimal only)."),
    NIVIO_BRANDING_LOGO_URL("A URL pointing to a logo."),
    NIVIO_BRANDING_MESSAGE("A welcome message on the front page."),

    /**
     * see {@link org.kohsuke.github.GitHubBuilder}
     */
    GITHUB_LOGIN("GitHub user name. Can also be used to connect as organization with OAuth."),
    GITHUB_PASSWORD("GitHub password (for username/password login)."),
    GITHUB_OAUTH("GitHUb OAuth Token to connect to GitHub via personal access token."),
    GITHUB_JWT("GitHub JSON Web Token (JWT) to connect to GitHub as a GitHub App."),

    GITLAB_HOST_URL("The full URL to the GitLab API, e.g. http://your.gitlab.server.com/api/v4."),
    GITLAB_PERSONAL_ACCESS_TOKEN("Personal token to access the GitLab API at :envvar:`GITLAB_HOST_URL` (optional)."),
    GITLAB_USERNAME("GitLab OAuth login username (optional). If used, :envvar:`GITLAB_PASSWORD` is also required)."),
    GITLAB_PASSWORD("GitLab OAuth login password (optional)."),

    KUBERNETES_MASTER("K8s master URL (optional). All variables from https://github.com/fabric8io/kubernetes-client#configuring-the-client can be used."),

    SONAR_SERVER_URL("SonarQube server URL."),
    SONAR_LOGIN("SonarQube login (username)."),
    SONAR_PASSWORD("SonarQube password."),
    SONAR_PROXY_HOST("SonarQube proxy host (optional)."),
    SONAR_PROXY_PORT("SonarQube proxy port (optional).");


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
