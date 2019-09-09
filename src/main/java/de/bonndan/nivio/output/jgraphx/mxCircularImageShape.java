package de.bonndan.nivio.output.jgraphx;

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.shape.mxEllipseShape;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxCellState;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class mxCircularImageShape extends mxEllipseShape {

    public static final String NAME = "circularImage";

    private final int IMAGE_MARGIN = 8;

    public void paintShape(mxGraphics2DCanvas canvas, mxCellState state) {
        Shape shape = this.createShape(canvas, state);
        if (shape != null) {
            if (this.configureGraphics(canvas, state, true)) {
                canvas.fillShape(shape, this.hasShadow(canvas, state));
            }

            if (this.configureGraphics(canvas, state, false)) {
                canvas.getGraphics().draw(shape);
            }
        }

        boolean flipH = mxUtils.isTrue(state.getStyle(), mxConstants.STYLE_IMAGE_FLIPH, false);
        boolean flipV = mxUtils.isTrue(state.getStyle(), mxConstants.STYLE_IMAGE_FLIPV, false);
        canvas.drawImage(this.getImageBounds(canvas, state), this.getImageForStyle(canvas, state), mxGraphics2DCanvas.PRESERVE_IMAGE_ASPECT, flipH, flipV);

    }

    public Shape createShape(mxGraphics2DCanvas canvas, mxCellState state) {
        Rectangle temp = state.getRectangle();
        int min = Math.min(temp.width, temp.height);
        return new Ellipse2D.Float((float)temp.x, (float)temp.y, (float)min, (float)min);
    }

    public Rectangle getImageBounds(mxGraphics2DCanvas canvas, mxCellState state) {
        Rectangle rectangle = state.getRectangle();
        rectangle.grow(-IMAGE_MARGIN, -IMAGE_MARGIN);
        return rectangle;
    }

    public String getImageForStyle(mxGraphics2DCanvas canvas, mxCellState state)
    {
        return canvas.getImageForStyle(state.getStyle());
    }
}
