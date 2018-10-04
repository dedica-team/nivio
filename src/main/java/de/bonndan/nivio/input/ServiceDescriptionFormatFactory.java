package de.bonndan.nivio.input;

import de.bonndan.nivio.input.compose2.ServiceDescriptionFactoryCompose2;
import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.input.nivio.ServiceDescriptionFactoryNivio;

public class ServiceDescriptionFormatFactory {

    public static final String FORMAT_DOCKER_COMPOSE2 = "docker-compose-v2";

    public static ServiceDescriptionFactory getFactory(SourceReference sourceReference) {

        if (sourceReference.getFormat() == null)
            return new ServiceDescriptionFactoryNivio();

        switch (sourceReference.getFormat()) {
            case FORMAT_DOCKER_COMPOSE2:
                return new ServiceDescriptionFactoryCompose2();

        }

        return new ServiceDescriptionFactoryNivio();
    }
}
