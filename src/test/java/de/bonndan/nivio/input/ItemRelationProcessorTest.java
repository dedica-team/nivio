package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.LandscapeBuilder;
import de.bonndan.nivio.model.LandscapeFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ItemRelationProcessorTest {

    private LandscapeDescription input;
    private LandscapeBuilder landscape;
    private ItemRelationProcessor processor;

    @BeforeEach
    void setup() {
        input = new LandscapeDescription("test");
        landscape = LandscapeFactory.createForTesting("test", "test");

        processor = new ItemRelationProcessor(mock(ProcessLog.class));
    }

    @Test
    void clearsAllRelations() {
        assertTrue(false);
    }

    @Test
    void addsNewRelations() {
        assertTrue(false);
    }

    @Test
    void updatesExistingRelations() {
        assertTrue(false);
    }
}