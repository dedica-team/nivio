package de.bonndan.nivio.input.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GroupDescriptionTest {

    @Test
    void validatesIdentifier() {
        assertThrows(IllegalArgumentException.class, () -> new GroupDescription().setName("1/1$"));
        assertThrows(IllegalArgumentException.class, () -> new GroupDescription().setIdentifier("1/1$"));
    }

    @Test
    public void testNotEquals() {
        GroupDescription groupDescription = new GroupDescription();
        groupDescription.setIdentifier("11");

        GroupDescription groupDescription2 = new GroupDescription();
        groupDescription2.setIdentifier("12");

        assertNotEquals(groupDescription, groupDescription2);
    }


    @Test
    public void testEquals() {
        GroupDescription groupDescription = new GroupDescription();
        groupDescription.setIdentifier("11");

        GroupDescription groupDescription2 = new GroupDescription();
        groupDescription2.setIdentifier("11");

        assertEquals(groupDescription, groupDescription2);
    }

}