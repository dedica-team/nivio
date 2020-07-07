package de.bonndan.nivio.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.springframework.util.StringUtils;

import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * A landscape component that has links.
 */
public interface Linked {

    // add semantics, e.g. handle identifier "sonarqube" to grab metrics
    // see https://github.com/dedica-team/nivio/issues/97
    List<String> KNOWN_IDENTIFIERS = List.of(
            "homepage",
            "repo",
            "wiki"
    );

    String LINK_LABEL_PREFIX = "link.";

    /**
     * Returns all assigned links.
     *
     * @return map of links
     */
    @JsonProperty("_links")
    Map<String, Link> getLinks();

    /**
     * Creates and set a {@link Link} when only having a URL.
     *
     * @param identifier name/id
     * @param url href
     */
    default void setLink(String identifier, URL url) {
        if (StringUtils.isEmpty(identifier)) {
            throw new IllegalArgumentException("Link identifier is empty");
        }
        if (KNOWN_IDENTIFIERS.contains(identifier.toLowerCase())) {
            identifier = identifier.toLowerCase();
        }
        getLinks().put(identifier, new Link(url, identifier));
    }

    @JsonSetter("links")
    default void setLinks(Map<String, Link> links) {
        this.getLinks().putAll(links);
    }

}
