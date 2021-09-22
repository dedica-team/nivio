package de.bonndan.nivio.output.map.hex;

import org.springframework.lang.NonNull;

import java.util.Objects;

/**
 * A tile (step) of a hex path.
 *
 * Based on https://github.com/helfsoft/astar (no license given).
 */
public class PathTile {

    final MapTile mapTile;
    private Integer directionFromParent;
    private PathTile parent;

    int moveCosts = 0;
    int sumCosts;
    int heuristicCosts;

    public PathTile(@NonNull final MapTile mapTile) {
        this(mapTile, null);
    }

    PathTile(@NonNull final MapTile mapTile, Integer directionFromParent) {
        this.mapTile = Objects.requireNonNull(mapTile);
        this.directionFromParent = directionFromParent;
        sumCosts = 0;
        heuristicCosts = 0;
    }

    /**
     * Calculate the heuristic costs from this tile to the destination, then sums up heuristic costs (H) and moveCosts (G)
     *
     * @param dst The destination.
     */
    public void calcHeuristicToDestinationAndSum(PathTile dst) {
        heuristicCosts = mapTile.getHex().distance(dst.mapTile.getHex());
        sumCosts = moveCosts + heuristicCosts;
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof PathTile)) {
            return false;
        }
        PathTile other = (PathTile) obj;

        return other.mapTile.equals(this.mapTile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mapTile);
    }

    public PathTile getParent() {
        return parent;
    }

    public void setParent(PathTile parent) {
        this.parent = parent;
    }

    public Integer getDirectionFromParent() {
        return directionFromParent;
    }

    @NonNull
    public MapTile getMapTile() {
        return mapTile;
    }

    @Override
    public String toString() {
        return "Tile{" + "hex=" + mapTile + '}';
    }

    void setDirectionFromParent(Integer directionFromParent) {
        this.directionFromParent = directionFromParent;
    }
}
