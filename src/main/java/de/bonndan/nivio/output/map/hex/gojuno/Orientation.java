package de.bonndan.nivio.output.map.hex.gojuno;

/**
 * Copied from https://github.com/gojuno/hexgrid-java/blob/master/src/main/java/com/gojuno/hexgrid/Orientation.java
 */
public class Orientation {

    public static final Orientation FLAT = new Orientation(
            "flat",
            new double[]{3.0 / 2.0, 0.0, Math.sqrt(3.0) / 2.0, Math.sqrt(3.0)},
            new double[]{2.0 / 3.0, 0.0, -1.0 / 3.0, Math.sqrt(3.0) / 3.0},
            0.0);

    private String name;
    private double[] f;
    private double[] b;
    private double startAngle;
    private double[] sinuses;
    private double[] consinuses;

    private Orientation(String name, double[] f, double[] b, double startAngle) {
        this.name = name;
        this.f = f;
        this.b = b;
        this.startAngle = startAngle;
        prehashAngles();
    }

    public double[] getF() {
        return f;
    }

    public double[] getB() {
        return b;
    }

    public double getStartAngle() {
        return startAngle;
    }

    public double[] getSinuses() {
        return sinuses;
    }

    public double[] getCosinuses() {
        return consinuses;
    }

    private void prehashAngles() {
        sinuses = new double[6];
        consinuses = new double[6];
        for (int i = 0; i < 6; i++) {
            double angle = 2.0 * Math.PI * (i + getStartAngle()) / 6.0;
            sinuses[i] = Math.sin(angle);
            consinuses[i] = Math.cos(angle);
        }
    }

    @Override
    public String toString() {
        return String.format("orientation{name: %s}", name);
    }
}