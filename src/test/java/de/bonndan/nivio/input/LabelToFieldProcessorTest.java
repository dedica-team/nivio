package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class LabelToFieldProcessorTest {

    private LabelToFieldProcessor processor;

    @BeforeEach
    public void setup() {
        Logger logger = LoggerFactory.getLogger(LabelToFieldProcessorTest.class);
        ProcessLog processLog = new ProcessLog(logger);
        processor = new LabelToFieldProcessor(processLog);
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
        processor.process(input, null);

        //then
        assertEquals("foo", item1.getName());
        assertEquals("bar", item1.getDescription());
        assertEquals(2, item1.getProvidedBy().size());
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
        processor.process(input, null);

        //then
        assertEquals("foo", item1.getName());
        assertEquals("bar", item1.getDescription());
        assertEquals(1, item1.getProvidedBy().size());
    }

    @Test
    @DisplayName("Ensure comma separated links are parsed properly")
    public void links() {
        ItemDescription item1 = new ItemDescription();
        item1.getLabels().put("a", "b");
        item1.getLabels().put("nivio.links", "http://one.com, https://two.net");

        LandscapeDescription input = new LandscapeDescription();
        input.getItemDescriptions().add(item1);

        //when
        processor.process(input, null);

        //then
        Map<String, URL> links = item1.getLinks();
        assertFalse(links.isEmpty());
        URL url = links.get("1");
        assertNotNull(url);
        assertEquals("http://one.com", url.toString());

        url = links.get("2");
        assertNotNull(url);
        assertEquals("https://two.net", url.toString());
    }
}