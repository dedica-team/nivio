package de.bonndan.nivio.input.linked;

import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

public class GitHubConfig {

    /**
     * https://github-api.kohsuke.org/index.html
     *
     * export GITHUB_LOGIN=kohsuke
     * export GITHUB_PASSWORD=012345678
     *
     * To connect via Personal access token:
     *
     * export GITHUB_OAUTH=4d98173f7c075527cb64878561d1fe70
     * To connect via Personal access token as a user or organization:
     *
     * export GITHUB_LOGIN=my_org
     * export GITHUB_OAUTH=4d98173f7c075527cb64878561d1fe70
     * To connect via JWT token as a GitHub App:
     *
     * export GITHUB_JWT=my_jwt_token
     *
     */
    @Bean
    public GitHub getGitHub() {
        try {
            return GitHubBuilder.fromEnvironment().build();
        } catch (IOException ignored) {
            return null;
        }
    }
}
