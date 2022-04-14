package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.output.map.hex.Hex;
import de.bonndan.nivio.output.map.hex.PathTile;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * Factory to calculate the bounding boxes of a rendered landscape.
 */
public class SVGDimensionFactory {

    private SVGDimensionFactory() {
    }

    /**
     * Returns the outer coordinates for the given list of hexes.
     *
     * @param hexes list of hexes
     * @return bounding boxes
     */
    public static SVGDimension getDimension(@NonNull final List<Hex> hexes) {

        AtomicInteger minX = new AtomicInteger(Integer.MAX_VALUE);
        AtomicInteger minY = new AtomicInteger(Integer.MAX_VALUE);
        AtomicInteger maxX = new AtomicInteger(Integer.MIN_VALUE);
        AtomicInteger maxY = new AtomicInteger(Integer.MIN_VALUE);

        AtomicInteger minQ = new AtomicInteger(Integer.MAX_VALUE);
        AtomicInteger minR = new AtomicInteger(Integer.MAX_VALUE);
        AtomicInteger maxQ = new AtomicInteger(Integer.MIN_VALUE);
        AtomicInteger maxR = new AtomicInteger(Integer.MIN_VALUE);

        Consumer<Hex> setBounds = hex -> {
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
        };
        hexes.forEach(setBounds);

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

    static SVGDimension getDimension(List<SVGGroupArea> groupAreas, Collection<SVGRelation> relations) {

        List<Hex> hexes = new ArrayList<>();
        //fix viewport, because xy and hex coordinate system have different offsets
        groupAreas.forEach(svgGroupArea -> svgGroupArea.getGroupArea().forEach(t -> hexes.add(t.getHex())));

        relations.forEach(svgRelation -> {
            for (PathTile pathTile : svgRelation.getHexPath().getTiles()) {
                hexes.add(pathTile.getMapTile().getHex());
            }
        });

        return getDimension(hexes);
    }
}
