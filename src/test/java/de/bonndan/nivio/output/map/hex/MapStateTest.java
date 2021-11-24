package de.bonndan.nivio.output.map.hex;

import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.ItemFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MapStateTest {

    private MapState mapState;
    private Item testItem;

    @BeforeEach
    void setUp() {
        mapState = new MapState();
        testItem = ItemFactory.getTestItem("bar", "foo");
    }

    @Test
    void notContains() {
        assertThat(mapState.contains(new Hex(0,0))).isFalse();
    }

    @Test
    void containsEqual() {
        mapState.add(new MapTile(new Hex(0,0)), "foo");
        assertThat(mapState.contains(new Hex(0,0))).isTrue();
    }

    @Test
    void containsSame() {
        MapTile mapTile = new MapTile(new Hex(0, 0));
        mapState.add(mapTile, "foo");
        assertThat(mapState.contains(mapTile.getHex())).isTrue();
    }

    @Test
    void hasItem() {
        MapTile mapTile = new MapTile(new Hex(0, 0));
        mapTile.setItem(testItem.getFullyQualifiedIdentifier());
        mapState.add(mapTile, "foo");
        assertThat(mapState.hasItem(mapTile.getHex())).isTrue();
    }

    @Test
    void hasNoItem() {
        MapTile mapTile = new MapTile(new Hex(0, 0));
        mapState.add(mapTile, "foo");
        assertThat(mapState.hasItem(mapTile.getHex())).isFalse();
    }

    @Test
    void hasItemWithoutHex() {
        assertThat(mapState.hasItem(new Hex(0,0))).isFalse();
    }

    @Test
    void add() {

        MapTile mapTile = new MapTile(new Hex(0, 0));
        MapTile added = mapState.add(mapTile, "foo");

        assertThat(mapState.contains(added.getHex())).isTrue();
        assertThat(mapState.contains(mapTile.getHex())).isTrue();
        assertThat(added).isSameAs(mapTile);
    }

    @Test
    void addWithExisting() {

        MapTile mapTile = new MapTile(new Hex(0, 0));
        mapState.add(mapTile, UUID.randomUUID().toString());

        MapTile other = new MapTile(new Hex(0, 0));
        other.setItem(testItem.getFullyQualifiedIdentifier());
        other.setGroup(testItem.getFullyQualifiedIdentifier().getGroup());

        //when
        MapTile inMap = mapState.add(other, testItem);

        assertThat(inMap).isSameAs(mapTile);
        assertThat(inMap.getGroup()).isEqualTo(testItem.getGroup());
        assertThat(inMap.getItem()).isEqualTo(testItem.getFullyQualifiedIdentifier());
    }

    @Test
    void getHexForItem() {
        Item testItem = ItemFactory.getTestItem("foo", "bar");
        MapTile mapTile = new MapTile(new Hex(0, 0));
        mapTile.setItem(testItem.getFullyQualifiedIdentifier());
        mapTile.setGroup("foo");

        mapState.add(mapTile, testItem);

        //when
        Optional<MapTile> hexForItem = mapState.getHexForItem(testItem);

        //then
        assertThat(hexForItem).isPresent().get().isEqualTo(mapTile);

    }

    @Test
    void getOrAdd() {
        Hex hex = new Hex(0, 0);
        MapTile key = mapState.getOrAdd(hex);
        assertThat(key.getHex()).isSameAs(hex);
    }

    @Test
    void getOrAddExisting() {
        Hex hex = new Hex(0, 0);
        mapState.add(new MapTile(hex), "foo");

        //when
        MapTile key = mapState.getOrAdd(new Hex(0,0));
        assertThat(hex).isSameAs(key.getHex());
    }
}