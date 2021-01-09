package de.bonndan.nivio.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

public class URIHelper {

    /**
     * Returns an URI object if the string is an URI and has host and scheme.
     *
     * @param string uri
     */
    public static Optional<URI> getURI(String string) {
        try {
            URI value = new URI(string);
            if (value.getHost() != null && value.getScheme() != null) {
                return Optional.of(value);
            }
            return Optional.empty();
        } catch (URISyntaxException e) {
            return Optional.empty();
        }
    }
}
