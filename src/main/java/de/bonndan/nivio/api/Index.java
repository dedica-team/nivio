package de.bonndan.nivio.api;

import de.bonndan.nivio.model.Linked;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * API root object.
 */
public class Index implements Linked {

    private final Map<String, URL> links = new HashMap<>();

    @Override
    public Map<String, URL> getLinks() {
        return links;
    }
}
