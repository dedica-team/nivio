package de.bonndan.nivio.input;

import de.bonndan.nivio.input.compose2.ServiceDescriptionFactoryCompose2;
import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.input.kubernetes.ServiceDescriptionFactoryKubernetes;
import de.bonndan.nivio.input.nivio.ServiceDescriptionFactoryNivio;

public class ServiceDescriptionFormatFactory {

    public static ServiceDescriptionFactory getFactory(SourceReference reference) {

        if (reference == null || reference.getFormat() == null)
            return new ServiceDescriptionFactoryNivio();

        switch (reference.getFormat()) {
            case DOCKER_COMPOSE2:
                return new ServiceDescriptionFactoryCompose2();
            case KUBERNETES:
                return new ServiceDescriptionFactoryKubernetes(reference);
        }

        return new ServiceDescriptionFactoryNivio();
    }
}
