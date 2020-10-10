package de.bonndan.nivio.model;

import de.bonndan.nivio.LandscapeConfig;

import java.util.Map;

public interface Landscape extends Component, Linked {

    /**
     * @return path or yaml content
     */
    String getSource();

    LandscapeConfig getConfig();

    /**
     * Return the groups by their identifier.
     * @return
     */
    Map<String, Group> getGroups();
}
