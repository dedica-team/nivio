package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
}