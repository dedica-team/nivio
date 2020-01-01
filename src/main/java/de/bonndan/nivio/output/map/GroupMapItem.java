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
    public final long x1;
    public final long y1;
    public final long x2;
    public final long y2;

    public GroupMapItem(Group group, long x1, long y1, long x2, long y2) {
        super(group.getIdentifier(), group.getIdentifier(), "", "group", Color.getGroupColor(group));

        this.group = group;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public Hex getStart() {
        return asHex(x1, y1, size);
    }

    public Hex getEnd() {
        return asHex(x2, y2, size);
    }
}
