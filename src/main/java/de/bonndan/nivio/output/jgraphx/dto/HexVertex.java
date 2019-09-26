package de.bonndan.nivio.output.jgraphx.dto;

import com.mxgraph.model.mxCell;
import de.bonndan.nivio.landscape.Service;

import static java.lang.Math.sqrt;

// https://www.redblobgames.com/grids/hexagons/#rounding
public class HexVertex extends Vertex {

    private int size;

    public HexVertex(Service service, mxCell mxCell, int size) {
        super(service, mxCell);
        this.size = size;
    }

    public HexVertex(String groupName, mxCell mxCell, int size) {
        super(groupName, mxCell);
        this.size = size;
    }

    //https://stackoverflow.com/questions/20734438/algorithm-to-generate-a-hexagonal-grid-with-coordinate-system/20751975#20751975

    Hex hex() {
        var q = (2. / 3 * x) / size;
        var r = (-1. / 3 * x + sqrt(3) / 3 * y) / size;
        FractionalHex hex = new FractionalHex(q, r, -q - r);
        return hex.hexRound();
    }

}
