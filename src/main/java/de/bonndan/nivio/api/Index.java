package de.bonndan.nivio.api;

import de.bonndan.nivio.config.NivioConfigProperties;
import de.bonndan.nivio.model.Link;
import de.bonndan.nivio.model.Linked;

import java.util.HashMap;
import java.util.Map;

/**
 * API root object.
 */
public class Index implements Linked {

    private final Map<String, Link> links = new HashMap<>();
    private final NivioConfigProperties.ApiModel config;

    public Index(NivioConfigProperties.ApiModel config) {
        this.config = config;
    }

    @Override
    public Map<String, Link> getLinks() {
        return links;
    }

    public NivioConfigProperties.ApiModel getConfig() {
        return config;
    }
}
