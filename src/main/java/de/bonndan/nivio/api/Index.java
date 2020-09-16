package de.bonndan.nivio.api;

import de.bonndan.nivio.model.Link;
import de.bonndan.nivio.model.Linked;

import java.util.HashMap;
import java.util.Map;

/**
 * API root object.
 */
public class Index implements Linked {

    private final Map<String, Link> links = new HashMap<>();

    @Override
    public Map<String, Link> getLinks() {
        return links;
    }
}
