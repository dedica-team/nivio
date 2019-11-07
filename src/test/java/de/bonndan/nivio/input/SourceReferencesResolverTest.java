package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.RelationItem;
import de.bonndan.nivio.model.RelationType;
import de.bonndan.nivio.util.RootPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static de.bonndan.nivio.model.ServiceItems.pick;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

public class SourceReferencesResolverTest {

    @Mock
    ProcessLog log;

    @BeforeEach
    public void setup() {
        log = new ProcessLog(Mockito.mock(Logger.class));
    }

    @Test
    public void resolve() {

        File file = new File(RootPath.get() + "/src/test/resources/example/example_incremental_env.yml");
        LandscapeDescription landscapeDescription = LandscapeDescriptionFactory.fromYaml(file);
        assertFalse(landscapeDescription.getSourceReferences().isEmpty());

        SourceReferencesResolver sourceReferencesResolver = new SourceReferencesResolver(log);
        Map<ItemDescription, List<String>> templatesAndTargets = new HashMap<>();
        sourceReferencesResolver.resolve(landscapeDescription, templatesAndTargets);

        ItemDescription mapped = (ItemDescription) pick("blog-server", null, landscapeDescription.getItemDescriptions());
        assertNotNull(mapped);
        assertEquals("blog1", mapped.getShortName());
        assertEquals("name2", mapped.getName());
    }

    @Test
    public void resolveOneReferenceIsNotAvailable() {

        //given
        File file = new File(RootPath.get() + "/src/test/resources/example/example_broken.yml");
        LandscapeDescription landscapeDescription = LandscapeDescriptionFactory.fromYaml(file);
        assertFalse(landscapeDescription.getSourceReferences().isEmpty());
        assertFalse(landscapeDescription.isPartial());

        //when
        SourceReferencesResolver sourceReferencesResolver = new SourceReferencesResolver(log);
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
        LandscapeDescription landscapeDescription = LandscapeDescriptionFactory.fromYaml(file);

        //when
        SourceReferencesResolver sourceReferencesResolver = new SourceReferencesResolver(log);
        Map<ItemDescription, List<String>> templatesAndTargets = new HashMap<>();
        sourceReferencesResolver.resolve(landscapeDescription, templatesAndTargets);

        //then
        assertFalse(templatesAndTargets.isEmpty());
        assertEquals(2, templatesAndTargets.size());
    }

}
