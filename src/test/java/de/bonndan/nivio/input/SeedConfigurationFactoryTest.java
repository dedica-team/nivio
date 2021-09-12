package de.bonndan.nivio.input;

import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.assessment.kpi.CustomKPI;
import de.bonndan.nivio.assessment.kpi.HealthKPI;
import de.bonndan.nivio.assessment.kpi.KPIConfig;
import de.bonndan.nivio.input.dto.*;
import de.bonndan.nivio.input.http.HttpService;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.model.LandscapeConfig;
import de.bonndan.nivio.util.RootPath;
import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import static de.bonndan.nivio.model.ItemFactory.getTestItem;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class SeedConfigurationFactoryTest {

    final private String SEPARATOR = FileSystems.getDefault().getSeparator();
    final private String FILE_PATH = RootPath.get() + SEPARATOR + "src" + SEPARATOR + "test" + SEPARATOR + "resources" + SEPARATOR + "example" + SEPARATOR;
    final private String FILE_PATH_ENV = FILE_PATH + "example_env.yml";
    final private String FILE_PATH_TEMPLATES = FILE_PATH + "example_templates.yml";

    private SeedConfigurationFactory factory;

    @BeforeEach
    public void setup() {
        FileFetcher fileFetcher = new FileFetcher(mock(HttpService.class));
        factory = new SeedConfigurationFactory(fileFetcher);
    }

    @Test
    void read() throws MalformedURLException {
        File file = new File(FILE_PATH_ENV);
        SeedConfiguration configuration = factory.fromFile(file);
        assertEquals("Landscape example", configuration.getName());
        assertEquals("nivio:example", configuration.getIdentifier());
        assertEquals("mail@acme.org", configuration.getContact());
        assertTrue(configuration.getDescription().contains("demonstrate"));
        assertEquals(file.toURI().toURL().toString(), configuration.getSource().getURL().get().toString());
        assertFalse(configuration.getSourceReferences().isEmpty());

        SourceReference mapped = configuration.getSourceReferences().get(1);
        assertNotNull(mapped);
        assertEquals("nivio", mapped.getFormat());
        assertNotNull(configuration.getConfig());
    }

    @Test
    void readsMinimalWithIdentifier() {
        assertDoesNotThrow(() -> new SeedConfigurationFactory(mock(FileFetcher.class))
                .fromString("yaml", new Source("")));
    }

    @Test
    void readFails() {
        assertThrows(ReadingException.class, () -> new SeedConfigurationFactory(mock(FileFetcher.class))
                .fromString("",new Source("")));
    }

    @Test
    void readYamlStr() {
        File file = new File(FILE_PATH_ENV);
        SeedConfiguration configuration = factory.fromFile(file);
        assertEquals("Landscape example", configuration.getName());
        assertEquals("nivio:example", configuration.getIdentifier());
        assertEquals("mail@acme.org", configuration.getContact());
        assertTrue(configuration.getDescription().contains("demonstrate"));
        assertThat(configuration.getSource().get()).contains(file.getName());
        assertFalse(configuration.getSourceReferences().isEmpty());

        SourceReference mapped = configuration.getSourceReferences().get(1);
        assertNotNull(mapped);
        assertEquals("nivio", mapped.getFormat());
    }

    @Test
    void readYamlStrWithUrlSource() throws IOException {

        File file = new File(FILE_PATH_ENV);
        String yaml = new String(Files.readAllBytes(file.toPath()));
        SeedConfiguration configuration = factory.fromString(yaml, file.toURI().toURL());
        assertEquals(file.toURI().toURL().toString(), configuration.getSource().getURL().get().toString());
    }

    @Test
    void readUrl() throws IOException {

        //given
        File file = new File(FILE_PATH_ENV);

        //when
        SeedConfiguration configuration = factory.from(file.toURI().toURL());

        //then
        assertNotNull(configuration);
        assertEquals(file.toURI().toURL().toString(), configuration.getSource().getURL().get().toString());
    }

    @Test
    void readIncremental() {
        final String FILE_PATH_INCREMENTAL_ENV = FILE_PATH + "example_incremental_env.yml";
        File file = new File(FILE_PATH_INCREMENTAL_ENV);
        SeedConfiguration configuration = factory.fromFile(file);
        assertEquals("Incremental update example", configuration.getName());
        assertEquals("nivio:incremental", configuration.getIdentifier());
        assertFalse(configuration.getSourceReferences().isEmpty());
    }

    @Test
    void readEnvVars() {
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
        SeedConfiguration configuration = factory.fromFile(file);
        assertNotNull(configuration);
        assertEquals(user, configuration.getSourceReferences().get(0).getHeaderTokenValue());
    }

    @Test
    void customLabels() {
        final String FILE_PATH_ENVIRONMENT_VARS = FILE_PATH + "example_items_extrafields.yml";
        File file = new File(FILE_PATH_ENVIRONMENT_VARS);

        SeedConfiguration configuration = factory.fromFile(file);
        assertNotNull(configuration);
        ItemDescription one = configuration.getItems().stream().filter(itemDescription -> itemDescription.getIdentifier().equals("one")).findFirst().orElseThrow();
        assertNotNull(one);
        assertEquals("bar", one.getLabel("foo"));
        assertEquals("baz", one.getLabel("bar"));
    }

    @Test
    void environmentTemplatesRead() {
        File file = new File(FILE_PATH_TEMPLATES);
        SeedConfiguration configuration = factory.fromFile(file);
        assertNotNull(configuration);
        assertNotNull(configuration.getTemplates());
        assertEquals(2, configuration.getTemplates().size());

        ItemDescription template = configuration.getTemplates().get("myfirsttemplate");
        assertNotNull(template);
        ItemDescription groupTemplate = configuration.getTemplates().get("insamegroup");
        assertNotNull(groupTemplate);
    }

    @Test
    void environmentTemplatesSanitized() {
        File file = new File(FILE_PATH_TEMPLATES);
        SeedConfiguration configuration = factory.fromFile(file);

        ItemDescription template = configuration.getTemplates().get("myfirsttemplate");

        assertEquals("webservice", template.getType());
        assertTrue(template.getName().isEmpty());
    }

    @Test
    void templatesAssigned() {
        File file = new File(FILE_PATH_TEMPLATES);
        SeedConfiguration configuration = factory.fromFile(file);

        SourceReference ref = configuration.getSourceReferences().get(0);

        assertNotNull(ref.getAssignTemplates());
        assertEquals(2, ref.getAssignTemplates().size());
        assertTrue(ref.getAssignTemplates().containsKey("myfirsttemplate"));
        List<String> assignments = ref.getAssignTemplates().get("myfirsttemplate");
        assertNotNull(assignments);
    }

    @Test
    void templatesAssignedWithDataflow() {
        final String FILE_PATH_TEMPLATES_2 = FILE_PATH + "example_templates2.yml";
        File file = new File(FILE_PATH_TEMPLATES_2);
        SeedConfiguration configuration = factory.fromFile(file);

        ItemDescription template = configuration.getTemplates().get("addDataFlow");
        RelationDescription df = (RelationDescription) template.getRelations().toArray()[0];
        assertEquals("identifier LIKE 'other_crappy_name%'", df.getTarget());
    }

    @Test
    void configRead() {
        final String FILE_PATH_CONFIG = FILE_PATH + "example_config.yml";
        File file = new File(FILE_PATH_CONFIG);
        SeedConfiguration configuration = factory.fromFile(file);

        LandscapeConfig config = configuration.getConfig();

        assertNotNull(config);
        assertNotNull(config.getGroupLayoutConfig());
        assertNotNull(config.getGroupLayoutConfig().getForceConstantFactor());
    }

    @Test
    void testReadUnknownFile() {
        final String FILE_PATH_UNKNOWN = FILE_PATH + "example_xxx.yml";
        File file = new File(FILE_PATH_UNKNOWN);


        try {
            factory.fromFile(file);
        } catch (ReadingException ex) {
            assertTrue(ex.getMessage().contains("Failed to read file"));
            return;
        }

        fail("ReadingException was not thrown");
    }

    @Test
    void testReadGroups() {
        final String FILE_PATH_GROUPS = FILE_PATH + "example_groups.yml";
        File file = new File(FILE_PATH_GROUPS);
        SeedConfiguration configuration = factory.fromFile(file);

        Map<String, GroupDescription> groups = configuration.getGroups();
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
    void testGroupsHaveEnv() {
        final String FILE_PATH_GROUPS = FILE_PATH + "example_groups.yml";
        File file = new File(FILE_PATH_GROUPS);
        SeedConfiguration seedConfiguration = factory.fromFile(file);

        Map<String, GroupDescription> groups = seedConfiguration.getGroups();
        assertNotNull(groups);
        assertEquals(2, groups.size());
        GroupDescription groupDescription = groups.get("groupA");
        assertNotNull(groupDescription);
        assertEquals(seedConfiguration.getIdentifier(), groupDescription.getFullyQualifiedIdentifier().getLandscape());

        GroupDescription b = groups.get("B");
        assertNotNull(b);
        assertEquals(seedConfiguration.getIdentifier(), b.getFullyQualifiedIdentifier().getLandscape());
    }

    @Test
    void testUnknownProperty() {
        assertDoesNotThrow(() ->factory.fromFile(new File(FILE_PATH + "example_typo.yml")));
    }

    @Test
    void readCustomKPIs() {
        File file = new File(RootPath.get() + "/src/test/resources/example/example_kpis.yml");
        SeedConfiguration configuration = factory.fromFile(file);
        LandscapeConfig config = configuration.getConfig();

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

}
