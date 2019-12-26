package de.bonndan.nivio.output.map;


import static java.lang.Math.sqrt;

/**
 * Calculated hexagon map coordinates from x-y coordinates.
 *
 * https://stackoverflow.com/questions/20734438/algorithm-to-generate-a-hexagonal-grid-with-coordinate-system/20751975#20751975
 * https://www.redblobgames.com/grids/hexagons/#rounding
 */
class HexMapItem extends XYMapItem {

    public final String type;
    private final long x;
    private final long y;
    private final int size;

    public HexMapItem(XYMapItem mapItem, int size) {
        this.size = size;
        this.type = mapItem.type;
        this.landscapeItem =  mapItem.landscapeItem;
        this.x = mapItem.x;
        this.y = mapItem.y;
    }

    public Hex getHex() {
        var q = (2. / 3 * x) / size;
        var r = (-1. / 3 * x + sqrt(3) / 3 * y) / size;
        FractionalHex hex = new FractionalHex(q, r, -q - r);
        return hex.hexRound();
    }

}
