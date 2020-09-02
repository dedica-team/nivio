package de.bonndan.nivio.output.layout;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GroupConnectionsTest {

    @Test
    public void testGroupConnections() {
        GroupConnections groupConnections = new GroupConnections();
        assertFalse(groupConnections.isConnected("a"));

        assertTrue((groupConnections.canConnect("a", "b")));
        assertFalse((groupConnections.canConnect("a", "a")));
        assertFalse((groupConnections.canConnect("a", "")));
        assertFalse((groupConnections.canConnect("", "b")));

        groupConnections.connect("a", "b", "");
        assertTrue(groupConnections.isConnected("a"));
        assertTrue(groupConnections.isConnected("b"));
    }
}