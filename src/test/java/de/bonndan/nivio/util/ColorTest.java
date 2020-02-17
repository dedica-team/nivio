package de.bonndan.nivio.util;

import de.bonndan.nivio.output.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ColorTest {

    @Test
    public void testLongName() {
        String color = Color.nameToRGB("globalservices");
        assertEquals(6, color.length());
    }

    @Test
    public void testShort() {
        String color = Color.nameToRGB("xx");
        assertEquals(6, color.length());
    }

    @Test
    public void testRegression() {
        String color = Color.nameToRGB("restrictions");
        assertEquals("BB8E66", color);
    }
}
