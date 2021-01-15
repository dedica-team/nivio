package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.model.Link;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class LabelToFieldResolverTest {

    private LabelToFieldResolver processor;

    @BeforeEach
    public void setup() {
        Logger logger = LoggerFactory.getLogger(LabelToFieldResolverTest.class);
        ProcessLog processLog = new ProcessLog(logger);
        processor = new LabelToFieldResolver(processLog);
    }

    @Test
    @DisplayName("Ensure comma separated strings are parsed properly")
    void process() {
        ItemDescription item1 = new ItemDescription();
        item1.getLabels().put("a", "b");
        item1.getLabels().put("nivio.name", "foo");
        item1.getLabels().put("NiVIO.description", "bar");
        item1.getLabels().put("NIVIO.providedBy", "baz, bak");

        LandscapeDescription input = new LandscapeDescription();
        input.getItemDescriptions().add(item1);

        //when
        processor.resolve(input);

        //then
        assertEquals("foo", item1.getName());
        assertEquals("bar", item1.getDescription());
        assertEquals(2, item1.getProvidedBy().size());
    }


    @Test
    @DisplayName("Prefixed labels are removed after processing")
    void cleanup() {
        ItemDescription item1 = new ItemDescription();
        item1.getLabels().put("a", "b");
        item1.getLabels().put("nivio.name", "foo");
        item1.getLabels().put("nivio.description", "bar");
        item1.getLabels().put("nivio.providedBy", "baz, bak");

        LandscapeDescription input = new LandscapeDescription();
        input.getItemDescriptions().add(item1);

        assertEquals(4, item1.getLabels().size());

        //when
        processor.resolve(input);

        //then
        assertEquals(1, item1.getLabels().size()); //"a" remains
    }

    @Test
    @DisplayName("Ensure comma separated strings are parsed properly")
    public void listFieldLabelWithoutDelimiter() {
        ItemDescription item1 = new ItemDescription();
        item1.getLabels().put("a", "b");
        item1.getLabels().put("nivio.name", "foo");
        item1.getLabels().put("NiVIO.description", "bar");
        item1.getLabels().put("NIVIO.providedBy", "baz ");

        LandscapeDescription input = new LandscapeDescription();
        input.getItemDescriptions().add(item1);

        //when
        processor.resolve(input);

        //then
        assertEquals("foo", item1.getName());
        assertEquals("bar", item1.getDescription());
        assertEquals(1, item1.getProvidedBy().size());
    }

    @Test
    @DisplayName("Ensure comma separated links are parsed properly")
    public void deprecatedLinks() {
        ItemDescription item1 = new ItemDescription();
        item1.getLabels().put("a", "b");
        item1.getLabels().put("nivio.links", "http://one.com, https://two.net");

        LandscapeDescription input = new LandscapeDescription();
        input.getItemDescriptions().add(item1);

        //when
        processor.resolve(input);

        //then
        Map<String, Link> links = item1.getLinks();
        assertFalse(links.isEmpty());
        Link link = links.get("1");
        assertNotNull(link);
        assertNotNull(link.getHref());
        assertEquals("http://one.com", link.getHref().toString());

        link = links.get("2");
        assertNotNull(link);
        assertNotNull(link.getHref());
        assertEquals("https://two.net", link.getHref().toString());
    }

    @Test
    @DisplayName("Ensure links with keys are parsed")
    public void links() {
        ItemDescription item1 = new ItemDescription();
        item1.getLabels().put("a", "b");
        item1.getLabels().put("nivio.link.wiki", "http://one.com");
        item1.getLabels().put("nivio.link.repo", "https://two.net");

        LandscapeDescription input = new LandscapeDescription();
        input.getItemDescriptions().add(item1);

        //when
        processor.resolve(input);

        //then
        Map<String, Link> links = item1.getLinks();
        assertFalse(links.isEmpty());
        Link url = links.get("wiki");
        assertNotNull(url);
        assertNotNull(url.getHref());
        assertEquals("http://one.com", url.getHref().toString());

        url = links.get("repo");
        assertNotNull(url);
        assertNotNull(url.getHref());
        assertEquals("https://two.net", url.getHref().toString());
    }

    @Test
    @DisplayName("Ensure comma separated links are parsed properly")
    public void labelsToLabels() {
        ItemDescription item1 = new ItemDescription();
        item1.getLabels().put("a", "b");
        item1.getLabels().put("nivio.visibility", "public");
        item1.getLabels().put("nivio.software", "wordpress");
        item1.getLabels().put("nivio.other", "foo");

        LandscapeDescription input = new LandscapeDescription();
        input.getItemDescriptions().add(item1);

        //when
        processor.resolve(input);

        //then
        String vis = item1.getLabel(Label.visibility);
        assertNotNull(vis);
        assertEquals("public", vis);

        String software = item1.getLabel(Label.software);
        assertNotNull(software);
        assertEquals("wordpress", software);

        String other = item1.getLabel("other");
        assertNotNull(other);
        assertEquals("foo", other);
    }
}