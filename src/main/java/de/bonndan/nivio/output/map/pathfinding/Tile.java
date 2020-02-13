package de.bonndan.nivio.output.map.pathfinding;

import de.bonndan.nivio.output.map.hex.Hex;

/**
 * Based on https://github.com/helfsoft/astar (no license given).
 */
class Tile {

    final Hex hex;
    Tile parent;

    float moveCosts = 0f;
    float sumCosts;
    float heuristicCosts;
    private final boolean occupied;

    public Tile(Hex hex, boolean occupied) {
        this.hex = hex;
        this.occupied = occupied;

        sumCosts = 0f;
        heuristicCosts = 0f;
    }

    /**
     * Calculate the heuristic costs from this tile to the destination.
     *
     * @param dst The destination.
     */
    public void calcHeuristic(Tile dst) {
        heuristicCosts = hex.distance(dst.hex);
    }

    /**
     * Sum up heuristic costs (H) and moveCosts (G)
     */
    public void sumHeuristicAndMoveCosts() {
        sumCosts = moveCosts + heuristicCosts;
    }

    /**
     * Calculates the movecosts from one tile to this tile. If this tile is a
     * wall then increase the costs by factor 10 to avoid walls.
     *
     * @param from The tile from which we move to this tile
     * @return The move cost from "from" to "this"
     */
    public float calcMoveCostsFrom(Tile from) {
        float cost = 1f;

        if (this.isOccupied()) {
            cost *= 10f;
        }

        return cost + from.moveCosts;
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof Tile)) {
            return false;
        }
        Tile other = (Tile) obj;

        return other.hex.equals(this.hex);
    }

    public Tile getParent() {
        return parent;
    }

    public void setParent(Tile parent) {
        this.parent = parent;
    }

    public boolean isOccupied() {
        return occupied;
    }

    @Override
    public String toString() {
        return "Tile{" +
                "hex=" + hex +
                ", occupied=" + occupied +
                '}';
    }
}
