package de.bonndan.nivio.input.external;


import de.bonndan.nivio.input.external.github.GitHubRepoHandler;
import de.bonndan.nivio.input.external.gitlab.GitLabRepoHandler;
import de.bonndan.nivio.input.external.openapi.OpenAPILinkHandler;
import de.bonndan.nivio.input.external.sonar.SonarLinkHandler;
import de.bonndan.nivio.input.external.springboot.SpringBootHealthHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * A factory for {@link ExternalLinkHandler} implementations.
 */
@Component
public class LinkHandlerFactory {

    public static final String GITHUB = "github";
    private static final String SONAR = "sonar";
    private static final String SPRING_HEALTH = "spring.health";
    private static final String GITLAB = "gitlab";

    // add semantics, e.g. handle identifier "sonarqube" to grab metrics
    // see https://github.com/dedica-team/nivio/issues/97
    static Map<String, Class<? extends ExternalLinkHandler>> KNOWN_RESOLVERS = Map.of(
            GITHUB, GitHubRepoHandler.class,
            SONAR, SonarLinkHandler.class,
            SPRING_HEALTH, SpringBootHealthHandler.class,
            OpenAPILinkHandler.NAMESPACE, OpenAPILinkHandler.class,
            GITLAB, GitLabRepoHandler.class

    );

    private final AutowireCapableBeanFactory beanFactory;

    public LinkHandlerFactory(AutowireCapableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public Optional<ExternalLinkHandler> getResolver(final String key) {
        return Optional.ofNullable(KNOWN_RESOLVERS.get(key.toLowerCase()))
                .flatMap(aClass -> createHandler(key, aClass));
    }

    @NonNull
    private Optional<ExternalLinkHandler> createHandler(String key, Class<? extends ExternalLinkHandler> aClass) {
        try {
            return Optional.of(beanFactory.createBean(aClass));
        } catch (BeansException e) {

            return Optional.empty();
        }
    }
}
