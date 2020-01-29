package de.bonndan.nivio.output.map;

import de.bonndan.nivio.output.map.hex.Hex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;


class PathFinderTest {

    List<TilePath> tilePaths;
    TilePath tilePath1;
    TilePath tilePath2;
    List<Hex> occupied = new ArrayList<>();

    @BeforeEach
    public void setup() {
        tilePaths = new CopyOnWriteArrayList<>();
        tilePath1 = new TilePath(new Hex(0, 0, 0));
        tilePath2 = new TilePath(new Hex(0, 10, -10));
        tilePaths.add(tilePath1);
        tilePaths.add(tilePath2);

        occupied.add(new Hex(0, 1, -1));
        occupied.add(new Hex(0, 5, -5));
        occupied.add(new Hex(5, 5, -10));
        occupied.add(new Hex(7, 3, -10));
    }

    /**
     * Debug messages were enabled,
     * occupied was a list
     */
    @Test
    @Disabled
    public void testOriginal() {
        PathFinder pathFinder = new PathFinder(occupied);
        pathFinder.findPaths(tilePaths, new Hex(10, 10, -20));

        assertEquals(21, pathFinder.getIterations());
        assertEquals(11071, pathFinder.getTimeElapsed()); //machine specific, but for documentation
    }

    /**
     * DEBUG disabled
     */
    @Test
    @Disabled
    public void testOptimizedWithoutPathfindingAlgorithmChanges() {
        final Set<Hex> hexes = new HashSet<>(occupied);
        PathFinder pathFinder = new PathFinder(hexes);
        TilePath best = pathFinder.findPaths(tilePaths, new Hex(10, 10, -20));

        assertEquals(21, pathFinder.getIterations());
        assertEquals(21, best.tiles.size());
        assertEquals(1600, pathFinder.getTimeElapsed());
    }
}