package de.bonndan.nivio.model;

import de.bonndan.nivio.input.dto.LandscapeDescription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

class LandscapeFactoryTest {

    private LandscapeDescription description;

    @BeforeEach
    public void setup() throws MalformedURLException {
        description = new LandscapeDescription();
        description.setIdentifier("foo");
        description.setSource("/my/file.yaml");
        description.setName("bar");
        description.setOwner("baz");
        description.setContact("baz@mail.com");
        description.setDescription("Hello, World.");
        description.setLink("home", new URL("https://dedica.team"));
        description.getLabels().put("one", "two");
    }

    @Test
    public void create() {
        LandscapeImpl landscape = LandscapeFactory.toLandscape(description);
        assertNotNull(landscape);
        assertEquals(description.getIdentifier(), landscape.getIdentifier());
        assertEquals(description.getSource(), landscape.getSource());
    }

    @Test
    public void assignAll() {
        LandscapeImpl landscape = LandscapeFactory.toLandscape(description);
        LandscapeFactory.assignAll(description, landscape);
        assertEquals(description.getContact(), landscape.getContact());
        assertEquals(description.getConfig(), landscape.getConfig());
        assertEquals(description.getOwner(), landscape.getOwner());
        assertEquals(description.getDescription(), landscape.getDescription());
        assertEquals(description.getName(), landscape.getName());
        assertEquals(1,  landscape.getLabels().size());
        assertEquals("two",  landscape.getLabels().get("one"));
        assertEquals(1,  landscape.getLinks().size());
        assertEquals("https://dedica.team",  landscape.getLinks().get("home").toString());
    }
}