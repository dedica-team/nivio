package de.bonndan.nivio.input;


import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.SourceFormat;
import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.model.DataFlowItem;
import de.bonndan.nivio.model.LandscapeConfig;
import de.bonndan.nivio.model.LandscapeConfig.GroupConfig;
import de.bonndan.nivio.model.LandscapeItem;
import de.bonndan.nivio.model.StateProviderConfig;
import de.bonndan.nivio.util.RootPath;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static de.bonndan.nivio.model.ServiceItems.pick;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class LandscapeDescriptionFactoryTest {

    @Test
    public void read() {

        File file = new File(RootPath.get() + "/src/test/resources/example/example_env.yml");
        LandscapeDescription landscapeDescription = EnvironmentFactory.fromYaml(file);
        assertEquals("Landscape example", landscapeDescription.getName());
        assertEquals("nivio:example", landscapeDescription.getIdentifier());
        assertEquals("mail@acme.org", landscapeDescription.getContact());
        assertEquals(RootPath.get() + "/src/test/resources/example/example_env.yml", landscapeDescription.getSource());
        assertFalse(landscapeDescription.getSourceReferences().isEmpty());

        SourceReference mapped = landscapeDescription.getSourceReferences().get(1);
        assertNotNull(mapped);
        assertEquals(SourceFormat.NIVIO, mapped.getFormat());
        assertNotNull(landscapeDescription.getConfig());
        assertTrue(landscapeDescription.getConfig().getGroupConfig("content").isPresent());
    }

    @Test
    public void readYamlStr() throws IOException {

        File file = new File(RootPath.get() + "/src/test/resources/example/example_env.yml");
        String yaml = new String(Files.readAllBytes(file.toPath()));
        LandscapeDescription landscapeDescription = EnvironmentFactory.fromString(yaml);
        assertEquals("Landscape example", landscapeDescription.getName());
        assertEquals("nivio:example", landscapeDescription.getIdentifier());
        assertEquals("mail@acme.org", landscapeDescription.getContact());
        assertTrue(landscapeDescription.getSource().contains("name: Landscape example"));
        assertFalse(landscapeDescription.getSourceReferences().isEmpty());

        SourceReference mapped = landscapeDescription.getSourceReferences().get(1);
        assertNotNull(mapped);
        assertEquals(SourceFormat.NIVIO, mapped.getFormat());
    }

    @Test
    public void readYamlStrWithUrlSource() throws IOException {

        File file = new File(RootPath.get() + "/src/test/resources/example/example_env.yml");
        String yaml = new String(Files.readAllBytes(file.toPath()));
        LandscapeDescription landscapeDescription = EnvironmentFactory.fromString(yaml, file.toURI().toURL());
        assertEquals(file.toURI().toURL().toString(), landscapeDescription.getSource());
    }

    @Test
    public void readIncremental() {

        File file = new File(RootPath.get() + "/src/test/resources/example/example_incremental_env.yml");
        LandscapeDescription landscapeDescription = EnvironmentFactory.fromYaml(file);
        assertEquals("Incremental update example", landscapeDescription.getName());
        assertEquals("nivio:incremental", landscapeDescription.getIdentifier());
        assertFalse(landscapeDescription.getSourceReferences().isEmpty());
    }

    @Test
    public void readStateProviders() {

        File file = new File(RootPath.get() + "/src/test/resources/example/example_providers.yml");
        LandscapeDescription landscapeDescription = EnvironmentFactory.fromYaml(file);
        assertFalse(landscapeDescription.getStateProviders().isEmpty());

        StateProviderConfig cfg = landscapeDescription.getStateProviders().get(0);
        assertNotNull(cfg);
        assertEquals("prometheus-exporter", cfg.getType());
        assertTrue(cfg.getTarget().contains("example/rancher_prometheus_exporter.txt"));
    }

    @Test
    public void readEnvVars() throws IOException, NoSuchFieldException, IllegalAccessException {

        File file = new File(RootPath.get() + "/src/test/resources/example/example_environment_vars.yml");
        String read = new String(Files.readAllBytes(file.toPath()));

        for (Class c : Collections.class.getDeclaredClasses()) {
            if ("java.util.Collections$UnmodifiableMap".equals(c.getName())) {
                Field m = c.getDeclaredField("m");
                m.setAccessible(true);
                var x = (Map<String, String>)m.get(System.getenv());
                x.put("PRIVATE_TOKEN", "veryPrivateToken");
            }
        };

        LandscapeDescription landscapeDescription = EnvironmentFactory.fromString(read);
        assertNotNull(landscapeDescription);
        assertEquals("veryPrivateToken", landscapeDescription.getSourceReferences().get(0).getHeaderTokenValue());
    }

    @Test
    public void environmentTemplatesRead() {
        File file = new File(RootPath.get() + "/src/test/resources/example/example_templates.yml");
        LandscapeDescription landscapeDescription = EnvironmentFactory.fromYaml(file);
        assertNotNull(landscapeDescription.getTemplates());
        assertEquals(2, landscapeDescription.getTemplates().size());

        LandscapeItem template = pick("myfirsttemplate", null, landscapeDescription.getTemplates());
        assertNotNull(template);
        LandscapeItem groupTemplate = pick("insamegroup", null, landscapeDescription.getTemplates());
        assertNotNull(groupTemplate);
    }

    @Test
    public void environmentTemplatesSanitized() {
        File file = new File(RootPath.get() + "/src/test/resources/example/example_templates.yml");
        LandscapeDescription landscapeDescription = EnvironmentFactory.fromYaml(file);

        LandscapeItem template = pick("myfirsttemplate", null, landscapeDescription.getTemplates());

        assertEquals("webservice", template.getType());
        assertTrue(template.getName().isEmpty());
        assertTrue(template.getShortName().isEmpty());
    }

    @Test
    public void templatesAssigned() {
        File file = new File(RootPath.get() + "/src/test/resources/example/example_templates.yml");
        LandscapeDescription landscapeDescription = EnvironmentFactory.fromYaml(file);

        SourceReference ref = landscapeDescription.getSourceReferences().get(0);

        assertNotNull(ref.getAssignTemplates());
        assertEquals(2, ref.getAssignTemplates().size());
        assertTrue(ref.getAssignTemplates().containsKey("myfirsttemplate"));
        List<String> assignments = ref.getAssignTemplates().get("myfirsttemplate");
        assertNotNull(assignments);
    }

    @Test
    public void templatesAssignedWithDataflow() {
        File file = new File(RootPath.get() + "/src/test/resources/example/example_templates2.yml");
        LandscapeDescription landscapeDescription = EnvironmentFactory.fromYaml(file);

        ItemDescription template = landscapeDescription.getTemplates().get(3);
        DataFlowItem df = (DataFlowItem) template.getDataFlow().toArray()[0];
        assertEquals("identifier LIKE 'other_crappy_name%'", df.getTarget());
    }

    @Test
    public void configRead() {
        File file = new File(RootPath.get() + "/src/test/resources/example/example_config.yml");
        LandscapeDescription landscapeDescription = EnvironmentFactory.fromYaml(file);

        LandscapeConfig config = landscapeDescription.getConfig();

        assertNotNull(config);
        assertFalse(config.getGroupConfig("notpresent").isPresent());
        assertTrue(config.getGroupConfig("test1").isPresent());
        assertEquals("#234234", config.getGroupConfig("test1").map(GroupConfig::getColor).get());
    }
}