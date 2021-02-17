package de.bonndan.nivio.input;

import de.bonndan.nivio.util.URLHelper;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SeedTest {

    @Test
    public void twoLocal() throws MalformedURLException {
        Path currentRelativePath = Paths.get("");
        String root = currentRelativePath.toAbsolutePath().toString();
        Seed seed = new Seed(java.util.Optional.of(root + "/src/test/resources/example/example_env.yml," + root + "/src/test/resources/example/inout.yml"));
        List<URL> locations = seed.getLocations();

        assertFalse(locations.isEmpty());
        assertEquals(2, locations.size());
        assertEquals(new File(root).toURI().toURL() + "src/test/resources/example/example_env.yml", locations.get(0).toString());
        assertEquals(new File(root).toURI().toURL() + "src/test/resources/example/inout.yml", locations.get(1).toString());
    }

    @Test
    public void http() {
        Path currentRelativePath = Paths.get("");
        String root = currentRelativePath.toAbsolutePath().toString();
        Seed seed = new Seed(java.util.Optional.of(root + "/src/test/resources/example/example_env.yml,http://somehost.com/somefile.yml"));
        List<URL> locations = seed.getLocations();

        assertFalse(locations.isEmpty());
        assertEquals(2, locations.size());
        assertEquals("http://somehost.com/somefile.yml", locations.get(1).toString());
    }

    @Test
    public void fails() {
        assertThrows(RuntimeException.class, () -> new Seed(java.util.Optional.of(" :xxx")));
    }
}
