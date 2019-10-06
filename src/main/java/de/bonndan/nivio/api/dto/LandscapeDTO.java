package de.bonndan.nivio.api.dto;

import de.bonndan.nivio.model.LandscapeConfig;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.model.StateProviderConfig;
import org.springframework.hateoas.ResourceSupport;

import java.util.List;

/**
 * API representation of a landscape.
 */
public class LandscapeDTO extends ResourceSupport implements Landscape  {

    public String identifier;
    public String name;
    public String contact;
    public List<StateProviderConfig> stateProviders;
    public String source;

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getContact() {
        return contact;
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public List<StateProviderConfig> getStateProviders() {
        return stateProviders;
    }

    @Override
    public LandscapeConfig getConfig() {
        return null;
    }
}
