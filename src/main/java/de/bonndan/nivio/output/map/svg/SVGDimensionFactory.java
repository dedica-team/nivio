package de.bonndan.nivio.output.map.svg;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Factory to calculate the bounding boxes of a rendered landscape.
 *
 *
 */
public class SVGDimensionFactory {

    static SVGDimension getDimension(List<SVGGroupArea> groupAreas) {

        AtomicInteger minX = new AtomicInteger(Integer.MAX_VALUE);
        AtomicInteger minY = new AtomicInteger(Integer.MAX_VALUE);
        AtomicInteger maxX = new AtomicInteger(Integer.MIN_VALUE);
        AtomicInteger maxY = new AtomicInteger(Integer.MIN_VALUE);

        AtomicInteger minQ = new AtomicInteger(Integer.MAX_VALUE);
        AtomicInteger minR = new AtomicInteger(Integer.MAX_VALUE);
        AtomicInteger maxQ = new AtomicInteger(Integer.MIN_VALUE);
        AtomicInteger maxR = new AtomicInteger(Integer.MIN_VALUE);

        //fix viewport, because xy and hex coordinate system have different offsets
        groupAreas.forEach(svgGroupArea -> {
            svgGroupArea.getGroupArea().forEach(hex -> {
                var pos = hex.toPixel();
                if (pos.x < minX.get()) {
                    minX.set((int) pos.x);
                }

                if (pos.y < minY.get()) {
                    minY.set((int) pos.y);
                }

                if (pos.x > maxX.get()) {
                    maxX.set((int) pos.x);
                }
                if (pos.y > maxY.get()) {
                    maxY.set((int) pos.y);
                }

                // same for hex coords
                if (hex.q < minQ.get()) {
                    minQ.set(hex.q);
                }

                if (hex.q > maxQ.get()) {
                    maxQ.set(hex.q);
                }

                if (hex.r < minR.get()) {
                    minR.set(hex.r);
                }

                if (hex.r > maxR.get()) {
                    maxR.set(hex.r);
                }
            });
        });

        SVGDimension.BoundingBox hex = new SVGDimension.BoundingBox(
                minQ.get(),
                minR.get(),
                maxQ.get(),
                maxR.get()
        );
        SVGDimension.BoundingBox cartesian = new SVGDimension.BoundingBox(
                minX.get(),
                minY.get(),
                maxX.get(),
                maxY.get()
        );

        return new SVGDimension(hex, cartesian);
    }
}
