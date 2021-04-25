package de.bonndan.nivio.output;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ColorTest {

    @Test
    public void testLongName() {
        String color = Color.nameToRGB("globalservices", Color.GRAY);
        assertThat(color).isEqualTo("43FDC1");
    }

    @Test
    public void testShort() {
        String color = Color.nameToRGB("xx", Color.GRAY);
        assertThat(color).isEqualTo("F00000");
    }

    @Test
    public void testRegression() {
        String color = Color.nameToRGB("restrictions", Color.GRAY);
        assertEquals("BB8E66", color);
    }

    @Test
    public void testTooDark() {

        //given
        String name = "0";
        float[] hsv = Color.hsb(Color.nameToRGBRaw(name));
        assertThat(hsv[2]).isLessThan(Color.MIN_BRIGHTNESS);
        assertThat(hsv[1]).isGreaterThan(Color.MIN_SATURATION);

        //when
        String color = Color.nameToRGB(name, Color.GRAY);

        //then
        assertThat(color).isNotNull();
        assertThat(color).isEqualTo("ff4400");
        hsv = Color.hsb(color);
        assertThat(hsv[2]).isGreaterThan(Color.MIN_BRIGHTNESS);
    }

    @Test
    public void testTooLessSaturation() {

        //given
        String name = "fffffff35t6245z6";
        float[] hsv = Color.hsb(Color.nameToRGBRaw(name));
        assertThat(hsv[2]).isGreaterThan(Color.MIN_BRIGHTNESS);
        assertThat(hsv[1]).isLessThan(Color.MIN_SATURATION);

        //when
        String color = Color.nameToRGB(name, Color.GRAY);

        //then
        assertThat(color).isNotNull();
        assertThat(color).isEqualTo("ffa3d2");
        hsv = Color.hsb(color);
        assertThat(hsv[1]).isGreaterThan(Color.MIN_SATURATION);
    }

    @Test
    void safe() {
        String safe;
        safe = Color.safe("000000");
        assertThat(safe).isEqualTo("000000");

        safe = Color.safe("000");
        assertThat(safe).isEqualTo("000000");

        safe = Color.safe("#23465f");
        assertThat(safe).isEqualTo("23465f");

    }

    @Test
    void safeWithBadChars() {
        String safe;

        safe = Color.safe("234#65f!");
        assertThat(safe).isEqualTo("23465f");
    }
}
