package de.bonndan.nivio.output.jgraphx.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.LandscapeItem;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;


import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Vertex implements Serializable {

    public String id;
    public String name;
    public long x;
    public long y;
    public double width;
    public double height;
    public String image;
    public String group;
    public String groupColor;
    public String type;
    public LandscapeItem service;

    public Vertex(Item service, mxCell cell) {
        type = "service";
        this.service = service;

        mxGeometry geometry = cell.getGeometry();

        id = cell.getId();
        name = (String) cell.getValue();
        group = service.getGroup();

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

    public Vertex(String groupName, mxCell cell) {
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

    public Vertex() {

    }

}
