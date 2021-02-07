package de.bonndan.nivio.util;

import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

public class URIHelper {

    /**
     * Returns an Optional of an URI object (empty on URISyntaxException).
     *
     * @param string uri
     */
    public static Optional<URI> getURI(@Nullable String string) {
        if (StringUtils.isEmpty(string)) {
            return Optional.empty();
        }

        try {
            return Optional.of(new URI(string));
        } catch (URISyntaxException e) {
            return Optional.empty();
        }
    }

    /**
     * Returns an Optional of an URI object (not empty if the string is an URI and has host and scheme).
     *
     * @param string uri
     */
    public static Optional<URI> getURIWithHostAndScheme(@Nullable String string) {
        Optional<URI> uri = getURI(string);
        if (uri.isPresent()) {
            if (uri.get().getHost() != null && uri.get().getScheme() != null) {
                return uri;
            }
        }
        return Optional.empty();
    }
}
