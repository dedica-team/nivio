package de.bonndan.nivio.input;

import de.bonndan.nivio.input.compose2.ServiceDescriptionFactoryCompose2;
import de.bonndan.nivio.input.dto.SourceFormat;
import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.input.nivio.ServiceDescriptionFactoryNivio;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServiceDescriptionFormatFactoryTest {

    @Test
    public void defaultIfNull() {
        ServiceDescriptionFactory factory = ServiceDescriptionFormatFactory.getFactory(
                new SourceReference()
        );

        assertTrue(factory instanceof ServiceDescriptionFactory);
        assertTrue(factory instanceof ServiceDescriptionFactoryNivio);
    }

    @Test
    public void defaultIfOther() {
        ServiceDescriptionFactory factory = ServiceDescriptionFormatFactory.getFactory(
                new SourceReference(SourceFormat.from("abc"))
        );

        assertTrue(factory instanceof ServiceDescriptionFactory);
        assertTrue(factory instanceof ServiceDescriptionFactoryNivio);
    }

    @Test
    public void compose2() {

        ServiceDescriptionFactory factory = ServiceDescriptionFormatFactory.getFactory(
                new SourceReference(SourceFormat.from("docker-compose-v2"))
        );

        assertTrue(factory instanceof ServiceDescriptionFactory);
        assertTrue(factory instanceof ServiceDescriptionFactoryCompose2);
    }
}
