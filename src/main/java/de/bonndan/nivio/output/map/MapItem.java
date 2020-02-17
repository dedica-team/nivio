package de.bonndan.nivio.output.map;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

import static java.lang.Math.sqrt;

/**
 * JSON representation for custom rendering.
 *
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
abstract class MapItem implements Serializable {

    public final String id;
    public final String name;
    public final String image;
    public final String type;
    public final String color;

    MapItem(String id, String name, String image, String type, String color) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.type = type;
        this.color = color;
    }
}
