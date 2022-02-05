package de.bonndan.nivio.output.map.hex;

import org.springframework.lang.NonNull;

import java.net.URI;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A map tile is a location ({@link Hex}) on the map with a state.
 */
public class MapTile {

    private URI item;
    private String group;
    private final Set<Integer> pathDirections = new HashSet<>();
    private final Hex hex;
    private final AtomicInteger portCount = new AtomicInteger(0);

    public MapTile(@NonNull final Hex hex) {
        this.hex = Objects.requireNonNull(hex);
    }

    public URI getItem() {
        return item;
    }

    public void setItem(URI item) {
        this.item = item;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Set<Integer> getPathDirections() {
        return pathDirections;
    }

    public void addPathDirection(Integer pathDirection) {
        this.pathDirections.add(Objects.requireNonNull(pathDirection));
    }

    public Hex getHex() {
        return hex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MapTile mapTile = (MapTile) o;
        return hex.equals(mapTile.hex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hex);
    }

    @Override
    public String toString() {
        return "MapTile{" +
                "item=" + item +
                ", group='" + group + '\'' +
                ", pathDirection=" + pathDirections +
                ", hex=" + hex +
                '}';
    }

    /**
     * Set that a path end on this tile.
     *
     * @return the old number of relations ending on the tile (starting at 0)
     */
    public int incrementPortCount() {
        return portCount.getAndIncrement();
    }

    public int getPortCount() {
        return portCount.get();
    }
}
