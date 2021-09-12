package de.bonndan.nivio.input.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemDescriptionTest {

    @Test
    void validatesConstructorIdentifier() {
        assertThrows(IllegalArgumentException.class, () -> new ItemDescription(""));
    }

    @Test
    void setIdentifier() {
        assertThrows(IllegalArgumentException.class, () -> new ItemDescription().setIdentifier(""));
    }
}