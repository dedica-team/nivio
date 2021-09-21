package de.bonndan.nivio.output.map.hex;

import org.springframework.lang.NonNull;

import java.util.Objects;

/**
 * A tile (step) of a hex path.
 *
 * Based on https://github.com/helfsoft/astar (no license given).
 */
class PathTile {

    final Hex hex;
    PathTile parent;

    int moveCosts = 0;
    int sumCosts;
    int heuristicCosts;

    PathTile(@NonNull final Hex hex) {
        this.hex = Objects.requireNonNull(hex);
        sumCosts = 0;
        heuristicCosts = 0;
    }

    /**
     * Calculate the heuristic costs from this tile to the destination, then sums up heuristic costs (H) and moveCosts (G)
     *
     * @param dst The destination.
     */
    public void calcHeuristicToDestinationAndSum(PathTile dst) {
        heuristicCosts = hex.distance(dst.hex);
        sumCosts = moveCosts + heuristicCosts;
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof PathTile)) {
            return false;
        }
        PathTile other = (PathTile) obj;

        return other.hex.equals(this.hex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hex);
    }

    public PathTile getParent() {
        return parent;
    }

    public void setParent(PathTile parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        return "Tile{" + "hex=" + hex + '}';
    }
}
