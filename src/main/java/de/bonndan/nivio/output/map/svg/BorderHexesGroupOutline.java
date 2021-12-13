package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.output.map.hex.Hex;
import de.bonndan.nivio.output.map.hex.PathElement;
import org.springframework.lang.NonNull;

import java.awt.geom.Point2D;
import java.util.*;

import static de.bonndan.nivio.output.map.hex.PathElement.cmd;

/**
 * Creates the path around an ordered set of hexes with form a group border.
 */
public class BorderHexesGroupOutline {

    private BorderHexesGroupOutline() {}

    public static List<PathElement> getPath(@NonNull final LinkedHashMap<Hex, SVGGroupAreaOutlineFactory.Position> borderHexes,
                                            @NonNull final Set<Hex> groupArea
    ) {
        LinkedHashSet<Point2D.Double> path = new LinkedHashSet<>();
        Objects.requireNonNull(borderHexes).forEach((hex, position) -> path.addAll(getPathPointsFor(position, Objects.requireNonNull(groupArea))));

        List<PathElement> pathElements = new ArrayList<>();
        pathElements.add(cmd("M"));
        path.stream().map(PathElement::pt).forEach(pathElements::add);
        pathElements.add(cmd("Z"));

        return pathElements;
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
            if (ownSegments.isEmpty() && groupArea.contains(neighbour))
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
