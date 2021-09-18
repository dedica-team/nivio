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

    @BeforeEach
    void setUp() {
        mapState = new MapState();
    }

    @Test
    void notContains() {
        assertThat(mapState.contains(new Hex(0,0))).isFalse();
    }

    @Test
    void containsEqual() {
        mapState.add(new Hex(0,0), "foo");
        assertThat(mapState.contains(new Hex(0,0))).isTrue();
    }

    @Test
    void containsSame() {
        Hex hex = new Hex(0, 0);
        mapState.add(hex, "foo");
        assertThat(mapState.contains(hex)).isTrue();
    }

    @Test
    void hasItem() {
        Hex hex = new Hex(0, 0);
        hex.item = "foo";
        mapState.add(hex, "foo");
        assertThat(mapState.hasItem(hex)).isTrue();
    }

    @Test
    void hasNoItem() {
        Hex hex = new Hex(0, 0);
        mapState.add(hex, "foo");
        assertThat(mapState.hasItem(hex)).isFalse();
    }

    @Test
    void hasItemWithoutHex() {
        assertThat(mapState.hasItem(new Hex(0,0))).isFalse();
    }

    @Test
    void add() {

        Hex hex = new Hex(0, 0);
        Hex added = mapState.add(hex, "foo");

        assertThat(mapState.contains(added)).isTrue();
        assertThat(mapState.contains(hex)).isTrue();
        assertThat(added).isSameAs(hex);
    }

    @Test
    void addWithExisting() {

        Hex hex = new Hex(0, 0);
        mapState.add(hex, UUID.randomUUID().toString());

        Hex other = new Hex(0, 0);
        other.item = "bar";
        other.group = "foo";
        Hex inMap = mapState.add(other, ItemFactory.getTestItem("foo", "bar"));

        assertThat(inMap).isSameAs(hex);
        assertThat(inMap.group).isEqualTo("foo");
        assertThat(inMap.item).isEqualTo("bar");
    }

    @Test
    void getHexForItem() {
        Hex hex = new Hex(0, 0);
        hex.item = "bar";
        hex.group = "foo";
        Item testItem = ItemFactory.getTestItem("foo", "bar");
        mapState.add(hex, testItem);

        //when
        Optional<Hex> hexForItem = mapState.getHexForItem(testItem);

        //then
        assertThat(hexForItem).isPresent().get().isEqualTo(hex);

    }

    @Test
    void getOrAdd() {
        Hex hex = new Hex(0, 0);
        Hex key = mapState.getOrAdd(hex);
        assertThat(hex).isSameAs(key);
    }

    @Test
    void getOrAddExisting() {
        Hex hex = new Hex(0, 0);
        mapState.add(hex, "foo");

        //when
        Hex key = mapState.getOrAdd(new Hex(0,0));
        assertThat(hex).isSameAs(key);
    }
}