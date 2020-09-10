package de.bonndan.nivio.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaggedTest {

    @Test
    public void setsNonEmpty() {
        Item item = new Item("test", "a");
        item.setTags(new String[]{null, "", "foo", "bar"});

        //when
        String[] tags = item.getTags();

        //then
        assertEquals(2, tags.length);
        List<String> tags1 = List.of(tags);
        assertTrue(tags1.contains("foo"));
        assertTrue(tags1.contains("bar"));
        assertFalse(tags1.contains(""));
    }

    @Test
    public void setsOnlyLowerCase() {
        Item item = new Item("test", "a");
        item.setTags(new String[]{"foo", "Foo"});

        //when
        String[] tags = item.getTags();

        //then
        assertEquals(1, tags.length);
        List<String> tags1 = List.of(tags);
        assertTrue(tags1.contains("foo"));
    }
}

