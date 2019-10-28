package de.bonndan.nivio.input;

import de.bonndan.nivio.input.compose2.ItemDescriptionFactoryCompose2;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.input.http.HttpService;
import de.bonndan.nivio.input.kubernetes.ItemDescriptionFactoryKubernetes;
import de.bonndan.nivio.input.nivio.ItemDescriptionFactoryNivio;
import de.bonndan.nivio.input.rancher1.ItemDescriptionFactoryRancher1;
import de.bonndan.nivio.util.URLHelper;

import java.net.URL;

public class ItemDescriptionFormatFactory {

    /**
     * Returns the proper factory to generate/parse service descriptions based on the input format.
     *
     * @param reference the reference pointing at a file or url
     * @param landscapeDescription landscape, may contain a base url
     * @return the factory
     */
    public static ItemDescriptionFactory getFactory(SourceReference reference, LandscapeDescription landscapeDescription) {

        URL baseUrl = URLHelper.getParentPath(landscapeDescription.getSource());

        FileFetcher fetcher = new FileFetcher(new HttpService());

        if (reference == null || reference.getFormat() == null)
            return new ItemDescriptionFactoryNivio(fetcher, baseUrl);

        //TODO use SourceFormat.from or similar
        switch (reference.getFormat()) {
            case DOCKER_COMPOSE2:
                return new ItemDescriptionFactoryCompose2(new FileFetcher(new HttpService()), baseUrl);
            case KUBERNETES:
                return new ItemDescriptionFactoryKubernetes(reference);
            case RANCHER1_PROMETHEUS:
                return new ItemDescriptionFactoryRancher1(baseUrl);
        }

        return new ItemDescriptionFactoryNivio(fetcher, baseUrl);
    }
}
