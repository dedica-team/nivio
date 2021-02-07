package de.bonndan.nivio.model;

import org.junit.jupiter.api.Test;

import static de.bonndan.nivio.model.ItemFactory.getTestItem;
import static org.junit.jupiter.api.Assertions.*;

class GroupTest {

    @Test
    void doesNotAllowEmptyIdentifier() {
        assertThrows(Exception.class, () -> new Group("", null));
    }

    @Test
    void getItemsIsImmutable() {
        Group g = new Group("foo", null);
        assertThrows(Exception.class, () -> g.getItems().add(getTestItem("a", "b")));
    }

    @Test
    void addItemAllowed() {
        Group g = new Group("foo", null);
        Item item = getTestItem("foo", "b");
        g.addItem(item);
        assertEquals(1, g.getItems().size());
    }

    @Test
    void addItemForbidden() {
        Group g = new Group("foo", null);
        Item item = getTestItem("a", "b");
        assertThrows(IllegalArgumentException.class, () -> g.addItem(item));
    }
}