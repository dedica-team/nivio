package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.output.map.hex.Hex;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Produces a point path along the centers of the given hexes.
 *
 *
 */
public class HexPath {

    private final Item source;
    private final Item target;
    private final List<Hex> hexes;
    private List<Hex> bends = null;

    /**
     * @param source
     * @param target
     * @param hexes the hex tile chain in correct order.
     */
    public HexPath(Item source, Item target, List<Hex> hexes) {
        this.source = source;
        this.target = target;
        this.hexes = hexes;
    }

    void reducePoints() {
        //reduce tiles (not first or last)


        /*
        var reduced = [];
        console.log(drop);
        for (i = 0; i < this.tiles.length; i++) {
            if (drop.indexOf(i) !== -1)
                continue;
            reduced.push(this.tiles[i]);
        }
        this.tiles = reduced;
         */
    }

    public Item getSource() {
        return source;
    }

    public Item getTarget() {
        return target;
    }

    /**
     * Returns all hex tiles which are part of the path.
     *
     *
     */
    public List<Hex> getHexes() {
        return hexes;
    }

    /**
     * Returns the path as svg path description with bezier curves.
     *
     * @return M...L notation
     */
    String getPoints() {
        calcBends();
        String points = "M";
        for (var i = 0; i < hexes.size(); i++) {
            var hex = hexes.get(i);
            if (this.isBend(hex)) {
                //cubic curve
                var prev = hexes.get(i - 1).toPixel();
                var point = hex.toPixel();
                var next = hexes.get(i + 1).toPixel();

                var newBefore = new Point2D.Double();
                newBefore.x = prev.x + (point.x - prev.x) / 2;
                newBefore.y = prev.y + (point.y - prev.y) / 2;
                points += " " + newBefore.x + "," + newBefore.y + " ";
                points += "Q " + point.x + "," + point.y + " ";

                var newAfter = new Point2D.Double();
                newAfter.x = next.x + (point.x - next.x) / 2;
                newAfter.y = next.y + (point.y - next.y) / 2;
                points += " " + newAfter.x + "," + newAfter.y + " L";
            } else {
                var p = hex.toPixel();
                points += " " + p.x + "," + p.y + " L";
            }
        }
        return points.substring(0, points.length() - 1);
    }

    private boolean isBend(Hex hex) {

        for (Hex bend : this.bends) {
            if (bend.q == hex.q && bend.r == hex.r) {
                return true;
            }
        }

        return false;
    }

    int getSpeed() {
        calcBends();
        return this.hexes.size() - this.bends.size();
    }

    private void calcBends() {
        if (this.bends == null) {
            this.bends = new ArrayList<>();

            var i = 0;
            for (i = 1; i < this.hexes.size() - 1; i++) {
                var prev = this.hexes.get(i - 1);
                var cur = this.hexes.get(i);
                var next = this.hexes.get(i + 1);
                var qBend = (prev.q == cur.q && next.q != cur.q) || (prev.q != cur.q && next.q == cur.q);
                var rBend = (prev.r == cur.r && next.r != cur.r) || (prev.r != cur.r && next.r == cur.r);
                if (qBend || rBend) {
                    this.bends.add(cur);
                }
            }
        }
    }
}
