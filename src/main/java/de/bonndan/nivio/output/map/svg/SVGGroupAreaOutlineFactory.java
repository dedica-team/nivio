package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.output.map.hex.Hex;
import j2html.tags.ContainerTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.awt.geom.Point2D;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Collects all hexes close to group item hexes to create an outline around an area
 *
 *
 */
public class SVGGroupAreaOutlineFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(SVGGroupAreaOutlineFactory.class);

    private final Set<Hex> processed = new HashSet<>();
    private final Set<Hex> groupArea;


    public SVGGroupAreaOutlineFactory(@NonNull Set<Hex> groupArea) {
        this.groupArea = Objects.requireNonNull(groupArea);
    }

    @Nullable
    public List<ContainerTag> getOutline(String fillId) {

        if (!groupArea.iterator().hasNext()) {
            return null;
        }

        //filter all which cannot have an outline
        groupArea.stream().filter(hex -> completelySurrounded(hex)).forEach(hex -> processed.add(hex));

        List<ContainerTag> outlines = new ArrayList<>();
        while(processed.size() < groupArea.size()) {
            ContainerTag pointsPath = getContainer(fillId);
            if (pointsPath != null) {
                outlines.add(pointsPath);
            }
        }

        /* DEBUG path point order
        List<ContainerTag> markers = new ArrayList<>();
        int i = 0;
        for (Point2D.Double aDouble : path) {
            markers.add(SvgTagCreator.text(i + "")
                    .attr("x", aDouble.x)
                    .attr("y", aDouble.y));
            i++;
        }
        territoryHexes.addAll(markers);
         */
        return outlines;
    }

    private ContainerTag getContainer(String fillId) {
        LinkedHashSet<Point2D.Double> path = new LinkedHashSet<>();
        Hex next = findUnprocessed(groupArea);
        if (next == null)
            return null;
        Position start = new Position(next, 0);
        while (next != null) {
            Position newPos = getPathPointsFor(start, path);
            start = newPos;
            if (newPos != null) {
                next = newPos.hex;
                if (next != null && !groupArea.contains(next)) {
                    throw new RuntimeException("Outline algorithm returned a new start hex which is not in group area: " + next);
                }
            } else {
                next = null;
            }
        }

        /* old style of multiple hexes
        List<DomContent> territoryHexes = groupArea.stream()
                .map(hex -> new SVGHex(hex, fillId).render())
                .collect(Collectors.toList());
         */

        String points = path.stream()
                .map(aDouble -> aDouble.x + " " + aDouble.y)
                .collect(Collectors.joining(","));

        return SvgTagCreator.polygon()
                .condAttr(!StringUtils.isEmpty(fillId), "stroke", fillId)
                .condAttr(!StringUtils.isEmpty(fillId), "fill", fillId)
                .condAttr(!StringUtils.isEmpty(fillId), "fill-opacity", String.valueOf(0.1))
                .attr("stroke-width", 1)
                .attr("points", points);
    }

    @Nullable
    private Hex findUnprocessed(Set<Hex> groupArea) {
        for (Hex hex : groupArea) {
            if (!processed.contains(hex))
                return hex;
        }
        return null;
    }

    private boolean completelySurrounded(Hex hex) {
        return groupArea.containsAll(hex.neighbours());
    }

    /**
     * @param start hex to find path around and point to start from on circumference of hex
     * @param path  all group outline segments found so far
     * @return the position to continue with
     */
    private Position getPathPointsFor(@NonNull Position start, @NonNull LinkedHashSet<Point2D.Double> path) {

        if (processed.contains(start.hex)) {
            return null;
        }

        final List<Point2D.Double> ownSegments = new ArrayList<>();
        final List<Point2D.Double> points = start.hex.asPoints(Hex.HEX_SIZE);
        final List<Hex> neighbours = start.hex.neighbours();

        Hex neighbour = null;
        Point2D.Double currentPoint = null;
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


        //have some segments and next
        path.addAll(ownSegments);

        //add to processed --after a full iteration
        processed.add(start.hex);

        i = i - 2;
        if (i < 0)
            i = 5 + i;
        return new Position(groupArea.contains(neighbour) ? neighbour : null, i);
    }

    static class Position {

        final Hex hex;
        private final int rotationOffset;

        Position(@Nullable Hex hex, int rotationOffset) {
            this.hex = hex;
            this.rotationOffset = rotationOffset;
        }
    }

}