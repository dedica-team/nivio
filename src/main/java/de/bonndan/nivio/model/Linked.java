package de.bonndan.nivio.model;

import org.springframework.util.StringUtils;

import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * A landscape component that has links.
 *
 *
 */
public interface Linked {

    //TODO add semantics, e.g. handle identifier "sonarqube" to grab metrics
    // see https://github.com/dedica-team/nivio/issues/97
    public static final List<String> KNOWN_IDENTIFIERS = List.of(
            "homepage",
            "repo",
            "wiki"
    );

    String LINK_LABEL_PREFIX = "link.";

    /**
     *
     * @return map of links
     */
    Map<String, URL> getLinks();

    default void setLink(String identifier, URL url) {
        if (StringUtils.isEmpty(identifier)){
            throw new IllegalArgumentException("Link identifier is empty");
        }
        if (KNOWN_IDENTIFIERS.contains(identifier.toLowerCase())) {
            identifier = identifier.toLowerCase();
        }
        getLinks().put(identifier, url);
    }
}
