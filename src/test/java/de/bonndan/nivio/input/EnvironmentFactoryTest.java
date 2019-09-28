package de.bonndan.nivio.input;


import de.bonndan.nivio.input.dto.Environment;
import de.bonndan.nivio.input.dto.ServiceDescription;
import de.bonndan.nivio.input.dto.SourceFormat;
import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.landscape.DataFlowItem;
import de.bonndan.nivio.landscape.LandscapeConfig;
import de.bonndan.nivio.landscape.LandscapeConfig.GroupConfig;
import de.bonndan.nivio.landscape.ServiceItem;
import de.bonndan.nivio.landscape.StateProviderConfig;
import de.bonndan.nivio.util.RootPath;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static de.bonndan.nivio.landscape.ServiceItems.pick;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class EnvironmentFactoryTest {

    @Test
    public void read() {

        File file = new File(RootPath.get() + "/src/test/resources/example/example_env.yml");
        Environment environment = EnvironmentFactory.fromYaml(file);
        assertEquals("Landscape example", environment.getName());
        assertEquals("nivio:example", environment.getIdentifier());
        assertEquals("mail@acme.org", environment.getContact());
        assertEquals(RootPath.get() + "/src/test/resources/example/example_env.yml", environment.getSource());
        assertFalse(environment.getSourceReferences().isEmpty());

        SourceReference mapped = environment.getSourceReferences().get(1);
        assertNotNull(mapped);
        assertEquals(SourceFormat.NIVIO, mapped.getFormat());
        assertNotNull(environment.getConfig());
        assertTrue(environment.getConfig().getGroupConfig("content").isPresent());
    }

    @Test
    public void readYamlStr() throws IOException {

        File file = new File(RootPath.get() + "/src/test/resources/example/example_env.yml");
        String yaml = new String(Files.readAllBytes(file.toPath()));
        Environment environment = EnvironmentFactory.fromString(yaml);
        assertEquals("Landscape example", environment.getName());
        assertEquals("nivio:example", environment.getIdentifier());
        assertEquals("mail@acme.org", environment.getContact());
        assertTrue(environment.getSource().contains("name: Landscape example"));
        assertFalse(environment.getSourceReferences().isEmpty());

        SourceReference mapped = environment.getSourceReferences().get(1);
        assertNotNull(mapped);
        assertEquals(SourceFormat.NIVIO, mapped.getFormat());
    }

    @Test
    public void readYamlStrWithUrlSource() throws IOException {

        File file = new File(RootPath.get() + "/src/test/resources/example/example_env.yml");
        String yaml = new String(Files.readAllBytes(file.toPath()));
        Environment environment = EnvironmentFactory.fromString(yaml, file.toURI().toURL());
        assertEquals(file.toURI().toURL().toString(), environment.getSource());
    }

    @Test
    public void readIncremental() {

        File file = new File(RootPath.get() + "/src/test/resources/example/example_incremental_env.yml");
        Environment environment = EnvironmentFactory.fromYaml(file);
        assertEquals("Incremental update example", environment.getName());
        assertEquals("nivio:incremental", environment.getIdentifier());
        assertFalse(environment.getSourceReferences().isEmpty());
    }

    @Test
    public void readStateProviders() {

        File file = new File(RootPath.get() + "/src/test/resources/example/example_providers.yml");
        Environment environment = EnvironmentFactory.fromYaml(file);
        assertFalse(environment.getStateProviders().isEmpty());

        StateProviderConfig cfg = environment.getStateProviders().get(0);
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

        Environment environment = EnvironmentFactory.fromString(read);
        assertNotNull(environment);
        assertEquals("veryPrivateToken", environment.getSourceReferences().get(0).getHeaderTokenValue());
    }

    @Test
    public void environmentTemplatesRead() {
        File file = new File(RootPath.get() + "/src/test/resources/example/example_templates.yml");
        Environment environment = EnvironmentFactory.fromYaml(file);
        assertNotNull(environment.getTemplates());
        assertEquals(2, environment.getTemplates().size());

        ServiceItem template = pick("myfirsttemplate", null, environment.getTemplates());
        assertNotNull(template);
        ServiceItem groupTemplate = pick("insamegroup", null, environment.getTemplates());
        assertNotNull(groupTemplate);
    }

    @Test
    public void environmentTemplatesSanitized() {
        File file = new File(RootPath.get() + "/src/test/resources/example/example_templates.yml");
        Environment environment = EnvironmentFactory.fromYaml(file);

        ServiceItem template = pick("myfirsttemplate", null, environment.getTemplates());

        assertEquals("webservice", template.getType());
        assertTrue(template.getName().isEmpty());
        assertTrue(template.getShort_name().isEmpty());
    }

    @Test
    public void templatesAssigned() {
        File file = new File(RootPath.get() + "/src/test/resources/example/example_templates.yml");
        Environment environment = EnvironmentFactory.fromYaml(file);

        SourceReference ref = environment.getSourceReferences().get(0);

        assertNotNull(ref.getAssignTemplates());
        assertEquals(2, ref.getAssignTemplates().size());
        assertTrue(ref.getAssignTemplates().containsKey("myfirsttemplate"));
        List<String> assignments = ref.getAssignTemplates().get("myfirsttemplate");
        assertNotNull(assignments);
    }

    @Test
    public void templatesAssignedWithDataflow() {
        File file = new File(RootPath.get() + "/src/test/resources/example/example_templates2.yml");
        Environment environment = EnvironmentFactory.fromYaml(file);

        ServiceDescription template = environment.getTemplates().get(3);
        DataFlowItem df = (DataFlowItem) template.getDataFlow().toArray()[0];
        assertEquals("identifier LIKE 'other_crappy_name%'", df.getTarget());
    }

    @Test
    public void configRead() {
        File file = new File(RootPath.get() + "/src/test/resources/example/example_config.yml");
        Environment environment = EnvironmentFactory.fromYaml(file);

        LandscapeConfig config = environment.getConfig();

        assertNotNull(config);
        assertFalse(config.getGroupConfig("notpresent").isPresent());
        assertTrue(config.getGroupConfig("test1").isPresent());
        assertEquals("#234234", config.getGroupConfig("test1").map(GroupConfig::getColor).get());
    }
}