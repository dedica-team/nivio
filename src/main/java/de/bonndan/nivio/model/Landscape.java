package de.bonndan.nivio.model;

import java.util.List;
import java.util.Set;

public interface Landscape {

    String getIdentifier();

    String getName();

    String getContact();

    /**
     * @return path or yaml content
     */
    String getSource();

    List<StateProviderConfig> getStateProviders();

    LandscapeConfig getConfig();

    Set<GroupItem> getGroups();
}
