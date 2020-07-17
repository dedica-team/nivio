package de.bonndan.nivio.input;


import de.bonndan.nivio.input.http.HttpService;
import de.bonndan.nivio.model.Linked;
import org.springframework.stereotype.Component;

/**
 * A factory for {@link LinkResolver} implementations.
 *
 *
 */
@Component
public class LinkResolverFactory {

    private final HttpService httpService;

    public LinkResolverFactory(HttpService httpService) {
        this.httpService = httpService;
    }

    /**
     * @param key see {@link de.bonndan.nivio.model.Linked} KNOWN_IDENTIFIERS
     */
    public LinkResolver getResolver(String key) {
        var moveHere = Linked.KNOWN_IDENTIFIERS;
        return null;
    }
}
