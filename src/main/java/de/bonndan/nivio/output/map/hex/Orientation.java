package de.bonndan.nivio.output.map.hex;

public class Orientation {

    static Orientation LAYOUT_FLAT = new Orientation(3.0 / 2.0, 0.0, Math.sqrt(3.0) / 2.0, Math.sqrt(3.0), 2.0 / 3.0, 0.0, -1.0 / 3.0, Math.sqrt(3.0) / 3.0, 0.0);

    private final double startAngle;
    public double f0;
    public double f1;
    public double f2;
    public double f3;

    private final double b0;
    private final double b1;
    private final double b2;
    private final double b3;


    Orientation(double f0, double f1, double f2, double f3, double b0, double b1, double b2, double b3, double startAngle) {

        this.f0 = f0;
        this.f1 = f1;
        this.f2 = f2;
        this.f3 = f3;
        this.b0 = b0;
        this.b1 = b1;
        this.b2 = b2;
        this.b3 = b3;
        this.startAngle = startAngle;
    }

}
