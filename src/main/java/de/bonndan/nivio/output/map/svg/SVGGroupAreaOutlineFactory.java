package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.output.map.hex.Hex;
import j2html.tags.ContainerTag;
import j2html.tags.DomContent;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Collects all hexes close to group item hexes to create an outline around an area
 */
class SVGGroupAreaOutlineFactory {

    private final GroupAreaStyle groupAreaStyle;

    private boolean debug = false;


    SVGGroupAreaOutlineFactory(@NonNull final GroupAreaStyle groupAreaStyle) {
        this.groupAreaStyle = groupAreaStyle;
    }

    void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * @param groupArea all hexes in the group area
     * @param fillId    fill color
     * @return all svg elements forming the group outline
     *
     */
    @NonNull
    public List<DomContent> getOutline(@NonNull final Set<Hex> groupArea, @NonNull final String fillId) {

        if (!groupArea.iterator().hasNext()) {
            return new ArrayList<>();
        }

        //find left top
        //start with left top
        Hex start = Hex.topLeft(groupArea);
        return getOutline(start, groupArea, fillId);
    }

    private List<DomContent> getOutline(@NonNull final Hex start, @NonNull final Set<Hex> groupArea, String fillId) {

        if (groupArea.containsAll(start.neighbours())) {
            throw new IllegalArgumentException(String.format("Starting point %s for outline is not on border.", start));
        }

        LinkedHashMap<Hex, Position> borderHexes = getBorderHexes(start, groupArea);
        List<Point2D.Double> centers = borderHexes.keySet().stream()
                .map(Hex::toPixel)
                .collect(Collectors.toList());

        List<DomContent> containerTags = new ArrayList<>();
        String pointsPath = null;
        switch (groupAreaStyle) {
            case SHARP:
                pointsPath = SharpCornersGroupOutline.getPath(centers);
                break;
            case SMOOTH:
                pointsPath = SmoothCornersGroupOutline.getPath(centers);
                break;
            case WOBBLY:
                pointsPath = WobblyGroupOutline.getPath(centers);
                break;
            case HEXES:
                pointsPath = BorderHexesGroupOutline.getPath(borderHexes, groupArea);
                /* style of multiple hexes*/
                List<DomContent> territoryHexes = groupArea.stream()
                        .map(hex -> new SVGHex(hex, fillId, fillId).render())
                        .collect(Collectors.toList());
                containerTags.addAll(territoryHexes);
                break;
        }


        /* DEBUG path point order */
        if (debug) {
            int i = 0;
            for (Point2D.Double aDouble : centers) {
                containerTags.add(SvgTagCreator.text(i + "")
                        .attr("x", aDouble.x)
                        .attr("y", aDouble.y));
                i++;
            }
        }

        if (pointsPath != null) {
            ContainerTag svgPath = SvgTagCreator.path()
                    .attr("d", pointsPath)
                    .attr("fill", "none")
                    .condAttr(!StringUtils.isEmpty(fillId), "stroke", fillId)
                    .attr("stroke-width", 3);
            containerTags.add(svgPath);
        }

        return containerTags;
    }

    static LinkedHashMap<Hex, Position> getBorderHexes(Hex start, Set<Hex> groupArea) {
        LinkedHashMap<Hex, Position> borderHexes = new LinkedHashMap<>();
        Position next = new Position(start, 0);
        while (next != null) {
            //end
            if (borderHexes.containsKey(next.hex)) {
                break;
            }
            borderHexes.put(next.hex, next);
            next = getNext(next, groupArea);
        }

        return borderHexes;
    }

    /**
     * @param startPosition hex to find path around and point to start from on circumference of hex
     * @param allInGroup    all hexes in the group area
     * @return the position to continue with
     */
    private static Position getNext(@NonNull Position startPosition, Set<Hex> allInGroup) {

        Hex start = startPosition.hex;
        final List<Hex> neighbours = start.neighbours();
        if (allInGroup.containsAll(neighbours)) {
            throw new IllegalArgumentException(String.format("Fully enclosed hex %s passed as starting point.", start));
        }

        int repeats = 0;
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
        final int rotationOffset;

        Position(@NonNull Hex hex, int rotationOffset) {
            this.hex = hex;
            if (rotationOffset == -1)
                rotationOffset = 5;
            this.rotationOffset = rotationOffset;
        }
    }

    public enum GroupAreaStyle {
        SHARP,
        WOBBLY,
        SMOOTH,
        HEXES
    }
}