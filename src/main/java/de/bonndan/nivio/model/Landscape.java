package de.bonndan.nivio.model;

import java.util.Map;

public interface Landscape {

    String getIdentifier();

    String getName();

    String getContact();

    /**
     * @return path or yaml content
     */
    String getSource();

    LandscapeConfig getConfig();

    /**
     * Return the groups by their identifier.
     */
    Map<String, GroupItem> getGroups();
}
