package de.bonndan.nivio.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.bonndan.nivio.model.GroupItem;
import de.bonndan.nivio.model.LandscapeConfig;
import de.bonndan.nivio.model.Landscape;
import org.springframework.hateoas.RepresentationModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * API representation of a landscape.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LandscapeDTO extends RepresentationModel implements Landscape  {

    public String identifier;
    public String name;
    public String contact;
    public String description;

    public String source;
    public Map<String, GroupItem> groups;
    public LandscapeStatistics stats;
    public Set<String> teams;

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
    public String getDescription() {
        return description;
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
        return groups;
    }
}
