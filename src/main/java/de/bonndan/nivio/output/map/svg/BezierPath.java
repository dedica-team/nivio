package de.bonndan.nivio.output.map.svg;

import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BezierPath {

    static final Matcher matchPoint = Pattern.compile("\\s*(\\d+)[^\\d]+(\\d+)\\s*").matcher("");

    BezierListProducer path;

    /**
     * Creates a new instance of Animate
     */
    public BezierPath() {
    }

    public void parsePathString(String d) {

        this.path = new BezierListProducer();

        parsePathList(d);
    }

    protected void parsePathList(String list) {
        final Matcher matchPathCmd = Pattern.compile("([MmLlHhVvAaQqTtCcSsZz])|([-+]?((\\d*\\.\\d+)|(\\d+))([eE][-+]?\\d+)?)").matcher(list);

        //Tokenize
        LinkedList<String> tokens = new LinkedList<>();
        while (matchPathCmd.find()) {
            tokens.addLast(matchPathCmd.group());
        }

        char curCmd = 'Z';
        while (!tokens.isEmpty()) {
            String curToken = tokens.removeFirst();
            char initChar = curToken.charAt(0);
            if ((initChar >= 'A' && initChar <= 'Z') || (initChar >= 'a' && initChar <= 'z')) {
                curCmd = initChar;
            } else {
                tokens.addFirst(curToken);
            }

            switch (curCmd) {
                case 'M':
                    path.movetoAbs(nextFloat(tokens), nextFloat(tokens));
                    curCmd = 'L';
                    break;
                case 'm':
                    path.movetoRel(nextFloat(tokens), nextFloat(tokens));
                    curCmd = 'l';
                    break;
                case 'L':
                    path.linetoAbs(nextFloat(tokens), nextFloat(tokens));
                    break;
                case 'l':
                    path.linetoRel(nextFloat(tokens), nextFloat(tokens));
                    break;
                case 'H':
                    path.linetoHorizontalAbs(nextFloat(tokens));
                    break;
                case 'h':
                    path.linetoHorizontalRel(nextFloat(tokens));
                    break;
                case 'V':
                    path.linetoVerticalAbs(nextFloat(tokens));
                    break;
                case 'v':
                    path.linetoVerticalAbs(nextFloat(tokens));
                    break;
                case 'A':
                case 'a':
                    break;
                case 'Q':
                    path.curvetoQuadraticAbs(nextFloat(tokens), nextFloat(tokens),
                            nextFloat(tokens), nextFloat(tokens));
                    break;
                case 'q':
                    path.curvetoQuadraticAbs(nextFloat(tokens), nextFloat(tokens),
                            nextFloat(tokens), nextFloat(tokens));
                    break;
                case 'T':
                    path.curvetoQuadraticSmoothAbs(nextFloat(tokens), nextFloat(tokens));
                    break;
                case 't':
                    path.curvetoQuadraticSmoothRel(nextFloat(tokens), nextFloat(tokens));
                    break;
                case 'C':
                    path.curvetoCubicAbs(nextFloat(tokens), nextFloat(tokens),
                            nextFloat(tokens), nextFloat(tokens),
                            nextFloat(tokens), nextFloat(tokens));
                    break;
                case 'c':
                    path.curvetoCubicRel(nextFloat(tokens), nextFloat(tokens),
                            nextFloat(tokens), nextFloat(tokens),
                            nextFloat(tokens), nextFloat(tokens));
                    break;
                case 'S':
                    path.curvetoCubicSmoothAbs(nextFloat(tokens), nextFloat(tokens),
                            nextFloat(tokens), nextFloat(tokens));
                    break;
                case 's':
                    path.curvetoCubicSmoothRel(nextFloat(tokens), nextFloat(tokens),
                            nextFloat(tokens), nextFloat(tokens));
                    break;
                case 'Z':
                case 'z':
                    path.closePath();
                    break;
                default:
                    throw new RuntimeException("Invalid path element");
            }
        }
    }

    protected static float nextFloat(LinkedList<String> l) {
        String s = l.removeFirst();
        return Float.parseFloat(s);
    }

    /**
     * Evaluates this animation element for the passed interpolation time.  Interp
     * must be on [0..1].
     */
    public Point2D.Float eval(double interp) {
        Point2D.Float point = new Point2D.Float();


        double curLength = path.curveLength * interp;
        for (Bezier bez : path.bezierSegs) {
            double bezLength = bez.getLength();
            if (curLength < bezLength) {
                double param = curLength / bezLength;
                bez.eval(param, point);
                break;
            }

            curLength -= bezLength;
        }

        return point;
    }

    /**
     * Calculates the angle at the given piece of the path.
     *
     * @param distanceToEnd in px/map units
     * @param yOffset       extra x offset to add
     * @param yOffset       extra y offset to add
     * @return point and atan
     */
    public PointWithAngle angleAtEnd(int distanceToEnd, int xOffset, int yOffset) {

        float start = (path.curveLength - distanceToEnd) / path.curveLength;
        double end = start + 0.0001;
        return angleAt(start, end, xOffset, yOffset, false);
    }

    /**
     * Calculates the angle at the given piece of the path.
     *
     * @param start   relative start point
     * @param end     relative end point
     * @param yOffset extra y offset to add
     * @param upright toogle that angle is calculated so that text is always upright
     * @return point and atan
     */
    public PointWithAngle angleAt(double start, double end, int xOffset, int yOffset, boolean upright) {

        Point2D.Float point1 = eval(start);
        point1.setLocation(point1.x + xOffset, point1.y + yOffset);
        Point2D.Float point2 = eval(end);
        point2.setLocation(point2.x+ xOffset, point2.y + yOffset);

        var degrees = Math.atan2((point2.y - point1.y), (point2.x - point1.x)) * 180 / Math.PI;
        if (upright && (degrees > 90 || degrees < -90)) {
            degrees += 180; //always upright
        }

        return new PointWithAngle(point1, degrees);
    }

    static class PointWithAngle {
        final Point2D.Float point;
        final double degrees;

        public PointWithAngle(Point2D.Float point, double degrees) {
            this.point = point;
            this.degrees = degrees;
        }
    }
}
