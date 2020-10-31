package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.output.map.hex.Hex;

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

    static class BoundingBox {
        int padding = 3 * Hex.HEX_SIZE;
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

        /**
         * Returns the proper values for a SVG viewbox.
         */
        public String asViewBox() {
            return String.format("%d %d %d %d",
                    horMin - padding,
                    vertMin - padding,
                    horMax - horMin + 2 * padding,
                    vertMax - vertMin + 2 * padding);
        }
    }
}
