package de.bonndan.nivio.input;

import de.bonndan.nivio.input.compose2.ItemDescriptionFactoryCompose2;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.SourceFormat;
import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.input.nivio.ItemDescriptionFactoryNivio;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ItemDescriptionFormatFactoryTest {

    @Test
    public void defaultIfNull() {
        ItemDescriptionFactory factory = ItemDescriptionFormatFactory.getFactory(
                new SourceReference(),
                new LandscapeDescription()
        );

        assertTrue(factory instanceof ItemDescriptionFactory);
        assertTrue(factory instanceof ItemDescriptionFactoryNivio);
    }

    @Test
    public void defaultIfOther() {
        ItemDescriptionFactory factory = ItemDescriptionFormatFactory.getFactory(
                new SourceReference(SourceFormat.from("abc")),
                new LandscapeDescription()
        );

        assertTrue(factory instanceof ItemDescriptionFactory);
        assertTrue(factory instanceof ItemDescriptionFactoryNivio);
    }

    @Test
    public void compose2() {

        ItemDescriptionFactory factory = ItemDescriptionFormatFactory.getFactory(
                new SourceReference(SourceFormat.from("docker-compose-v2")),
                new LandscapeDescription()
        );

        assertTrue(factory instanceof ItemDescriptionFactory);
        assertTrue(factory instanceof ItemDescriptionFactoryCompose2);
    }
}
