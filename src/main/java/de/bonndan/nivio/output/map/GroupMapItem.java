package de.bonndan.nivio.output.map;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.output.Color;


/**
 * A group on the map (work in progress).
 *
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public
class GroupMapItem extends MapItem {

    public final Group group;
    public final long x;
    public final long y;
    public final long width;
    public final long height;

    public GroupMapItem(Group group, long x, long y, long width, long height) {
        super(group.getIdentifier(), group.getIdentifier(), "", "group", Color.getGroupColor(group));

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.group = group;
    }

    public Hex getStart() {
        return asHex(x, y, size);
    }

    public Hex getEnd() {
        return asHex(x + width, y + height, size);
    }
}
