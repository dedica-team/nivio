package de.bonndan.nivio.input;

import de.bonndan.nivio.input.compose2.ServiceDescriptionFactoryCompose2;
import de.bonndan.nivio.input.dto.SourceFormat;
import de.bonndan.nivio.input.kubernetes.ServiceDescriptionFactoryKubernetes;
import de.bonndan.nivio.input.nivio.ServiceDescriptionFactoryNivio;

public class ServiceDescriptionFormatFactory {

    public static ServiceDescriptionFactory getFactory(SourceFormat format) {

        if (format == null)
            return new ServiceDescriptionFactoryNivio();

        switch (format) {
            case DOCKER_COMPOSE2:
                return new ServiceDescriptionFactoryCompose2();
            case KUBERNETES:
                return new ServiceDescriptionFactoryKubernetes();
        }

        return new ServiceDescriptionFactoryNivio();
    }
}
