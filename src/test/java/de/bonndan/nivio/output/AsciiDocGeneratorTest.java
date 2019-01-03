package de.bonndan.nivio.output;

import de.bonndan.nivio.landscape.Groups;
import de.bonndan.nivio.landscape.Landscape;
import de.bonndan.nivio.landscape.Service;
import de.bonndan.nivio.output.docs.AsciiDocGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertTrue;

public class AsciiDocGeneratorTest {

    private AsciiDocGenerator asciiDocGenerator;
    private Landscape landscape;

    @BeforeEach
    public void setup() {
        landscape = new Landscape();
        landscape.setIdentifier("doc:test");
        landscape.setName("DocTest");
        landscape.setContact("me@acme.org");

        asciiDocGenerator = new AsciiDocGenerator();
    }

    @Test
    public void testTitlePage() {
        String document = asciiDocGenerator.toDocument(landscape);

        assertTrue(document.contains("= DocTest"));
        assertTrue(document.contains("Identifier:: doc:test"));
        assertTrue(document.contains("Contact:: me@acme.org"));
    }

    @Test
    public void testGroupsDivisor() {

        Service service = new Service();
        service.setIdentifier("s");
        service.setName("example");
        service.setGroup("g1");
        landscape.addService(service);

        String document = asciiDocGenerator.toDocument(landscape);

        assertTrue(document.contains("== Group: g1"));
    }

    @Test
    public void testUnGrouped() {

        Service service = new Service();
        service.setIdentifier("s");
        service.setName("not in a group");
        landscape.addService(service);

        String document = asciiDocGenerator.toDocument(landscape);

        assertTrue(document.contains("== Group: " + Groups.COMMON));
    }
}
