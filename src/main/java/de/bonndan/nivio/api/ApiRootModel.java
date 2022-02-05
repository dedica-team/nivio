package de.bonndan.nivio.api;

import de.bonndan.nivio.model.Link;
import de.bonndan.nivio.model.Linked;

import java.util.HashMap;
import java.util.Map;

/**
 * API root object.
 */
public class ApiRootModel implements Linked {

    private final Map<String, Link> links = new HashMap<>();
    private final Map<String, Link> oauth2links = new HashMap<>();
    private final ConfigApiModel config;

    public ApiRootModel(ConfigApiModel config) {
        this.config = config;
    }

    @Override
    public Map<String, Link> getLinks() {
        return links;
    }

    public Map<String, Link> getOauth2Links() {
        return oauth2links;
    }

    public ConfigApiModel getConfig() {
        return config;
    }
}
