package de.bonndan.nivio.output.map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import de.bonndan.nivio.model.LandscapeItem;

/**
 * JSON representation for custom rendering.
 *
 * The x,y coordinates are derived from the rendered mxGraph.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
class XYMapItem extends MapItem {

    public String id;
    public String name;
    public long x;
    public long y;
    public double width;
    public double height;
    public String image;
    public String group;
    public String type;

    XYMapItem(LandscapeItem item, mxCell cell) {
        type = "service";
        this.landscapeItem = item;

        mxGeometry geometry = cell.getGeometry();

        id = cell.getId();
        name = (String) cell.getValue();
        group = item.getGroup();

        if (cell.getParent().getGeometry() != null) {
            x = Math.round(geometry.getX() + cell.getParent().getGeometry().getX());
            y = Math.round(geometry.getY() + cell.getParent().getGeometry().getY());
        } else {
            x = Math.round(geometry.getX());
            y = Math.round(geometry.getY());
        }
        width = Math.round(geometry.getWidth());
        height = Math.round(geometry.getHeight());
    }

    XYMapItem(String groupName, mxCell cell) {
        type = "group";

        mxGeometry geometry = cell.getGeometry();

        id = cell.getId();
        name = (String) cell.getValue();
        group = groupName;

        if (cell.getParent().getGeometry() != null) {
            x = Math.round(geometry.getX() + cell.getParent().getGeometry().getX());
            y = Math.round(geometry.getY() + cell.getParent().getGeometry().getY());
        } else {
            x = Math.round(geometry.getX());
            y = Math.round(geometry.getY());
        }
        width = Math.round(geometry.getWidth());
        height = Math.round(geometry.getHeight());
    }

    XYMapItem() {

    }
}
