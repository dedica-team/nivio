package de.bonndan.nivio.output.map;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.bonndan.nivio.model.LandscapeItem;

import java.io.Serializable;

/**
 * JSON representation for custom rendering.
 *
 * The x,y coordinates are derived from the rendered mxGraph.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
class MapItem implements Serializable {

    public String id;
    public String name;
    public String image;
    public String group;
    public String groupColor;
    public String type;
    public LandscapeItem landscapeItem;
}
