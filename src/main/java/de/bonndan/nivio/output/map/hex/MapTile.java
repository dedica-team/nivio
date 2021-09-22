package de.bonndan.nivio.output.map.hex;

import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import org.springframework.lang.NonNull;

import java.util.Objects;

/**
 * A map tile is a location ({@link Hex})on the map with a state.
 */
public class MapTile {

    private FullyQualifiedIdentifier item;
    private String group;
    private Integer pathDirection;
    private final Hex hex;

    public MapTile(@NonNull final Hex hex) {
        this.hex = Objects.requireNonNull(hex);
    }

    public FullyQualifiedIdentifier getItem() {
        return item;
    }

    public void setItem(FullyQualifiedIdentifier item) {
        this.item = item;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Integer getPathDirection() {
        return pathDirection;
    }

    public void setPathDirection(Integer pathDirection) {
        this.pathDirection = pathDirection;
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
                ", pathDirection=" + pathDirection +
                ", hex=" + hex +
                '}';
    }
}
