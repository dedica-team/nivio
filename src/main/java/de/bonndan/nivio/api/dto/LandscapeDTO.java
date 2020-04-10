package de.bonndan.nivio.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import org.springframework.hateoas.RepresentationModel;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * API representation of a landscape.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LandscapeDTO extends RepresentationModel {

    public String identifier;
    public String name;
    public String contact;
    public String description;

    public String source;
    public Map<String, Group> groups;
    public Set<Item> items;
    public Date lastUpdate;
    public String[] teams;

    public String getIdentifier() {
        return identifier;
    }

    public String getName() {
        return name;
    }

    public String getContact() {
        return contact;
    }

    public String getDescription() {
        return description;
    }

    public String getSource() {
        return source;
    }
}
