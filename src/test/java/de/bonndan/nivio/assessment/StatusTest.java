package de.bonndan.nivio.assessment;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatusTest {

    @Test
    void comparator() {
        Status green = Status.GREEN;
        Status red = Status.RED;
        assertEquals(-1, new Status.Comparator().compare(green, red));
        assertEquals(0, new Status.Comparator().compare(green, green));
        assertEquals(1, new Status.Comparator().compare(red, green));
    }
}