package de.bonndan.nivio.output.map;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

class TilePath {

    List<Hex> tiles = new ArrayList<>();
    private final List<Hex> bends = new ArrayList<>();
    private boolean closed = false;

    TilePath(Hex tile) {
        if (tile != null)
            this.tiles.add(tile);
    }

    void close() {
        if (this.closed)
            return;

        this.closed = true;

        var i = 0;
        for (i = 1; i < this.tiles.size() - 1; i++) {
            var prev = this.tiles.get(i - 1);
            var cur = this.tiles.get(i);
            var next = this.tiles.get(i + 1);
            var qBend = (prev.q == cur.q && next.q != cur.q) || (prev.q != cur.q && next.q == cur.q);
            var rBend = (prev.r == cur.r && next.r != cur.r) || (prev.r != cur.r && next.r == cur.r);
            if (qBend || rBend)
                this.bends.add(cur);
        }

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

    String getPoints(Layout layout) {
        String points = "M";
        for (var i = 0; i < this.tiles.size(); i++) {
            var hex = this.tiles.get(i);
            if (this.isBend(hex)) {
                //cubic curve
                var prev = HexUtils.hexToPixel(this.tiles.get(i - 1), layout);
                var point = HexUtils.hexToPixel(this.tiles.get(i), layout);
                var next = HexUtils.hexToPixel(this.tiles.get(i + 1), layout);

                var newBefore = new Point2D.Double();
                newBefore.x = prev.x + (point.x - prev.x) / 2;
                newBefore.y = prev.y + (point.y - prev.y) / 2;
                points += " " + newBefore.x + "," + newBefore.y +" ";
                points += "Q " + point.x + "," + point.y +" ";

                var newAfter = new Point2D.Double();
                newAfter.x = next.x + (point.x - next.x) / 2;
                newAfter.y = next.y + (point.y - next.y) / 2;
                points += " " + newAfter.x + "," + newAfter.y +" L";
            } else {
                var p = HexUtils.hexToPixel(hex, layout);
                points += " " + p.x + "," + p.y +" L";
            }
        }
        return points.substring(0, points.length() - 1);
    }

    private boolean isBend(Hex hex) {
        if (this.bends.size() == 0)
            return false;

        for (var i = 0; i < this.bends.size(); i++) {
            if (this.bends.get(i).q == hex.q && this.bends.get(i).r == hex.r)
                return true;
        }

        return false;
    }

    int getSpeed() {
        return this.tiles.size() - this.bends.size();
    }

    boolean isClosed() {
        return closed;
    }
}
