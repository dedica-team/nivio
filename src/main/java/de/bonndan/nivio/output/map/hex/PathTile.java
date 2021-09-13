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

    float moveCosts = 0f;
    float sumCosts;
    float heuristicCosts;

    public PathTile(@NonNull final Hex hex) {
        this.hex = Objects.requireNonNull(hex);
        sumCosts = 0f;
        heuristicCosts = 0f;
    }

    /**
     * Calculate the heuristic costs from this tile to the destination.
     *
     * @param dst The destination.
     */
    public void calcHeuristic(PathTile dst) {
        heuristicCosts = hex.distance(dst.hex);
    }

    /**
     * Sum up heuristic costs (H) and moveCosts (G)
     */
    public void sumHeuristicAndMoveCosts() {
        sumCosts = moveCosts + heuristicCosts;
    }

    /**
     * Calculates the movecosts from one tile to this tile.
     *
     * If this tile is occupied by an item then increase the costs by factor 10 (like a wall).
     * If this tile is not occupied by an item but from has a different group the costs are slighty raised (like a bump).
     *
     * @param from The tile from which we move to this tile
     * @return The move cost from "from" to "this"
     */
    public float calcMoveCostsFrom(PathTile from) {
        float cost = 1f;

        if (this.hex.item != null) {
            cost *= 10f;
        } else {
            if (this.hex.group != null) {
                cost *= 3f;
            }
        }

        return cost + from.moveCosts;
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof PathTile)) {
            return false;
        }
        PathTile other = (PathTile) obj;

        return other.hex.equals(this.hex);
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
