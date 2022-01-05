package de.bonndan.nivio.output.map.svg;

/**
 * Value object representing a svg documents dimensions.
 */
public class SVGDimension {

    final BoundingBox hex;
    final BoundingBox cartesian;


    /**
     * @param hex       lowest and highest hex (q,r) coordinates
     * @param cartesian lowest and highest hex (x,y) coordinates
     */
    public SVGDimension(BoundingBox hex, BoundingBox cartesian) {
        this.hex = hex;
        this.cartesian = cartesian;
    }

    public static class BoundingBox {
        final int horMin;
        final int vertMin;
        final int horMax;
        final int vertMax;

        /**
         * @param horMin  lowest horizontal
         * @param vertMin lowest vertical
         * @param horMax  highest horizontal
         * @param vertMax highest vertical
         */
        BoundingBox(int horMin, int vertMin, int horMax, int vertMax) {
            this.horMin = horMin;
            this.vertMin = vertMin;
            this.horMax = horMax;
            this.vertMax = vertMax;
        }

        @Override
        public String toString() {
            return String.format("%d %d %d %d", horMin, vertMin, horMax, vertMax);
        }

        public int getHeight() {
            return vertMax - vertMin;
        }

        public int getWidth() {
            return horMax - horMin;
        }
    }
}
