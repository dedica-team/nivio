package de.bonndan.nivio.input.linked;


import de.bonndan.nivio.input.http.HttpService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Optional;

/**
 * A factory for {@link ExternalLinkHandler} implementations.
 */
@Component
public class LinkHandlerFactory {

    public static final String GITHUB = "github";
    // add semantics, e.g. handle identifier "sonarqube" to grab metrics
    // see https://github.com/dedica-team/nivio/issues/97
    static Map<String, Class<? extends ExternalLinkHandler>> KNOWN_RESOLVERS = Map.of(
            GITHUB, GitHubProjectHandler.class
    );

    private final HttpService httpService;

    public LinkHandlerFactory(HttpService httpService) {
        this.httpService = httpService;
    }

    /**
     * @param key see {@link de.bonndan.nivio.model.Linked} KNOWN_IDENTIFIERS
     */
    public Optional<ExternalLinkHandler> getResolver(final String key) {

        return Optional.ofNullable(KNOWN_RESOLVERS.get(key.toLowerCase()))
                .map(aClass -> createHandler(key, httpService, aClass));
    }

    @NonNull
    private ExternalLinkHandler createHandler(String key, HttpService httpService, Class<? extends ExternalLinkHandler> aClass) {
        try {
            return aClass.getDeclaredConstructor().newInstance(httpService);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(String.format("Failed to create resolver of type %s", key));
        }
    }
}
