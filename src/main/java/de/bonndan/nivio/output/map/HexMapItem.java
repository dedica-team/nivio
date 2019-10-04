package de.bonndan.nivio.output.map;


import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.LandscapeItem;
import de.bonndan.nivio.output.jgraphx.FinalGraph;

import static java.lang.Math.sqrt;

/**
 * Calculated hexagon map coordinates from x-y coordinates.
 *
 * https://stackoverflow.com/questions/20734438/algorithm-to-generate-a-hexagonal-grid-with-coordinate-system/20751975#20751975
 * https://www.redblobgames.com/grids/hexagons/#rounding
 */
class HexMapItem extends MapItem {

    public final String type;
    public final String groupColor;
    private final long x;
    private final long y;
    private final int size;

    public HexMapItem(XYMapItem i, int size) {
        this.size = size;
        this.type = i.type;
        this.groupColor = FinalGraph.getGroupColor(i.service.getGroup(), ((Item)i.service).getLandscape().getConfig()); //TODO
        this.landscapeItem = i.service;
        this.x = i.x;
        this.y = i.y;
    }

    //

    public Hex getHex() {
        var q = (2. / 3 * x) / size;
        var r = (-1. / 3 * x + sqrt(3) / 3 * y) / size;
        FractionalHex hex = new FractionalHex(q, r, -q - r);
        return hex.hexRound();
    }

}
