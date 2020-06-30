package de.bonndan.nivio.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    List<String> KNOWN_IDENTIFIERS = List.of(
            "homepage",
            "repo",
            "wiki"
    );

    String LINK_LABEL_PREFIX = "link.";

    /**
     * Returns all assigned links.
     *
     * The map is not serialized, instead all links are turned into HateOAS links by a special serializer.
     *
     * @return map of links
     */
    @JsonIgnore
    Map<String, URL> getLinks();

    /**
     * The Hateoas representation.
     *
     * @return links in hateoas format.
     */
    @JsonProperty("_links")
    default LinkedWrapper getHateoasLinks() {
        return new LinkedWrapper(this);
    }

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
