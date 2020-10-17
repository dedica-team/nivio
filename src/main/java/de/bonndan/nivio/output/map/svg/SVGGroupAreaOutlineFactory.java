package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.output.map.hex.Hex;
import j2html.tags.ContainerTag;
import j2html.tags.DomContent;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.awt.geom.Point2D;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Collects all hexes close to group item hexes to create an outline around an area
 */
public class SVGGroupAreaOutlineFactory {

    private boolean debug = false;

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    @Nullable
    public List<DomContent> getOutline(@NonNull Set<Hex> groupArea, String fillId) {

        if (!groupArea.iterator().hasNext()) {
            return null;
        }

        //find left top
        //start with left top
        Hex start = Hex.topLeft(groupArea);
        List<DomContent> outlines = new ArrayList<>();
        List<DomContent> pointsPath = getOutline(start, groupArea, fillId);
        outlines.addAll(pointsPath);

        return outlines;
    }

    private List<DomContent> getOutline(@NonNull Hex start, @NonNull Set<Hex> groupArea, String fillId) {

        if (groupArea.containsAll(start.neighbours())) {
            throw new IllegalArgumentException("Starting point " + start + " for outline is not on border.");
        }
        LinkedHashSet<Hex> borderHexes = new LinkedHashSet<>();
        Position next = new Position(start, 0);
        while (next != null) {
            //end
            if (borderHexes.contains(next.hex)) {
                break;
            }
            borderHexes.add(next.hex);
            next = getNext(next, groupArea);
        }

        List<Point2D.Double> centers = borderHexes.stream()
                .map(Hex::toPixel)
                .collect(Collectors.toList());

        String pointsPath = WobblyGroupOutline.getPath(centers);
        //String pointsPath = SharpCornersGroupOutline.getPath(centers);
        //String pointsPath = SmoothCornersGroupOutline.getPath(corners);

        /* DEBUG path point order */
        List<DomContent> containerTags = new ArrayList<>();
        if (debug) {
            int i = 0;
            for (Point2D.Double aDouble : centers) {
                containerTags.add(SvgTagCreator.text(i + "")
                        .attr("x", aDouble.x)
                        .attr("y", aDouble.y));
                i++;
            }
        }

        if (debug) {
            /* old style of multiple hexes*/
            List<DomContent> territoryHexes = groupArea.stream()
                    .map(hex -> new SVGHex(hex, fillId, fillId).render())
                    .collect(Collectors.toList());
            containerTags.addAll(territoryHexes);
        }


        ContainerTag svgPath = SvgTagCreator.path()
                .attr("d", pointsPath)
                .condAttr(!StringUtils.isEmpty(fillId), "stroke", fillId)
                .condAttr(!StringUtils.isEmpty(fillId), "fill", fillId)
                .condAttr(!StringUtils.isEmpty(fillId), "fill-opacity", String.valueOf(0.1));

        containerTags.add(svgPath);
        return containerTags;
    }

    /**
     * @param startPosition hex to find path around and point to start from on circumference of hex
     * @param allInGroup    all hexes in the group area
     * @return the position to continue with
     */
    private Position getNext(@NonNull Position startPosition, Set<Hex> allInGroup) {

        Hex start = startPosition.hex;
        final List<Hex> neighbours = start.neighbours();
        if (allInGroup.containsAll(neighbours)) {
            throw new IllegalArgumentException(String.format("Fully enclosed hex %s passed as starting point.", start));
        }

        int repeats = 0;
        boolean foundFreeSide = false;
        int neighboursSize = neighbours.size();

        for (int i = startPosition.rotationOffset; i < neighboursSize; i++) {
            Hex neighbour = neighbours.get(i);

            //return the first group item in rotation direction
            if (allInGroup.contains(neighbour)) {
                //Since we rotate clockwise we know that we hex adjacent to both start and neighbour must be free (otherwise
                //that one would have been returned). Hence the next search should start there.
                return new Position(neighbour, i - 1);
            }

            //5 is end, but if we're still here we need to do one more round
            if (i == neighboursSize - 1) {
                if (repeats < 1) {
                    i = -1; //continue at zero in next cycle
                    repeats++;
                } else {
                    throw new RuntimeException("Could not pick non-empty neighbor");
                }
            }
        }

        throw new RuntimeException("getNext starting at " + start + " could not find neighbour to follow " + neighbours);
    }

    static class Position {

        final Hex hex;
        private final int rotationOffset;

        Position(@NonNull Hex hex, int rotationOffset) {
            this.hex = hex;
            if (rotationOffset == -1)
                rotationOffset = 5;
            this.rotationOffset = rotationOffset;
        }
    }

}