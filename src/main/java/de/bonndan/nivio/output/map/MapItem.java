package de.bonndan.nivio.output.map;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.bonndan.nivio.model.LandscapeItem;

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
    public int size;

    MapItem(String id, String name, String image, String type, String color) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.type = type;
        this.color = color;
    }

    /**
     * Calculated hexagon map coordinates from x-y coordinates.
     * <p>
     * https://stackoverflow.com/questions/20734438/algorithm-to-generate-a-hexagonal-grid-with-coordinate-system/20751975#20751975
     * https://www.redblobgames.com/grids/hexagons/#rounding
     */
    protected Hex asHex(long x, long y, int size) {
        var q = (2. / 3 * x) / size;
        var r = (-1. / 3 * x + sqrt(3) / 3 * y) / size;
        FractionalHex hex = new FractionalHex(q, r, -q - r);
        return hex.hexRound();
    }
}
