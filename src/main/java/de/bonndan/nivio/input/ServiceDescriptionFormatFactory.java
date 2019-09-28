package de.bonndan.nivio.input;

import de.bonndan.nivio.input.compose2.ServiceDescriptionFactoryCompose2;
import de.bonndan.nivio.input.dto.Environment;
import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.input.http.HttpService;
import de.bonndan.nivio.input.kubernetes.ServiceDescriptionFactoryKubernetes;
import de.bonndan.nivio.input.nivio.ServiceDescriptionFactoryNivio;
import de.bonndan.nivio.util.URLHelper;

import java.net.URL;

public class ServiceDescriptionFormatFactory {

    /**
     * Returns the proper factory to generate/parse service descriptions based on the input format.
     *
     * @param reference the reference pointing at a file or url
     * @param environment landscape, may contain a base url
     * @return the factory
     */
    public static ServiceDescriptionFactory getFactory(SourceReference reference, Environment environment) {

        URL baseUrl = URLHelper.getParentPath(environment.getSource());

        FileFetcher fetcher = new FileFetcher(new HttpService());

        if (reference == null || reference.getFormat() == null)
            return new ServiceDescriptionFactoryNivio(fetcher, baseUrl);

        switch (reference.getFormat()) {
            case DOCKER_COMPOSE2:
                return new ServiceDescriptionFactoryCompose2(new FileFetcher(new HttpService()), baseUrl);
            case KUBERNETES:
                return new ServiceDescriptionFactoryKubernetes(reference);
        }

        return new ServiceDescriptionFactoryNivio(fetcher, baseUrl);
    }
}
