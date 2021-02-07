package de.bonndan.nivio.input;


import de.bonndan.nivio.model.LandscapeConfig;
import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.assessment.kpi.CustomKPI;
import de.bonndan.nivio.assessment.kpi.HealthKPI;
import de.bonndan.nivio.assessment.kpi.KPIConfig;
import de.bonndan.nivio.input.dto.*;
import de.bonndan.nivio.input.http.HttpService;
import de.bonndan.nivio.model.*;
import de.bonndan.nivio.util.RootPath;
import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import static de.bonndan.nivio.model.ItemFactory.getTestItem;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;


class LandscapeDescriptionFactoryTest {

    final private String SEPARATOR = FileSystems.getDefault().getSeparator();
    final private String FILE_PATH = RootPath.get() + SEPARATOR + "src" + SEPARATOR + "test" + SEPARATOR + "resources" + SEPARATOR + "example" + SEPARATOR;
    final private String FILE_PATH_ENV = FILE_PATH + "example_env.yml";
    final private String FILE_PATH_TEMPLATES = FILE_PATH + "example_templates.yml";

    private LandscapeDescriptionFactory factory;

    @BeforeEach
    public void setup() {
        FileFetcher fileFetcher = new FileFetcher(mock(HttpService.class));
        factory = new LandscapeDescriptionFactory(fileFetcher);
    }

    @Test
    public void read() {
        File file = new File(FILE_PATH_ENV);
        LandscapeDescription landscapeDescription = factory.fromYaml(file);
        assertEquals("Landscape example", landscapeDescription.getName());
        assertEquals("nivio:example", landscapeDescription.getIdentifier());
        assertEquals("mail@acme.org", landscapeDescription.getContact());
        assertTrue(landscapeDescription.getDescription().contains("demonstrate"));
        assertEquals(FILE_PATH_ENV, landscapeDescription.getSource());
        assertFalse(landscapeDescription.getSourceReferences().isEmpty());

        SourceReference mapped = landscapeDescription.getSourceReferences().get(1);
        assertNotNull(mapped);
        assertEquals("nivio", mapped.getFormat());
        assertNotNull(landscapeDescription.getConfig());
    }

    @Test
    public void readsMinimalWithIdentifier() {
        assertDoesNotThrow(() -> new LandscapeDescriptionFactory(mock(FileFetcher.class))
                .fromString("yaml", ""));
    }

    @Test
    public void readFails() {
        assertThrows(ReadingException.class, () -> new LandscapeDescriptionFactory(mock(FileFetcher.class))
                .fromString("", ""));
    }

    @Test
    public void readYamlStr() throws IOException {
        File file = new File(FILE_PATH_ENV);
        String yaml = new String(Files.readAllBytes(file.toPath()));
        LandscapeDescription landscapeDescription = factory.fromString(yaml, file.toString());
        assertEquals("Landscape example", landscapeDescription.getName());
        assertEquals("nivio:example", landscapeDescription.getIdentifier());
        assertEquals("mail@acme.org", landscapeDescription.getContact());
        assertTrue(landscapeDescription.getDescription().contains("demonstrate"));
        assertTrue(landscapeDescription.getSource().contains("name: Landscape example"));
        assertFalse(landscapeDescription.getSourceReferences().isEmpty());

        SourceReference mapped = landscapeDescription.getSourceReferences().get(1);
        assertNotNull(mapped);
        assertEquals("nivio", mapped.getFormat());
    }

    @Test
    public void readYamlStrWithUrlSource() throws IOException {

        File file = new File(FILE_PATH_ENV);
        String yaml = new String(Files.readAllBytes(file.toPath()));
        LandscapeDescription landscapeDescription = factory.fromString(yaml, file.toURI().toURL());
        assertEquals(file.toURI().toURL().toString(), landscapeDescription.getSource());
    }

    @Test
    public void readUrlFromDescription() throws IOException {

        File file = new File(FILE_PATH_ENV);
        Landscape outdatedLandscape = LandscapeFactory.createForTesting(
                "test", "testLandscape")
                .withSource(file.toURI().toURL().toString())
                .build();

        //when
        LandscapeDescription landscapeDescription = factory.from(outdatedLandscape);
        assertNotNull(landscapeDescription);
        assertEquals(file.toURI().toURL().toString(), landscapeDescription.getSource());
    }

