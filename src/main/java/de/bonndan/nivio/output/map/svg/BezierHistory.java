package de.bonndan.nivio.output.map.svg;


import java.awt.geom.Point2D;


public class BezierHistory
{

    Point2D.Float startPoint = new Point2D.Float();
    Point2D.Float lastPoint = new Point2D.Float();
    Point2D.Float lastKnot = new Point2D.Float();

    public BezierHistory()
    {
    }
    
    public void setStartPoint(float x, float y)
    {
        startPoint.setLocation(x, y);
    }
    
    public void setLastPoint(float x, float y)
    {
        lastPoint.setLocation(x, y);
    }
    
    public void setLastKnot(float x, float y)
    {
        lastKnot.setLocation(x, y);
    }
}
