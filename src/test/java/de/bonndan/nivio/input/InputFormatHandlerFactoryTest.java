package de.bonndan.nivio.input;

import de.bonndan.nivio.input.compose2.InputFormatHandlerCompose2;
import de.bonndan.nivio.input.nivio.InputFormatHandlerNivio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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
    void defaultIfNull() throws MalformedURLException {
        InputFormatHandler factory = formatFactory.getInputFormatHandler(
                new SourceReference(new URL("https://test.com"))
        );

        assertTrue(factory instanceof InputFormatHandler);
        assertTrue(factory instanceof InputFormatHandlerNivio);
    }

    @Test
    void defaultIfOther() {
        assertThrows(RuntimeException.class, () -> {
            formatFactory.getInputFormatHandler(new SourceReference(null, "abc"));
        });
    }

    @Test
    void compose2() {

        InputFormatHandler factory = formatFactory.getInputFormatHandler(
                new SourceReference(null, "docker-compose-v2")
        );

        assertTrue(factory instanceof InputFormatHandler);
        assertTrue(factory instanceof InputFormatHandlerCompose2);
    }

    @Test
    void ignoresCase() throws MalformedURLException {

        //given
        SourceReference ref = new SourceReference(new URL("https://test.com"));
        ref.setFormat("Docker-COMPOSE-v2");

        //when
        InputFormatHandler factory = formatFactory.getInputFormatHandler(ref);

        //then
        assertThat(factory).isNotNull();
    }
}
