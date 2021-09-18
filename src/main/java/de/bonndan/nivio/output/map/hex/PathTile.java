package de.bonndan.nivio.output.map.hex;

import org.springframework.lang.NonNull;

import java.util.Objects;

/**
 * A tile (step) of a hex path.
 *
 * Based on https://github.com/helfsoft/astar (no license given).
 */
class PathTile {

    /**
     * Costs for moving through an item
     */
    public static final int ITEM_PENALTY = 1000;

    /**
     * costs for moving on group area
     */
    public static final int GROUP_PENALTY = 3;

    /**
     * costs for moving on tile has is part of a path area
     */
    private static final int PATH_PENALTY = 2;

    /**
     * Regular movement costs.
     */
    public static final int BASE_COSTS = 1;

    final Hex hex;
    PathTile parent;

    int moveCosts = 0;
    int sumCosts;
    int heuristicCosts;

    public PathTile(@NonNull final Hex hex) {
        this.hex = Objects.requireNonNull(hex);
        sumCosts = 0;
        heuristicCosts = 0;
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
    public int calcMoveCostsFrom(PathTile from) {

        if (this.hex.item != null) {
            return ITEM_PENALTY + from.moveCosts;
        }

        if (this.hex.group != null) {
            return GROUP_PENALTY + from.moveCosts;
        }

        if (this.hex.getPathDirection() != null) {
            return PATH_PENALTY + from.moveCosts;
        }

        return BASE_COSTS + from.moveCosts;
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
