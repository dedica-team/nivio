package de.bonndan.nivio.landscape;

import java.util.List;

public interface LandscapeItem {

    String getIdentifier();

    String getName();

    String getContact();

    List<StateProviderConfig> getStateProviders();
}
