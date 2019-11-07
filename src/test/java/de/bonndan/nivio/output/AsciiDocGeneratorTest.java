package de.bonndan.nivio.output;

import de.bonndan.nivio.model.Groups;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.LandscapeImpl;
import de.bonndan.nivio.output.docs.AsciiDocGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertTrue;

public class AsciiDocGeneratorTest {

    private AsciiDocGenerator asciiDocGenerator;
    private LandscapeImpl landscape;

    @BeforeEach
    public void setup() {
        landscape = new LandscapeImpl();
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

        Item item = new Item();
        item.setIdentifier("s");
        item.setName("example");
        item.setGroup("g1");
        landscape.addItem(item);

        String document = asciiDocGenerator.toDocument(landscape);

        assertTrue(document.contains("== Group: g1"));
    }

    @Test
    public void testUnGrouped() {

        Item item = new Item();
        item.setIdentifier("s");
        item.setName("not in a group");
        landscape.addItem(item);

        String document = asciiDocGenerator.toDocument(landscape);

        assertTrue(document.contains("== Group: " + Groups.COMMON));
    }
}
