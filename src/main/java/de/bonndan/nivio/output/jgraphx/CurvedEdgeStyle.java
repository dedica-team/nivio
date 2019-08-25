package de.bonndan.nivio.output.jgraphx;

import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxEdgeStyle;

import java.util.List;

/**
 * https://stackoverflow.com/questions/22746439/jgraphx-custom-layoult-curved-edges
 */
class CurvedEdgeStyle implements mxEdgeStyle.mxEdgeStyleFunction {

    public static final String KEY = "curvedEdgeStyle";


    @Override
    public void apply(mxCellState state, mxCellState source, mxCellState target, List<mxPoint> points, List<mxPoint> result) {
        mxPoint pt = (points != null && points.size() > 0) ? points.get(0) : null;
        if (source != null && target != null) {
            double x = 0;
            double y = 0;
            if (pt != null) {
                result.add(pt);
            } else {

                double offsetx = target.getCenterX() > source.getCenterX() ? 1.85 : 2.15;
                double offsety = target.getCenterY() > source.getCenterY() ? 1.50 : 2.50;
                x = (target.getCenterX() + source.getCenterX()) / offsetx; //shift x center to right
                y = (target.getCenterY() + source.getCenterY()) / offsety;
                mxPoint point = new mxPoint(x, y);
                result.add(point);
            }
        }
    }

}
