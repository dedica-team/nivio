package de.bonndan.nivio;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.bonndan.nivio.config.ApplicationConfig;
import de.bonndan.nivio.input.*;
import de.bonndan.nivio.input.compose2.InputFormatHandlerCompose2;
import de.bonndan.nivio.input.csv.InputFormatHandlerCSV;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.external.LinkHandlerFactory;
import de.bonndan.nivio.input.http.HttpService;
import de.bonndan.nivio.input.nivio.InputFormatHandlerNivio;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.model.LandscapeRepository;
import de.bonndan.nivio.util.RootPath;
import org.springframework.context.ApplicationEventPublisher;

import java.io.File;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.Mockito.mock;

/**
 * This helper class provides a properly configured chain of tools for input processing.
 */
public class IntegrationTestSupport {

    private final HttpService httpService;
    private final SourceReferencesResolver sourceReferenceResolver;
    private final SeedConfigurationFactory seedConfigurationFactory;
    private final ApplicationEventPublisher eventPublisher;
    private final LandscapeRepository landscapeRepository;
    private final LinkHandlerFactory linkHandlerFactory;
    private final Indexer indexer;
    private final ObjectMapper objectMapper;
    private final FileFetcher fileFetcher;

    public IntegrationTestSupport() {
        httpService = mock(HttpService.class);
        fileFetcher = new FileFetcher(httpService);
        seedConfigurationFactory = new SeedConfigurationFactory(fileFetcher);
        eventPublisher = mock(ApplicationEventPublisher.class);
        InputFormatHandlerFactory formatFactory = new InputFormatHandlerFactory(
                List.of(
                        new InputFormatHandlerNivio(fileFetcher),
                        new InputFormatHandlerCompose2(fileFetcher),
                        new InputFormatHandlerCSV(fileFetcher)
                )
        );
        sourceReferenceResolver = new SourceReferencesResolver(formatFactory, eventPublisher);
        landscapeRepository = new LandscapeRepository();
        linkHandlerFactory = mock(LinkHandlerFactory.class);
        indexer = new Indexer(landscapeRepository, linkHandlerFactory, eventPublisher);
        objectMapper = new ApplicationConfig(null).jackson2ObjectMapperBuilder().build();
    }

    /**
     * @return mock
     */
    public HttpService getHttpService() {
        return httpService;
    }

    public SeedConfigurationFactory getSeedConfigurationFactory() {
        return seedConfigurationFactory;
    }

    /**
     * @return real resolver with format factories for nivio and docker-compose files
     */
    public SourceReferencesResolver getSourceReferenceResolver() {
        return sourceReferenceResolver;
    }

    /**
     * @return mock
     */
    public ApplicationEventPublisher getEventPublisher() {
        return eventPublisher;
    }

    public LandscapeRepository getLandscapeRepository() {
        return landscapeRepository;
    }

    /**
     * @return mock
     */
    public LinkHandlerFactory getLinkHandlerFactory() {
        return linkHandlerFactory;
    }

    public Indexer getIndexer() {
        return indexer;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * @return real file fetcher with mocked http service
     */
    public FileFetcher getFileFetcher() {
        return fileFetcher;
    }

    /**
     * Returns the first landscape description resulting from the given seed config file.
     *
     * @param file seed config
     */
    public LandscapeDescription getFirstLandscapeDescription(File file) {
        SeedConfiguration configuration = seedConfigurationFactory.fromFile(file);
        List<LandscapeDescription> resolve = sourceReferenceResolver.resolve(configuration);
        return resolve.stream().findFirst().orElseThrow();
    }

    /**
     * Returns the first landscape description resulting from the given seed config file.
     *
     * @param file seed config
     */
    public Landscape getFirstIndexedLandscape(File file) {
        SeedConfiguration configuration = seedConfigurationFactory.fromFile(file);

        List<LandscapeDescription> resolve = sourceReferenceResolver.resolve(configuration);
        if (resolve.size() == 0) {
            throw new NoSuchElementException("No landscape description resolved from " + file);
        }

        LandscapeDescription input = resolve.get(0);
        indexer.index(input);
        return landscapeRepository.findDistinctByIdentifier(input.getIdentifier()).orElseThrow();
    }


}
