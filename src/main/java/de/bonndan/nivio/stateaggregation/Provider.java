package de.bonndan.nivio.stateaggregation;

import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import de.bonndan.nivio.model.StatusItem;

import java.util.Map;

public interface Provider {

    /**
     * The provider is expected return a map of service states.
     *
     */
    Map<FullyQualifiedIdentifier, StatusItem> getStates();

}
