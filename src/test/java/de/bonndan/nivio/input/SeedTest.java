package de.bonndan.nivio.input;

import de.bonndan.nivio.util.URLHelper;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SeedTest {
    @Test
    public void twoLocal() {
        Path currentRelativePath = Paths.get("");
        String root = currentRelativePath.toAbsolutePath().toString();
        Seed seed = new Seed(java.util.Optional.of(root + "/src/test/resources/example/example_env.yml," + root + "/src/test/resources/example/inout.yml"));
        List<String> locations = seed.getLocations();

        assertFalse(locations.isEmpty());
        assertEquals(2, locations.size());
        assertEquals(root + "/src/test/resources/example/example_env.yml", locations.get(0));
        assertEquals(root + "/src/test/resources/example/inout.yml", locations.get(1));
    }

    @Test
    public void http() {
        Seed seed = new Seed(java.util.Optional.of("/tmp,http://somehost.com/somefile.yml"));
        List<String> locations = seed.getLocations();

        assertFalse(locations.isEmpty());
        assertEquals(2, locations.size());
        assertEquals("http://somehost.com/somefile.yml", locations.get(1));
    }

    @Test
    public void fails() {
        Seed seed = new Seed(java.util.Optional.of(" :xxx"));
        assertTrue(URLHelper.getURL(seed.getLocations().get(0)).isEmpty());
    }
}