    @Test
    public void readUrl() throws IOException {

        //given
        File file = new File(FILE_PATH_ENV);

        //when
        LandscapeDescription landscapeDescription = factory.from(file.toURI().toURL());

        //then
        assertNotNull(landscapeDescription);
        assertEquals(file.toURI().toURL().toString(), landscapeDescription.getSource());
    }

    @Test
    public void readIncremental() {
        final String FILE_PATH_INCREMENTAL_ENV = FILE_PATH + "example_incremental_env.yml";
        File file = new File(FILE_PATH_INCREMENTAL_ENV);
        LandscapeDescription landscapeDescription = factory.fromYaml(file);
        assertEquals("Incremental update example", landscapeDescription.getName());
        assertEquals("nivio:incremental", landscapeDescription.getIdentifier());
        assertFalse(landscapeDescription.getSourceReferences().isEmpty());
    }

    @Test
    public void readEnvVars() throws IOException {
        final String FILE_PATH_ENVIRONMENT_VARS;
        final String user;
        if (SystemUtils.IS_OS_WINDOWS) {
            FILE_PATH_ENVIRONMENT_VARS = FILE_PATH + "example_environment_vars_windows.yml";
            user = System.getenv("USERNAME");
        } else {
            FILE_PATH_ENVIRONMENT_VARS = FILE_PATH + "example_environment_vars_unix.yml";
            user = System.getenv("USER");
        }

        File file = new File(FILE_PATH_ENVIRONMENT_VARS);
        String read = new String(Files.readAllBytes(file.toPath()));
        LandscapeDescription landscapeDescription = factory.fromString(read, file.toString());
        assertNotNull(landscapeDescription);
        assertEquals(user, landscapeDescription.getSourceReferences().get(0).getHeaderTokenValue());
    }

    @Test
    public void customLabels() {
        final String FILE_PATH_ENVIRONMENT_VARS = FILE_PATH + "example_items_extrafields.yml";
        File file = new File(FILE_PATH_ENVIRONMENT_VARS);

        LandscapeDescription landscapeDescription = factory.fromYaml(file);
        assertNotNull(landscapeDescription);
        ItemDescription one = landscapeDescription.getItemDescriptions().pick("one", null);
        assertNotNull(one);
        assertEquals("bar", one.getLabel("foo"));
        assertEquals("baz", one.getLabel("bar"));
    }

    @Test
    public void environmentTemplatesRead() {
        File file = new File(FILE_PATH_TEMPLATES);
        LandscapeDescription landscapeDescription = factory.fromYaml(file);
        assertNotNull(landscapeDescription);
        assertNotNull(landscapeDescription.getTemplates());
        assertEquals(2, landscapeDescription.getTemplates().size());

        ItemDescription template = landscapeDescription.getTemplates().get("myfirsttemplate");
        assertNotNull(template);
        ItemDescription groupTemplate = landscapeDescription.getTemplates().get("insamegroup");
        assertNotNull(groupTemplate);
    }

    @Test
    public void environmentTemplatesSanitized() {
        File file = new File(FILE_PATH_TEMPLATES);
        LandscapeDescription landscapeDescription = factory.fromYaml(file);

        ItemDescription template = landscapeDescription.getTemplates().get("myfirsttemplate");

        assertEquals("webservice", template.getLabel(Label.type));
        assertTrue(template.getName().isEmpty());
    }

    @Test
    public void templatesAssigned() {
        File file = new File(FILE_PATH_TEMPLATES);
        LandscapeDescription landscapeDescription = factory.fromYaml(file);

        SourceReference ref = landscapeDescription.getSourceReferences().get(0);

        assertNotNull(ref.getAssignTemplates());
        assertEquals(2, ref.getAssignTemplates().size());
        assertTrue(ref.getAssignTemplates().containsKey("myfirsttemplate"));
        List<String> assignments = ref.getAssignTemplates().get("myfirsttemplate");
        assertNotNull(assignments);
    }

    @Test
    public void templatesAssignedWithDataflow() {
        final String FILE_PATH_TEMPLATES_2 = FILE_PATH + "example_templates2.yml";
        File file = new File(FILE_PATH_TEMPLATES_2);
        LandscapeDescription landscapeDescription = factory.fromYaml(file);

        ItemDescription template = landscapeDescription.getTemplates().get("addDataFlow");
        RelationDescription df = (RelationDescription) template.getRelations().toArray()[0];
        assertEquals("identifier LIKE 'other_crappy_name%'", df.getTarget());
    }

