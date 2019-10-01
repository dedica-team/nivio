package de.bonndan.nivio.api.dto;

import de.bonndan.nivio.model.LandscapeConfig;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.model.StateProviderConfig;

import java.util.List;

/**
 * @todo use HAL / ressource support etc
 */
public class LandscapeDTO implements Landscape {

    public static final String LANDSCAPE_PATH = "landscape";

    private String identifier;
    private String name;
    private String contact;
    private List<StateProviderConfig> stateProviders;
    private String source;

    public static LandscapeDTO from(Landscape item) {

        LandscapeDTO l = new LandscapeDTO();
        if (item == null)
            return l;

        l.identifier = item.getIdentifier();
        l.name = item.getName();
        l.contact = item.getContact();
        l.stateProviders = item.getStateProviders();
        l.source = item.getSource();

        return l;
    }

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

    public String getUrl() {
        return "/" + LANDSCAPE_PATH + "/" + identifier;
    }
}
