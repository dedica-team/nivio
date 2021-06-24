package de.bonndan.nivio.input;

import de.bonndan.nivio.input.compose2.InputFormatHandlerCompose2;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.http.HttpService;
import de.bonndan.nivio.input.nivio.InputFormatHandlerNivio;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.util.RootPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class SourceReferencesResolverTest {

    @Mock
    ProcessLog log;

    private SourceReferencesResolver sourceReferencesResolver;
    private LandscapeDescriptionFactory factory;
    private FileFetcher fileFetcher;
    private ApplicationEventPublisher eventPublisher;

    @BeforeEach
    public void setup() {
        log = new ProcessLog(LoggerFactory.getLogger(SourceReferencesResolver.class), "test");
        fileFetcher = new FileFetcher(mock(HttpService.class));
        eventPublisher = mock(ApplicationEventPublisher.class);
        sourceReferencesResolver = new SourceReferencesResolver(
                new InputFormatHandlerFactory(
                        new ArrayList<>(Arrays.asList(
                                new InputFormatHandlerNivio(fileFetcher),
                                InputFormatHandlerCompose2.forTesting())
                        )
                )
                , log,
                eventPublisher
        );

        factory = new LandscapeDescriptionFactory(fileFetcher);
    }

    @Test
    public void resolve() {

        File file = new File(RootPath.get() + "/src/test/resources/example/example_incremental_env.yml");
        LandscapeDescription landscapeDescription = factory.fromYaml(file);
        assertFalse(landscapeDescription.getSourceReferences().isEmpty());

        sourceReferencesResolver.resolve(landscapeDescription);

        ItemDescription mapped = landscapeDescription.getItemDescriptions().pick("blog-server", null);
        assertNotNull(mapped);
        assertEquals("blog1", mapped.getLabel(Label.shortname));
        assertEquals("name2", mapped.getName());
    }

    @Test
    public void resolveOneReferenceIsNotAvailable() {

        //given
        File file = new File(RootPath.get() + "/src/test/resources/example/example_broken.yml");
        LandscapeDescription landscapeDescription = factory.fromYaml(file);
        assertFalse(landscapeDescription.getSourceReferences().isEmpty());
        assertFalse(landscapeDescription.isPartial());

        sourceReferencesResolver = new SourceReferencesResolver(
                new InputFormatHandlerFactory(
                        new ArrayList<>(Arrays.asList(
                                new InputFormatHandlerNivio(new FileFetcher(new HttpService())),
                                InputFormatHandlerCompose2.forTesting())
                        )
                )
                , log, eventPublisher);

        //when
        sourceReferencesResolver.resolve(landscapeDescription);

        //then
        var last = log.getMessages().get(log.getMessages().size() - 1);
        assertEquals("ERROR", last.level);
        assertFalse(StringUtils.isEmpty(last.message));
        assertTrue(landscapeDescription.isPartial());
    }

    @Test
    public void misconfiguredYaml() {

        //given
        File file = new File(RootPath.get() + "/src/test/resources/example/example_readingerror.yml");
        LandscapeDescription landscapeDescription = factory.fromYaml(file);
        assertFalse(landscapeDescription.getSourceReferences().isEmpty());
        assertFalse(landscapeDescription.isPartial());

        sourceReferencesResolver = new SourceReferencesResolver(
                new InputFormatHandlerFactory(
                        new ArrayList<>(Arrays.asList(
                                new InputFormatHandlerNivio(new FileFetcher(new HttpService())),
                                InputFormatHandlerCompose2.forTesting())
                        )
                )
                , log, eventPublisher);

        //when
        sourceReferencesResolver.resolve(landscapeDescription);

        //then
        var last = log.getMessages().get(log.getMessages().size() - 1);
        assertEquals("ERROR", last.level);
        assertThat(last.message).contains("mapping values are not allowed here");
    }

    @Test
    void firesErrorEvent() {
        //given
        File file = new File(RootPath.get() + "/src/test/resources/example/example_readingerror.yml");
        LandscapeDescription landscapeDescription = factory.fromYaml(file);
        assertFalse(landscapeDescription.getSourceReferences().isEmpty());
        assertFalse(landscapeDescription.isPartial());

        sourceReferencesResolver = new SourceReferencesResolver(
                new InputFormatHandlerFactory(
                        new ArrayList<>(Arrays.asList(
                                new InputFormatHandlerNivio(new FileFetcher(new HttpService())),
                                InputFormatHandlerCompose2.forTesting())
                        )
                )
                , log,
                eventPublisher);

        //when
        sourceReferencesResolver.resolve(landscapeDescription);

        //then
        ArgumentCaptor<ProcessingErrorEvent> captor = ArgumentCaptor.forClass(ProcessingErrorEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());
        ProcessingErrorEvent value = captor.getValue();
        assertThat(value).isNotNull();
        assertThat(value.getMessage()).contains("Failed to parse yaml service description");
    }
}
