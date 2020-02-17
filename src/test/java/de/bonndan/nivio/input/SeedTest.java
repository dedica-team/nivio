package de.bonndan.nivio.input;

import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SeedTest {

    @Test
    public void empty() throws MalformedURLException {
        Seed seed = new Seed("");
        List<URL> locations = seed.getLocations();
        assertFalse(locations.isEmpty());
        assertEquals(Seed.NIVIO_ENV_DIRECTORY,locations.get(0).toString());
    }

    @Test
    public void twoLocal() throws MalformedURLException {
        Seed seed = new Seed("/tmp,file:///tmp2");
        List<URL> locations = seed.getLocations();

        assertFalse(locations.isEmpty());
        assertEquals(2, locations.size());
        assertEquals("file:/tmp",locations.get(0).toString());
        assertEquals("file:/tmp2",locations.get(1).toString());
    }

    @Test
    public void http() throws MalformedURLException {
        Seed seed = new Seed("/tmp,http://somehost.com/somefile.yml");
        List<URL> locations = seed.getLocations();

        assertFalse(locations.isEmpty());
        assertEquals(2, locations.size());
        assertEquals("http://somehost.com/somefile.yml",locations.get(1).toString());
    }

    @Test
    public void fails() throws MalformedURLException {
        Seed seed = new Seed(" :xxx");
        assertThrows(MalformedURLException.class,() -> seed.getLocations());
    }

    @Test
    public void windowsFileLocation() throws MalformedURLException {
        Seed seed = new Seed("c:\\a\\b\\c.yml");
        Seed.ESCAPE_AUTHORITY = true;
        List<URL> locations = seed.getLocations();
        assertEquals("file://c\\a\\b\\c.yml", locations.get(0).toString());
        Seed.ESCAPE_AUTHORITY = false;
    }
}
