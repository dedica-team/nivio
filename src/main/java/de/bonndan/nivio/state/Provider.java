package de.bonndan.nivio.state;

import de.bonndan.nivio.landscape.FullyQualifiedIdentifier;

import java.util.Map;

public interface Provider {

    /**
     * The provider is expected to write the states to the given map.
     *
     * @param state the map containing the services
     */
    void apply(Map<FullyQualifiedIdentifier, ServiceState> state);

}
