package de.bonndan.nivio.input;


import de.bonndan.nivio.LandscapeConfig;
import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.assessment.kpi.CustomKPI;
import de.bonndan.nivio.assessment.kpi.HealthKPI;
import de.bonndan.nivio.assessment.kpi.KPI;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.model.*;
import de.bonndan.nivio.util.RootPath;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class LandscapeDescriptionFactoryTest {

    @Test
    public void read() {

        File file = new File(RootPath.get() + "/src/test/resources/example/example_env.yml");
        LandscapeDescription landscapeDescription = LandscapeDescriptionFactory.fromYaml(file);
        assertEquals("Landscape example", landscapeDescription.getName());
        assertEquals("nivio:example", landscapeDescription.getIdentifier());
        assertEquals("mail@acme.org", landscapeDescription.getContact());
        assertTrue(landscapeDescription.getDescription().contains("demonstrate"));
        assertEquals(RootPath.get() + "/src/test/resources/example/example_env.yml", landscapeDescription.getSource());
        assertFalse(landscapeDescription.getSourceReferences().isEmpty());

        SourceReference mapped = landscapeDescription.getSourceReferences().get(1);
        assertNotNull(mapped);
        assertEquals("nivio", mapped.getFormat());
        assertNotNull(landscapeDescription.getConfig());
    }

    @Test
    public void readYamlStr() throws IOException {

        File file = new File(RootPath.get() + "/src/test/resources/example/example_env.yml");
        String yaml = new String(Files.readAllBytes(file.toPath()));
        LandscapeDescription landscapeDescription = LandscapeDescriptionFactory.fromString(yaml, file.toString());
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

        File file = new File(RootPath.get() + "/src/test/resources/example/example_env.yml");
        String yaml = new String(Files.readAllBytes(file.toPath()));
        LandscapeDescription landscapeDescription = LandscapeDescriptionFactory.fromString(yaml, file.toURI().toURL());
        assertEquals(file.toURI().toURL().toString(), landscapeDescription.getSource());
    }

    @Test
    public void readIncremental() {

        File file = new File(RootPath.get() + "/src/test/resources/example/example_incremental_env.yml");
        LandscapeDescription landscapeDescription = LandscapeDescriptionFactory.fromYaml(file);
        assertEquals("Incremental update example", landscapeDescription.getName());
        assertEquals("nivio:incremental", landscapeDescription.getIdentifier());
        assertFalse(landscapeDescription.getSourceReferences().isEmpty());
    }

    @Test
    public void readEnvVars() throws IOException, NoSuchFieldException, IllegalAccessException {

        File file = new File(RootPath.get() + "/src/test/resources/example/example_environment_vars.yml");
        String read = new String(Files.readAllBytes(file.toPath()));

        for (Class c : Collections.class.getDeclaredClasses()) {
            if ("java.util.Collections$UnmodifiableMap".equals(c.getName())) {
                Field m = c.getDeclaredField("m");
                m.setAccessible(true);
                var x = (Map<String, String>) m.get(System.getenv());
                x.put("PRIVATE_TOKEN", "veryPrivateToken");
            }
        }

        LandscapeDescription landscapeDescription = LandscapeDescriptionFactory.fromString(read, file.toString());
        assertNotNull(landscapeDescription);
        assertEquals("veryPrivateToken", landscapeDescription.getSourceReferences().get(0).getHeaderTokenValue());
    }

    @Test
    public void environmentTemplatesRead() {
        File file = new File(RootPath.get() + "/src/test/resources/example/example_templates.yml");
        LandscapeDescription landscapeDescription = LandscapeDescriptionFactory.fromYaml(file);
        assertNotNull(landscapeDescription);
        assertNotNull(landscapeDescription.getTemplates());
        assertEquals(2, landscapeDescription.getTemplates().size());

        LandscapeItem template = landscapeDescription.getTemplates().get("myfirsttemplate");
        assertNotNull(template);
        LandscapeItem groupTemplate = landscapeDescription.getTemplates().get("insamegroup");
        assertNotNull(groupTemplate);
    }

    @Test
    public void environmentTemplatesSanitized() {
        File file = new File(RootPath.get() + "/src/test/resources/example/example_templates.yml");
        LandscapeDescription landscapeDescription = LandscapeDescriptionFactory.fromYaml(file);

        ItemDescription template = landscapeDescription.getTemplates().get("myfirsttemplate");

        assertEquals("webservice", template.getLabel(Label.TYPE));
        assertTrue(template.getName().isEmpty());
    }

    @Test
    public void templatesAssigned() {
        File file = new File(RootPath.get() + "/src/test/resources/example/example_templates.yml");
        LandscapeDescription landscapeDescription = LandscapeDescriptionFactory.fromYaml(file);

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
        LandscapeDescription landscapeDescription = LandscapeDescriptionFactory.fromYaml(file);

        ItemDescription template = landscapeDescription.getTemplates().get("addDataFlow");
        RelationItem df = (RelationItem) template.getRelations().toArray()[0];
        assertEquals("identifier LIKE 'other_crappy_name%'", df.getTarget());
    }

    @Test
    public void configRead() {
        File file = new File(RootPath.get() + "/src/test/resources/example/example_config.yml");
        LandscapeDescription landscapeDescription = LandscapeDescriptionFactory.fromYaml(file);

        LandscapeConfig config = landscapeDescription.getConfig();

        assertNotNull(config);
        assertNotNull(config.getJgraphx());
        assertNotNull(config.getJgraphx().getForceConstantFactor());
    }

    @Test
    public void testReadUnkownFile() {
        File file = new File(RootPath.get() + "/src/test/resources/example/example_xxx.yml");


        try {
            LandscapeDescriptionFactory.fromYaml(file);
        } catch (ReadingException ex) {
            assertTrue(ex.getMessage().contains("Could not find file"));
            return;
        }

        fail("ReadingException was not thrown");
    }

    @Test
    public void testReadGroups() {
        File file = new File(RootPath.get() + "/src/test/resources/example/example_groups.yml");
        LandscapeDescription landscapeDescription = LandscapeDescriptionFactory.fromYaml(file);

        Map<String, GroupItem> groups = landscapeDescription.getGroups();
        assertNotNull(groups);
        assertEquals(2, groups.size());
        GroupItem groupItem = groups.get("groupA");
        assertNotNull(groupItem);
        assertEquals("groupA", groupItem.getIdentifier());

        GroupItem b = groups.get("B");
        assertNotNull(b);
        assertEquals("B", b.getIdentifier());
    }

    @Test
    public void readCustomKPIs() {
        File file = new File(RootPath.get() + "/src/test/resources/example/example_kpis.yml");
        LandscapeDescription landscapeDescription = LandscapeDescriptionFactory.fromYaml(file);
        LandscapeConfig config = landscapeDescription.getConfig();

        Map<String, KPI> kpIs = config.getKPIs();
        assertNotNull(kpIs);

        KPI health = kpIs.get(HealthKPI.IDENTIFIER);
        assertNotNull(health);
        assertEquals("can be overridden", health.getDescription());

        KPI monthlyCosts = kpIs.get("monthlyCosts");
        assertNotNull(monthlyCosts);
        assertEquals("Evaluates the monthly maintenance costs", monthlyCosts.getDescription());

        Item item = new Item();
        item.setLabel(Label.COSTS, "200");
        StatusValue statusValue =  monthlyCosts.getStatusValues(item).get(0);
        assertNotNull(statusValue);
        assertEquals(Status.RED, statusValue.getStatus());
    }
}