package de.bonndan.nivio.landscape;

import java.util.List;

//TODO naming
public interface LandscapeInterface {

    String getIdentifier();

    String getName();

    String getContact();

    List<StateProviderConfig> getStateProviders();
}
