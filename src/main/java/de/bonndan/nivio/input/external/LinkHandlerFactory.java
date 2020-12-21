package de.bonndan.nivio.input.external;


import de.bonndan.nivio.input.external.github.GitHubRepoHandler;
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

    // add semantics, e.g. handle identifier "sonarqube" to grab metrics
    // see https://github.com/dedica-team/nivio/issues/97
    static Map<String, Class<? extends ExternalLinkHandler>> KNOWN_RESOLVERS = Map.of(
            GITHUB, GitHubRepoHandler.class,
            SONAR, SonarLinkHandler.class,
            SPRING_HEALTH, SpringBootHealthHandler.class
    );

    private final AutowireCapableBeanFactory beanFactory;

    public LinkHandlerFactory(AutowireCapableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public Optional<ExternalLinkHandler> getResolver(final String key) {

        return Optional.ofNullable(KNOWN_RESOLVERS.get(key.toLowerCase()))
                .map(aClass -> createHandler(key, aClass));
    }

    @NonNull
    private ExternalLinkHandler createHandler(String key, Class<? extends ExternalLinkHandler> aClass) {
        try {
            return beanFactory.createBean(aClass);
        } catch (BeansException e) {
            throw new RuntimeException(String.format("Failed to create external link handler of type %s", key));
        }
    }
}
