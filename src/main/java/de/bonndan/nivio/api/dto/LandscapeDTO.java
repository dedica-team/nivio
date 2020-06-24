package de.bonndan.nivio.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.model.LandscapeImpl;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;
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

    /**
     * Group by group identifier
     */
    public Map<String, Group> groups;
    public Set<Item> items;
    public LocalDateTime lastUpdate;
    public String[] teams;

}
