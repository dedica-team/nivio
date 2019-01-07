package de.bonndan.nivio.api.dto;

import de.bonndan.nivio.landscape.LandscapeItem;
import de.bonndan.nivio.landscape.StateProviderConfig;

import java.util.List;

/**
 * @todo use HAL / ressource support etc
 */
public class LandscapeDTO implements de.bonndan.nivio.landscape.LandscapeItem {

    public static final String LANDSCAPE_PATH = "landscape";

    private String identifier;
    private String name;
    private String contact;
    private List<StateProviderConfig> stateProviders;

    public static LandscapeDTO from(LandscapeItem item) {

        LandscapeDTO l = new LandscapeDTO();
        if (item == null)
            return l;

        l.identifier = item.getIdentifier();
        l.name = item.getName();
        l.contact = item.getContact();
        l.stateProviders = item.getStateProviders();

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
    public List<StateProviderConfig> getStateProviders() {
        return stateProviders;
    }

    public String getUrl() {
        return "/" + LANDSCAPE_PATH + "/" + identifier;
    }
}
