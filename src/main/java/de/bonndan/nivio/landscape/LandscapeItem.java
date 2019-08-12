package de.bonndan.nivio.landscape;

import java.util.List;

public interface LandscapeItem {

    String getIdentifier();

    String getName();

    String getContact();

    /**
     * @return path or yaml content
     */
    String getSource();

    List<StateProviderConfig> getStateProviders();

    LandscapeConfig getConfig();
}
