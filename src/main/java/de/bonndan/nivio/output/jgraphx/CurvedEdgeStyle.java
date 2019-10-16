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
            if (pt != null) {
                result.add(pt);
                return;
            }

            double x = 0;
            double y = 0;
            double xShift = 0;
            double yShift = 0;
            /*
             * if the x difference is higher than y difference, we move the y controlpoint off the center and vice versa
             */
            double xDiff = target.getCenterX() - source.getCenterX();
            double yDiff = target.getCenterY() - source.getCenterY();
            if (Math.abs(xDiff) > Math.abs(yDiff))
                yShift = yDiff / 2;
            else
                xShift = xDiff /2;

            if (source.getCenterX() > target.getCenterX())
                xShift *= -1;
            if (source.getCenterY() > target.getCenterY())
                yShift *= -1;

            x = source.getCenterX() + xDiff/2 - xShift;
            y = source.getCenterY() + yDiff/2 - yShift;
            mxPoint point = new mxPoint(x, y);
            result.add(point);

        }
    }

}
