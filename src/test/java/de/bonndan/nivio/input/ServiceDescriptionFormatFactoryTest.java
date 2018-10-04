package de.bonndan.nivio.input;

import de.bonndan.nivio.input.compose2.ServiceDescriptionFactoryCompose2;
import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.input.nivio.ServiceDescriptionFactoryNivio;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServiceDescriptionFormatFactoryTest {

    @Test
    public void defaultIfNull() {
        SourceReference sourceReference = new SourceReference();
        ServiceDescriptionFactory factory = ServiceDescriptionFormatFactory.getFactory(sourceReference);

        assertTrue(factory instanceof ServiceDescriptionFactory);
        assertTrue(factory instanceof ServiceDescriptionFactoryNivio);
    }


    public void defaultIfOther() {
        SourceReference sourceReference = new SourceReference();
        sourceReference.setFormat("abc");
        ServiceDescriptionFactory factory = ServiceDescriptionFormatFactory.getFactory(sourceReference);

        assertTrue(factory instanceof ServiceDescriptionFactory);
        assertTrue(factory instanceof ServiceDescriptionFactoryNivio);
    }

    @Test
    public void compose2() {
        SourceReference sourceReference = new SourceReference();
        sourceReference.setFormat("docker-compose-v2");
        ServiceDescriptionFactory factory = ServiceDescriptionFormatFactory.getFactory(sourceReference);

        assertTrue(factory instanceof ServiceDescriptionFactory);
        assertTrue(factory instanceof ServiceDescriptionFactoryCompose2);
    }
}
