package de.bonndan.nivio.output.map.hex.gojuno;

import de.bonndan.nivio.output.map.hex.Hex;

class FractionalHex {

    private final double q;
    private final double r;

    public FractionalHex(double q, double r) {
        this.q = q;
        this.r = r;
    }

    public double getQ() {
        return q;
    }

    public double getR() {
        return r;
    }

    public double getS() {
        return -(q + r);
    }

    public Hex toHex() {
        int tmpQ = (int) Math.round(getQ());
        int tmpR = (int) Math.round(getR());
        int s = (int) Math.round(getS());
        double qDiff = Math.abs(tmpQ - getQ());
        double rDiff = Math.abs(tmpR - getR());
        double sDiff = Math.abs(s - getS());

        if (qDiff > rDiff && qDiff > sDiff) {
            tmpQ = -(tmpR + s);
        } else if (rDiff > sDiff) {
            tmpR = -(tmpQ + s);
        }

        return new Hex(tmpQ, tmpR);
    }

    @Override
    public String toString() {
        return String.format("fraction_hex{q: %s, r: %s}", q, r);
    }
}