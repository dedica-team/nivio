package de.bonndan.nivio.input.external.gitlab;

import org.gitlab4j.api.GitLabApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

import static de.bonndan.nivio.config.ConfigurableEnvVars.*;


@Configuration
public class GitLabConfig {

    @Bean
    public GitLabApi getGitLabAPI() {

        Optional<String> hostUrl = GITLAB_HOST_URL.value();
        if (hostUrl.isEmpty()) {
            return null;
        }

        Optional<String> personalAccessToken = GITLAB_PERSONAL_ACCESS_TOKEN.value();
        if (personalAccessToken.isPresent()) {
            return new GitLabApi(hostUrl.get(), personalAccessToken.get());
        }

        Optional<String> username = GITLAB_USERNAME.value();
        Optional<String> password = GITLAB_PASSWORD.value();
        if (username.isPresent() && password.isPresent()) {
            return new GitLabApi(hostUrl.get(), username.get(), password.get());
        }

        return null;
    }

}
