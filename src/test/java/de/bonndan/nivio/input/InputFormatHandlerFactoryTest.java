package de.bonndan.nivio.input;

import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.input.compose2.InputFormatHandlerCompose2;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.input.nivio.InputFormatHandlerNivio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class InputFormatHandlerFactoryTest {

    InputFormatHandlerFactory formatFactory;

    @BeforeEach
    public void setup() {
        List<InputFormatHandler> factories = new ArrayList<>();
        factories.add(new InputFormatHandlerCompose2(mock(FileFetcher.class)));
        factories.add(new InputFormatHandlerNivio(mock(FileFetcher.class)));

        formatFactory = new InputFormatHandlerFactory(factories);
    }

    @Test
    public void defaultIfNull() {
        InputFormatHandler factory = formatFactory.getInputFormatHandler(
                new SourceReference(),
                new LandscapeDescription()
        );

        assertTrue(factory instanceof InputFormatHandler);
        assertTrue(factory instanceof InputFormatHandlerNivio);
    }

    @Test
    public void defaultIfOther() {
        assertThrows(ProcessingException.class,() -> {
           formatFactory.getInputFormatHandler(new SourceReference(null, "abc"), new LandscapeDescription());
        });
    }

    @Test
    public void compose2() {

        InputFormatHandler factory = formatFactory.getInputFormatHandler(
                new SourceReference(null, "docker-compose-v2"),
                new LandscapeDescription()
        );

        assertTrue(factory instanceof InputFormatHandler);
        assertTrue(factory instanceof InputFormatHandlerCompose2);
    }
}
