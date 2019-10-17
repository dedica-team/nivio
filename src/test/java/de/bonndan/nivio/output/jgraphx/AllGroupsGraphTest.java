package de.bonndan.nivio.output.jgraphx;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AllGroupsGraphTest {

    @Test
    public void testGroupConnections() {
        AllGroupsGraph.GroupConnections groupConnections = new AllGroupsGraph.GroupConnections();
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