    @Test
    public void configRead() {
        final String FILE_PATH_CONFIG = FILE_PATH + "example_config.yml";
        File file = new File(FILE_PATH_CONFIG);
        LandscapeDescription landscapeDescription = factory.fromYaml(file);

        LandscapeConfig config = landscapeDescription.getConfig();

        assertNotNull(config);
        assertNotNull(config.getGroupLayoutConfig());
        assertNotNull(config.getGroupLayoutConfig().getForceConstantFactor());
    }

    @Test
    public void testReadUnknownFile() {
        final String FILE_PATH_UNKNOWN = FILE_PATH + "example_xxx.yml";
        File file = new File(FILE_PATH_UNKNOWN);


        try {
            factory.fromYaml(file);
        } catch (ReadingException ex) {
            assertTrue(ex.getMessage().contains("Failed to read file"));
            return;
        }

        fail("ReadingException was not thrown");
    }

    @Test
    public void testReadGroups() {
        final String FILE_PATH_GROUPS = FILE_PATH + "example_groups.yml";
        File file = new File(FILE_PATH_GROUPS);
        LandscapeDescription landscapeDescription = factory.fromYaml(file);

        Map<String, GroupDescription> groups = landscapeDescription.getGroups();
        assertNotNull(groups);
        assertEquals(2, groups.size());
        GroupDescription groupDescription = groups.get("groupA");
        assertNotNull(groupDescription);
        assertEquals("groupA", groupDescription.getIdentifier());

        GroupDescription b = groups.get("B");
        assertNotNull(b);
        assertEquals("B", b.getIdentifier());
    }

    @Test
    public void testGroupsHaveEnv() {
        final String FILE_PATH_GROUPS = FILE_PATH + "example_groups.yml";
        File file = new File(FILE_PATH_GROUPS);
        LandscapeDescription landscapeDescription = factory.fromYaml(file);

        Map<String, GroupDescription> groups = landscapeDescription.getGroups();
        assertNotNull(groups);
        assertEquals(2, groups.size());
        GroupDescription groupDescription = groups.get("groupA");
        assertNotNull(groupDescription);
        assertEquals(landscapeDescription.getIdentifier(), groupDescription.getFullyQualifiedIdentifier().getLandscape());

        GroupDescription b = groups.get("B");
        assertNotNull(b);
        assertEquals(landscapeDescription.getIdentifier(), b.getFullyQualifiedIdentifier().getLandscape());
    }

    @Test
    public void readCustomKPIs() {
        File file = new File(RootPath.get() + "/src/test/resources/example/example_kpis.yml");
        LandscapeDescription landscapeDescription = factory.fromYaml(file);
        LandscapeConfig config = landscapeDescription.getConfig();

        Map<String, KPIConfig> kpIs = config.getKPIs();
        assertNotNull(kpIs);
        assertEquals(3, kpIs.size());

        KPIConfig health = kpIs.get(HealthKPI.IDENTIFIER);
        assertNotNull(health);
        assertEquals("can be overridden", health.description);

        KPIConfig monthlyCosts = kpIs.get("monthlyCosts");
        assertNotNull(monthlyCosts);
        assertEquals("Evaluates the monthly maintenance costs", monthlyCosts.description);

        CustomKPI costKPI = new CustomKPI();
        costKPI.init(monthlyCosts);

        Item item = getTestItem("test", "a");
        item.setLabel(Label.costs, "200");
        StatusValue statusValue = costKPI.getStatusValues(item).get(0);
        assertNotNull(statusValue);
        assertEquals(Status.RED, statusValue.getStatus());
    }

    @Test
    public void fromBodyItem() {

        //when
        LandscapeDescription landscapeDescription = factory.fromBodyItems("foo", "nivio", "body");

        assertNotNull(landscapeDescription);
        assertThat(landscapeDescription.getIdentifier()).isEqualTo("foo");
        assertThat(landscapeDescription.getSourceReferences().size()).isEqualTo(1);
        SourceReference sourceReference = landscapeDescription.getSourceReferences().get(0);
        assertThat(sourceReference.getFormat()).isEqualTo("nivio");
        assertThat(sourceReference.getContent()).isEqualTo("body");
    }
}
