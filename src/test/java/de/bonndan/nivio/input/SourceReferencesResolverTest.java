package de.bonndan.nivio.input;

import de.bonndan.nivio.IntegrationTestSupport;
import de.bonndan.nivio.input.dto.BranchDescription;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.ProcessDescription;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.util.RootPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;

import java.io.File;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

class SourceReferencesResolverTest {

    @Mock
    ProcessLog log;

    private SourceReferencesResolver sourceReferencesResolver;
    private SeedConfigurationFactory factory;
    private ApplicationEventPublisher eventPublisher;

    @BeforeEach
    public void setup() {
        log = new ProcessLog(LoggerFactory.getLogger(SourceReferencesResolver.class), "test");

        IntegrationTestSupport integrationTestSupport = new IntegrationTestSupport();
        eventPublisher = integrationTestSupport.getEventPublisher();
        sourceReferencesResolver = integrationTestSupport.getSourceReferenceResolver();

        factory = new SeedConfigurationFactory(integrationTestSupport.getFileFetcher());
    }

    @Test
    void resolve() {

        File file = new File(RootPath.get() + "/src/test/resources/example/example_incremental_env.yml");
        SeedConfiguration configuration = factory.fromFile(file);
        assertFalse(configuration.getSourceReferences().isEmpty());

        List<LandscapeDescription> resolve = sourceReferencesResolver.resolve(configuration);

        assertThat(resolve).isNotEmpty();

        Set<ItemDescription> itemDescriptions = resolve.get(0).getItemDescriptions();
        assertThat(itemDescriptions).isNotEmpty();
        ItemDescription mapped = itemDescriptions.stream()
                .filter(itemDescription -> "blog-server".equals(itemDescription.getIdentifier()))
                .findFirst()
                .orElseThrow();

        assertNotNull(mapped);
        assertEquals("blog1", mapped.getLabel(Label.shortname));
        assertEquals("name2", mapped.getName());
    }

    @Test
    void containsProcesses() {

        File file = new File(RootPath.get() + "/src/test/resources/example/internals.yml");
        SeedConfiguration configuration = factory.fromFile(file);

        //when
        List<LandscapeDescription> resolve = sourceReferencesResolver.resolve(configuration);

        //then
        assertThat(resolve).isNotEmpty();

        Set<ProcessDescription> processDescriptions = resolve.get(0).getReadAccess().all(ProcessDescription.class);
        assertThat(processDescriptions).isNotEmpty().hasSize(1);
        ProcessDescription start = processDescriptions.iterator().next();
        assertThat(start.getBranches()).hasSize(1);
        BranchDescription firstBranch = start.getBranches().get(0);
        assertThat(firstBranch.getItems()).hasSize(4)
                .contains("start/seed")
                .contains("start/config")
                .contains("sources/parsing")
                .contains("sources/factory")
        ;
    }

    @Test
    void resolveOneReferenceIsNotAvailable() {

        //given
        File file = new File(RootPath.get() + "/src/test/resources/example/example_broken.yml");
        SeedConfiguration configuration = factory.fromFile(file);
        assertFalse(configuration.getSourceReferences().isEmpty());
        assertFalse(configuration.isPartial());

        //when
        List<LandscapeDescription> resolve = sourceReferencesResolver.resolve(configuration);
        //then
        assertThat(resolve).isNotEmpty();
        assertThat(resolve.get(0)).matches(landscapeDescription -> landscapeDescription.isPartial());

    }

    @Test
    void misconfiguredYaml() {

        //given
        File file = new File(RootPath.get() + "/src/test/resources/example/example_readingerror.yml");
        SeedConfiguration configuration = factory.fromFile(file);
        assertFalse(configuration.getSourceReferences().isEmpty());
        assertFalse(configuration.isPartial());

        //when
        List<LandscapeDescription> resolve = sourceReferencesResolver.resolve(configuration);

        //then
        assertThat(resolve).isNotEmpty();
    }

    @Test
    void firesErrorEvent() {
        //given
        File file = new File(RootPath.get() + "/src/test/resources/example/example_readingerror.yml");
        SeedConfiguration configuration = factory.fromFile(file);
        assertFalse(configuration.getSourceReferences().isEmpty());
        assertFalse(configuration.isPartial());

        //when
        sourceReferencesResolver.resolve(configuration);

        //then
        ArgumentCaptor<ErrorEvent> captor = ArgumentCaptor.forClass(ErrorEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());
        ErrorEvent value = captor.getValue();
        assertThat(value).isNotNull();
        assertThat(value.getEx().getMessage()).contains("Failed to parse yaml service description");
    }
}
