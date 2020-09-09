package de.bonndan.nivio.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GroupTest {

    @Test
    void doesNotAllowEmptyIdentifier() {
        assertThrows(Exception.class, () -> new Group(""));
    }

    @Test
    void getItemsIsImmutable() {
        Group g = new Group("foo");
        assertThrows(Exception.class, () -> g.getItems().add(new Item("a", "b")));
    }

    @Test
    void addItemAllowed() {
        Group g = new Group("foo");
        Item item = new Item("foo", "b");
        g.addItem(item);
        assertEquals(1, g.getItems().size());
    }

    @Test
    void addItemAllowedWithUnsetGroup() {
        Group g = new Group("foo");
        Item item = new Item(null, "b");
        g.addItem(item);
        assertEquals(1, g.getItems().size());
    }

    @Test
    void addItemForbidden() {
        Group g = new Group("foo");
        Item item = new Item("a", "b");
        assertThrows(IllegalArgumentException.class, () -> g.addItem(item));
    }
}