package de.bonndan.nivio.input;

import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.input.compose2.ItemDescriptionFactoryCompose2;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.input.nivio.ItemDescriptionFactoryNivio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class ItemDescriptionFormatFactoryTest {

    ItemDescriptionFormatFactory formatFactory;

    @BeforeEach
    public void setup() {
        List<ItemDescriptionFactory> factories = new ArrayList<>();
        factories.add(new ItemDescriptionFactoryCompose2(mock(FileFetcher.class)));
        factories.add(new ItemDescriptionFactoryNivio(mock(FileFetcher.class)));

        formatFactory = new ItemDescriptionFormatFactory(factories);
    }

    @Test
    public void defaultIfNull() {
        ItemDescriptionFactory factory = formatFactory.getFactory(
                new SourceReference(),
                new LandscapeDescription()
        );

        assertTrue(factory instanceof ItemDescriptionFactory);
        assertTrue(factory instanceof ItemDescriptionFactoryNivio);
    }

    @Test
    public void defaultIfOther() {
        assertThrows(ProcessingException.class,() -> {
           formatFactory.getFactory(new SourceReference(null, "abc"), new LandscapeDescription());
        });
    }

    @Test
    public void compose2() {

        ItemDescriptionFactory factory = formatFactory.getFactory(
                new SourceReference(null, "docker-compose-v2"),
                new LandscapeDescription()
        );

        assertTrue(factory instanceof ItemDescriptionFactory);
        assertTrue(factory instanceof ItemDescriptionFactoryCompose2);
    }
}
