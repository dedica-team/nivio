package de.bonndan.nivio.model;

import java.net.URL;
import java.util.Map;

/**
 * A landscape component that has links.
 *
 *
 */
public interface Linked {

    /**
     *
     * @return map of links
     */
    Map<String, URL> getLinks();
}
