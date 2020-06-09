package de.bonndan.nivio.input;

import de.bonndan.nivio.ProcessingErrorEvent;
import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.util.URLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;

@Component
public class LandscapeUrlsFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(LandscapeUrlsFactory.class);

    private final ApplicationEventPublisher publisher;

    private final FileFetcher fileFetcher;

    public LandscapeUrlsFactory(ApplicationEventPublisher publisher, FileFetcher fileFetcher) {
        this.publisher = publisher;
        this.fileFetcher = fileFetcher;
    }

    /**
     * Returns a list of URLs for a landscape description to watch for.
     *
     * @param seed the seed bean
     * @return a list of {@link URL}s per landscape.
     */
    public Map<Landscape, List<URL>> getLandscapeSourceLocations(Seed seed) {

        List<URL> landscapeDescriptionLocations = getUrls(seed);

        Map<Landscape, List<URL>> landscapeWatchLocations = new HashMap<>();

        landscapeDescriptionLocations.forEach(url -> {

            LandscapeDescription env = null;

            try {
                if (URLHelper.isLocal(url)) {
                    File file = Paths.get(url.toURI()).toFile();
                    env = LandscapeDescriptionFactory.fromYaml(file);
                } else {
                    env = LandscapeDescriptionFactory.fromString(fileFetcher.get(url), url);
                    Objects.requireNonNull(env);
                }

                landscapeWatchLocations.put(env, new ArrayList<>());
                landscapeWatchLocations.get(env).addAll(getLandscapeSourceLocations(env, new URL(env.getSource())));
                LOGGER.info("Created file map for landscape {}", env.getIdentifier());
            } catch (URISyntaxException | MalformedURLException e) {
                ProcessingException ex = new ProcessingException("Failed to initialize url from seed in landscape " + env, e);
                publisher.publishEvent(new ProcessingErrorEvent(this, ex));
            }
        });

        return landscapeWatchLocations;
    }

    public List<URL> getUrls(Seed seed) {
        List<URL> landscapeDescriptionLocations = new ArrayList<>();
        try {
            if (seed.hasValue()) {
                landscapeDescriptionLocations = seed.getLocations();
            }
            if (!StringUtils.isEmpty(System.getenv(Seed.DEMO))) {
                landscapeDescriptionLocations.addAll(seed.getDemoFiles());
            }
        } catch (MalformedURLException e) {
            ProcessingException processingException = new ProcessingException("Failed to initialize watchers from seed", e);
            publisher.publishEvent(new ProcessingErrorEvent(this, processingException));
        }
        return landscapeDescriptionLocations;
    }

    /**
     * Returns all URLs of a landscape description.
     *
     * @param env description
     * @param url config file url
     * @return urls: config file and source references.
     */
    public List<URL> getLandscapeSourceLocations(@NonNull LandscapeDescription env, @Nullable URL url) {
        List<URL> urls =new ArrayList<>();
        if (url != null) {
            urls.add(url);
        }

        for (SourceReference sourceReference : env.getSourceReferences()) {
            try {
                urls.add(new URL(sourceReference.getUrl()));
            } catch (MalformedURLException e) {
                LOGGER.error("Failed to handle url {}", sourceReference.getUrl(), e);
            }
        }

        return urls;
    }
}
