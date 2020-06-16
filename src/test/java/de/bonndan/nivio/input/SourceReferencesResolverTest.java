package de.bonndan.nivio.input;

import de.bonndan.nivio.input.compose2.ItemDescriptionFactoryCompose2;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.http.HttpService;
import de.bonndan.nivio.input.nivio.ItemDescriptionFactoryNivio;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.util.RootPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class SourceReferencesResolverTest {

    @Mock
    ProcessLog log;

    private SourceReferencesResolver sourceReferencesResolver;
    private LandscapeDescriptionFactory factory;

    @BeforeEach
    public void setup() {
        log = new ProcessLog(LoggerFactory.getLogger(SourceReferencesResolver.class));
        sourceReferencesResolver = new SourceReferencesResolver(
                new ItemDescriptionFormatFactory(
                        new ArrayList<ItemDescriptionFactory>(Arrays.asList(ItemDescriptionFactoryNivio.forTesting(), ItemDescriptionFactoryCompose2.forTesting()))
                )
                , log
        );

        FileFetcher fileFetcher = new FileFetcher(mock(HttpService.class));
        factory = new LandscapeDescriptionFactory(mock(ApplicationEventPublisher.class), fileFetcher);
    }

    @Test
    public void resolve() {

        File file = new File(RootPath.get() + "/src/test/resources/example/example_incremental_env.yml");
        LandscapeDescription landscapeDescription = factory.fromYaml(file);
        assertFalse(landscapeDescription.getSourceReferences().isEmpty());

        Map<ItemDescription, List<String>> templatesAndTargets = new HashMap<>();
        sourceReferencesResolver.resolve(landscapeDescription, templatesAndTargets);

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

        //when
        Map<ItemDescription, List<String>> templatesAndTargets = new HashMap<>();
        sourceReferencesResolver.resolve(landscapeDescription, templatesAndTargets);

        //then
        assertFalse(StringUtils.isEmpty(log.getError()));
        assertTrue(landscapeDescription.isPartial());
    }

    @Test
    public void readsTemplates() {

        //given
        File file = new File(RootPath.get() + "/src/test/resources/example/example_templates.yml");
        LandscapeDescription landscapeDescription = factory.fromYaml(file);

        //when
        Map<ItemDescription, List<String>> templatesAndTargets = new HashMap<>();
        sourceReferencesResolver.resolve(landscapeDescription, templatesAndTargets);

        //then
        assertFalse(templatesAndTargets.isEmpty());
        assertEquals(2, templatesAndTargets.size());
    }

}
