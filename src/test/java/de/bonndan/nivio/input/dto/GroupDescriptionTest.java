package de.bonndan.nivio.input.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GroupDescriptionTest {

    @Test
    public void testNotEquals() {
        GroupDescription groupDescription = new GroupDescription();
        groupDescription.setIdentifier("1");

        GroupDescription groupDescription2 = new GroupDescription();
        groupDescription2.setIdentifier("2");

        assertNotEquals(groupDescription, groupDescription2);
    }


    @Test
    public void testEquals() {
        GroupDescription groupDescription = new GroupDescription();
        groupDescription.setIdentifier("1");

        GroupDescription groupDescription2 = new GroupDescription();
        groupDescription2.setIdentifier("1");

        assertEquals(groupDescription, groupDescription2);
    }

}