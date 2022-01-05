package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.output.map.hex.Hex;
import de.bonndan.nivio.output.map.hex.MapTile;
import org.springframework.lang.NonNull;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 * Collects all hexes close to group item hexes to create an outline around an area
 */
abstract class GroupAreaOutlineFactory {

    /**
     * Returns all svg components to describe a group outline.
     *
     * @param groupArea all hexes in the group area
     * @param fillId    fill color
     * @return all svg elements forming the group outline
     */
    @NonNull
    abstract List<Component> getOutline(@NonNull final Set<MapTile> groupArea, @NonNull final String fillId);

    /**
     * Returns all hexes defining the area border.
     *
     * @param start     start hex (e.g. top left)
     * @param groupArea all hexes
     * @return hexes in sequence
     */
    protected static LinkedHashMap<Hex, Position> getBorderHexes(Hex start, Set<Hex> groupArea) {
        LinkedHashMap<Hex, Position> borderHexes = new LinkedHashMap<>();
        int startRotationOffset = getStartRotationOffset(start, groupArea);
        Position next = new Position(start, startRotationOffset);
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
     * Finds the starting point on the path of the first hexagon.
     *
     * We follow the hex path counter-clockwise up to the side of the hex where the last element of the path attaches.
     *
     * @param start     starting point of the group outline
     * @param groupArea all in area
     * @return rotation offset of the hexagon path to start from
     */
    static int getStartRotationOffset(Hex start, Set<Hex> groupArea) {
        List<Hex> neighbours = Hex.neighbours(start);
        Collections.reverse(neighbours); //now starting with north-east

        // going counter-clockwise
        var rotationOffset = 5;
        for (int i = 0; i < neighbours.size(); i++) {
            Hex neighbour = neighbours.get(i);
            Hex next = neighbours.get(i + 1);
            if (!groupArea.contains(neighbour) && groupArea.contains(next))
                return rotationOffset;
            rotationOffset--;
        }

        return 0;
    }

    /**
     * @param startPosition hex to find path around and point to start from on circumference of hex
     * @param allInGroup    all hexes in the group area
     * @return the position to continue with
     */
    static Position getNext(@NonNull Position startPosition, Set<Hex> allInGroup) {

        Hex start = startPosition.hex;
        final List<Hex> neighbours = Hex.neighbours(start);
        if (allInGroup.containsAll(neighbours)) {
            throw new IllegalArgumentException(String.format("Fully enclosed hex %s passed as starting point.", start));
        }

        int repeats = 0;
        int neighboursSize = neighbours.size();

        for (int i = startPosition.rotationOffset; i < neighboursSize; i++) {
            Hex neighbour = neighbours.get(i);

            //return the first group item in rotation direction
            if (allInGroup.contains(neighbour)) {
                return getNextPosition(start, i, neighbour);
            }

            //5 is end, but if we're still here we need to do one more round
            if (i == neighboursSize - 1) {
                if (repeats < 1) {
                    i = -1; //continue at zero in next cycle
                    repeats++;
                } else {
                    throw new IllegalStateException("Could not pick non-empty neighbor");
                }
            }
        }

        throw new IllegalStateException(String.format("getNext starting at %s could not find neighbour to follow %s", start, neighbours));
    }

    /**
     * Determines the next position for the path to follow.
     *
     * Since we rotate clockwise we know that the hex adjacent to both start and neighbour must be free (otherwise
     * that one would have been returned). Hence, the next search should start there.
     *
     * We also need to consider the coordinates of the next hex. If one is equal to the current, rotation offset needs
     * to be decreased more.
     *
     * @param current         current hex
     * @param currentRotation current rotation offset
     * @param next            which is the next border tile
     */
    static Position getNextPosition(Hex current, int currentRotation, Hex next) {
        int setBack = next.r == current.r || next.q == current.q ? 2 : 1;
        int rotationOffset = currentRotation - setBack;
        if (rotationOffset < 0)
            rotationOffset = 6 + rotationOffset;
        return new Position(next, rotationOffset);
    }

    public static class Position {

        final Hex hex;
        final int rotationOffset;

        Position(@NonNull final Hex hex, final int rotationOffset) {
            this.hex = hex;
            this.rotationOffset = rotationOffset == -1 ? 5 : rotationOffset;
        }
    }

}