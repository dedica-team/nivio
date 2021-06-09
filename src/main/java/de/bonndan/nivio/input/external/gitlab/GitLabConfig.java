package de.bonndan.nivio.input.external.gitlab;

import org.gitlab4j.api.GitLabApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

import java.util.Optional;

import static de.bonndan.nivio.config.ConfigurableEnvVars.*;


@Configuration
@EnableConfigurationProperties(GitLabProperties.class)
public class GitLabConfig {

    private final GitLabProperties gitLabProperties;

    @Autowired
    public GitLabConfig(GitLabProperties gitLabProperties) {
        this.gitLabProperties = gitLabProperties;
    }

    @Bean
    public GitLabApi getGitLabAPI() {

        Optional<String> hostUrl = GITLAB_HOST_URL.value();
        if (hostUrl.isEmpty()) {
            return null;
        }

        Optional<String> personalAccessToken = GITLAB_PERSONAL_ACCESS_TOKEN.value();
        Optional<String> username = GITLAB_USERNAME.value();
        Optional<String> password = GITLAB_PASSWORD.value();

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
