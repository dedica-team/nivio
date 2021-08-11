package de.bonndan.nivio.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static de.bonndan.nivio.model.ItemFactory.getTestItem;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class GroupTest {

    @Test
    void doesNotAllowEmptyIdentifier() {
        assertThrows(Exception.class, () -> new Group("", "test"));
    }

    @Test
    void getItemsIsImmutable() {
        Group g = new Group("foo", "test");
        assertThrows(Exception.class, () -> g.getItems().add(getTestItem("a", "b")));
    }

    @Test
    void addItemAllowed() {
        Group g = new Group("foo", "test");
        Item item = getTestItem("foo", "b");
        g.addOrReplaceItem(item);
        assertEquals(1, g.getItems().size());
    }

    @Test
    void addItemForbidden() {
        Group g = new Group("foo", "test");
        Item item = getTestItem("a", "b");
        assertThrows(IllegalArgumentException.class, () -> g.addOrReplaceItem(item));
    }

    @Test
    void replacesItem() {
        Group g = new Group("foo", "test");
        Item one = getTestItem("foo", "one");
        g.addOrReplaceItem(one);
        assertThat(g.getItems()).containsExactly(one);

        Item copy = getTestItem("foo", "one");
        copy.setLabel(Label.version, "1");

        g.addOrReplaceItem(copy);
        assertThat(g.getItems()).containsExactly(copy);
        assertThat(g.getItems().iterator().next().getLabel(Label.version)).isEqualTo("1");
    }

    @Test
    void removeItem() {
        Group g = new Group("foo", "test");
        Item item = getTestItem("foo", "b");
        g.addOrReplaceItem(item);
        assertEquals(1, g.getItems().size());

        boolean b = g.removeItem(item);
        assertThat(b).isTrue();
        assertThat(g.getItems()).hasSize(0);
    }

    @Test
    void removeItemFails() {
        Group g = new Group("foo", "test");
        Item item = getTestItem("foo", "b");
        g.addOrReplaceItem(item);

        boolean b = g.removeItem(getTestItem("foo", "c"));
        assertThat(b).isFalse();
        assertThat(g.getItems()).hasSize(1);
    }

    @Test
    void hasNoChanges() {
        Group g1 = new Group("foo", "bar", "John", null, null, null, null);
        Group g2 = new Group("foo", "bar", "John", null, null, null, null);

        //when
        List<String> changes = g1.getChanges(g2);
        assertThat(changes).isEmpty();
    }

    @Test
    void hasChanges() {
        Group g1 = new Group("foo", "bar", "John", null, null, null, null);
        Group g2 = new Group("foo", "bar", "Doe", null, null, null, null);

        //when
        List<String> changes = g1.getChanges(g2);
        assertThat(changes).hasSize(1);
    }
}