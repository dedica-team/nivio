package de.bonndan.nivio.output.jgraphx.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.bonndan.nivio.landscape.ServiceItem;

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
    public ServiceItem service;
}
