package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.output.map.hex.Hex;
import org.springframework.lang.NonNull;

import java.awt.geom.Point2D;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Creates the path around an ordered set of hexes with form a group border.
 */
public class BorderHexesGroupOutline {

    public static String getPath(@NonNull final LinkedHashMap<Hex, SVGGroupAreaOutlineFactory.Position> borderHexes,
                                 @NonNull final Set<Hex> groupArea
    ) {
        LinkedHashSet<Point2D.Double> path = new LinkedHashSet<>();
        Objects.requireNonNull(borderHexes).forEach((hex, position) -> path.addAll(getPathPointsFor(position, Objects.requireNonNull(groupArea))));

        String points = path.stream()
                .map(aDouble -> String.format("%s %s", (float)aDouble.x, (float)aDouble.y)) //float is enough precision for pixels
                .collect(Collectors.joining(","));

        return String.format("M %s Z", points);
    }

    /**
     * @param start hex to find path around and point to start from on circumference of hex
     * @return the position to continue with
     */
    private static Collection<? extends Point2D.Double> getPathPointsFor(SVGGroupAreaOutlineFactory.Position start, Set<Hex> groupArea) {

        final List<Point2D.Double> ownSegments = new ArrayList<>();
        final List<Point2D.Double> points = start.hex.asPoints(Hex.HEX_SIZE);
        final List<Hex> neighbours = Hex.neighbours(start.hex);

        Hex neighbour;
        Point2D.Double currentPoint;
        int i = start.rotationOffset;
        int repeats = 0;
        for (int neighboursSize = neighbours.size(); i < neighboursSize; i++) {

            neighbour = neighbours.get(i);
            currentPoint = points.get(i);

            //not found any segment yet, then continue
            if (ownSegments.size() == 0 && groupArea.contains(neighbour))
                continue;

            //found a segment because neighbour is free tile
            if (!groupArea.contains(neighbour)) {
                ownSegments.add(currentPoint);

                //5 is end, but if the neighbour is free we need to continue
                if (i == 5 && repeats < 1) {
                    i = -1; //continue at zero in next cycle
                    repeats++;
                }
                continue;
            }

            // neighbour is in territory, so follow it
            ownSegments.add(currentPoint);
            break;
        }

        return ownSegments;
    }
}
