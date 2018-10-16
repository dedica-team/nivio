package de.bonndan.nivio.state;

import de.bonndan.nivio.landscape.FullyQualifiedIdentifier;

import java.util.Map;

public interface Provider {

    /**
     * The provider is expected return a map of service states.
     *
     */
    Map<FullyQualifiedIdentifier, ServiceState> getStates();

}
