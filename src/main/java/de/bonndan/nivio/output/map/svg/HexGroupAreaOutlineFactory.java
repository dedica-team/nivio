package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.output.map.hex.Hex;
import de.bonndan.nivio.output.map.hex.MapTile;
import org.springframework.lang.NonNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Collects all hexes close to group item hexes to create an outline.
 */
class HexGroupAreaOutlineFactory extends GroupAreaOutlineFactory {

    private boolean debug = false;

    void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * Returns all svg components to describe a group outline.
     *
     * @param groupArea all hexes in the group area
     * @param fillId    fill color
     * @return all svg elements forming the group outline
     */
    @Override
    @NonNull
    public List<Component> getOutline(@NonNull final Set<MapTile> groupArea, @NonNull final String fillId) {

        if (!groupArea.iterator().hasNext()) {
            return new ArrayList<>();
        }

        //find left top and start with it
        Set<Hex> hexes = groupArea.stream().map(MapTile::getHex).collect(Collectors.toUnmodifiableSet());
        Hex start = Hex.topLeft(hexes);

        if (hexes.containsAll(Hex.neighbours(start))) {
            throw new IllegalArgumentException(String.format("Starting point %s for outline is not on border.", start));
        }

        LinkedHashMap<Hex, Position> borderHexes = getBorderHexes(start, hexes);
        List<Component> containerTags = new ArrayList<>();
        var path = BorderHexesGroupOutline.getPath(borderHexes, hexes);
        containerTags.add(new SVGPath(path, fillId));
        /* style of multiple hexes*/
        List<SVGHex> territoryHexes = hexes.stream()
                .map(hex -> new SVGHex(hex, fillId, fillId, debug))
                .collect(Collectors.toList());
        containerTags.addAll(territoryHexes);

        return containerTags;
    }

}