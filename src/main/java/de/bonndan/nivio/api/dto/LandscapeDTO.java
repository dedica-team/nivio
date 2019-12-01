package de.bonndan.nivio.api.dto;

import de.bonndan.nivio.model.GroupItem;
import de.bonndan.nivio.model.LandscapeConfig;
import de.bonndan.nivio.model.Landscape;
import org.springframework.hateoas.RepresentationModel;

import java.util.Map;

/**
 * API representation of a landscape.
 */
public class LandscapeDTO extends RepresentationModel implements Landscape  {

    public String identifier;
    public String name;
    public String contact;
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
    public LandscapeConfig getConfig() {
        return null;
    }

    @Override
    public Map<String, GroupItem> getGroups() {
        return null;
    }
}
