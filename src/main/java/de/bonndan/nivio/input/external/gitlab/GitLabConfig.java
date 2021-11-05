package de.bonndan.nivio.input.external.gitlab;

import org.gitlab4j.api.GitLabApi;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

import java.util.Optional;

@Configuration
@EnableConfigurationProperties(GitLabProperties.class)
public class GitLabConfig {

    private final GitLabProperties gitLabProperties;

    public GitLabConfig(GitLabProperties gitLabProperties) {
        this.gitLabProperties = gitLabProperties;
    }

    @Bean
    public GitLabApi getGitLabAPI() {

        Optional<String> hostUrl = Optional.ofNullable(gitLabProperties.getHostUrl());
        if (hostUrl.isEmpty()) {
            return null;
        }

        Optional<String> personalAccessToken = Optional.ofNullable(gitLabProperties.getPersonalAccessToken());
        Optional<String> username = Optional.ofNullable(gitLabProperties.getUsername());
        Optional<String> password = Optional.ofNullable(gitLabProperties.getPassword());

        return getGitLabAPI(hostUrl.get(), personalAccessToken, username, password);
    }

    GitLabApi getGitLabAPI(@NonNull String hostUrl,
                           Optional<String> personalAccessToken,
                           Optional<String> username,
                           Optional<String> password
    ) {

        if (personalAccessToken.isPresent()) {
            return new GitLabApi(hostUrl, personalAccessToken.get());
        }

        if (username.isPresent() && password.isPresent()) {
            return new GitLabApi(hostUrl, username.get(), password.get());
        }

        return null;
    }

}
