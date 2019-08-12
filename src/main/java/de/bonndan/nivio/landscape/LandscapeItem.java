package de.bonndan.nivio.landscape;

import java.util.List;
import java.util.Map;

public interface LandscapeItem {

    String getIdentifier();

    String getName();

    String getContact();

    /**
     * @return path or yaml content
     */
    String getSource();

    List<StateProviderConfig> getStateProviders();

    Map<String,String> getConfigMap();
}
