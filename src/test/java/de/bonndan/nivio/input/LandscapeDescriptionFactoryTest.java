package de.bonndan.nivio.input;


import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.model.*;
import de.bonndan.nivio.util.RootPath;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class LandscapeDescriptionFactoryTest {
    final String SEPARATOR = FileSystems.getDefault().getSeparator();
    final String FILE_PATH = RootPath.get() + SEPARATOR + "src" + SEPARATOR + "test" + SEPARATOR + "resources" + SEPARATOR + "example" + SEPARATOR;
    final String FILE_PATH_ENV = FILE_PATH + "example_env.yml";
    final String FILE_PATH_INCREMENTAL_ENV = FILE_PATH + "example_incremental_env.yml";
    final String FILE_PATH_ENVIRONMENT_VARS = FILE_PATH + "example_environment_vars.yml";
    final String FILE_PATH_TEMPLATES = FILE_PATH + "example_templates.yml";
    final String FILE_PATH_TEMPLATES_2 = FILE_PATH + "example_templates2.yml";
    final String FILE_PATH_CONFIG = FILE_PATH + "example_config.yml";
    final String FILE_PATH_UNKNOWN = FILE_PATH + "example_xxx.yml";
    final String FILE_PATH_GROUPS = FILE_PATH + "example_groups.yml";

    @Test
    public void read() {
        File file = new File(FILE_PATH_ENV);
        LandscapeDescription landscapeDescription = LandscapeDescriptionFactory.fromYaml(file);
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
    public void readYamlStr() throws IOException {

        File file = new File(FILE_PATH_ENV);
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

        File file = new File(FILE_PATH_ENV);
        String yaml = new String(Files.readAllBytes(file.toPath()));
        LandscapeDescription landscapeDescription = LandscapeDescriptionFactory.fromString(yaml, file.toURI().toURL());
        assertEquals(file.toURI().toURL().toString(), landscapeDescription.getSource());
    }

    @Test
    public void readIncremental() {

        File file = new File(FILE_PATH_INCREMENTAL_ENV);
        LandscapeDescription landscapeDescription = LandscapeDescriptionFactory.fromYaml(file);
        assertEquals("Incremental update example", landscapeDescription.getName());
        assertEquals("nivio:incremental", landscapeDescription.getIdentifier());
        assertFalse(landscapeDescription.getSourceReferences().isEmpty());
    }

    @Test
    public void readEnvVars() throws IOException, NoSuchFieldException, IllegalAccessException {

        File file = new File(FILE_PATH_ENVIRONMENT_VARS);
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
        /* TODO: x.put doesn't override PRIVATE_TOKEN to veryPrivateToken but it pulls the correct value from our example file (${PRIVATE_TOKEN})*/
        assertEquals("veryPrivateToken", landscapeDescription.getSourceReferences().get(0).getHeaderTokenValue());
    }

    @Test
    public void environmentTemplatesRead() {
        File file = new File(FILE_PATH_TEMPLATES);
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
        File file = new File(FILE_PATH_TEMPLATES);
        LandscapeDescription landscapeDescription = LandscapeDescriptionFactory.fromYaml(file);

        LandscapeItem template = landscapeDescription.getTemplates().get("myfirsttemplate");

        assertEquals("webservice", template.getType());
        assertTrue(template.getName().isEmpty());
        assertTrue(template.getShortName().isEmpty());
    }

    @Test
    public void templatesAssigned() {
        File file = new File(FILE_PATH_TEMPLATES);
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
        File file = new File(FILE_PATH_TEMPLATES_2);
        LandscapeDescription landscapeDescription = LandscapeDescriptionFactory.fromYaml(file);

        ItemDescription template = landscapeDescription.getTemplates().get("addDataFlow");
        RelationItem df = (RelationItem) template.getRelations().toArray()[0];
        assertEquals("identifier LIKE 'other_crappy_name%'", df.getTarget());
    }

    @Test
    public void configRead() {
        File file = new File(FILE_PATH_CONFIG);
        LandscapeDescription landscapeDescription = LandscapeDescriptionFactory.fromYaml(file);

        LandscapeConfig config = landscapeDescription.getConfig();

        assertNotNull(config);
        assertNotNull(config.getJgraphx());
        assertNotNull(config.getJgraphx().getForceConstantFactor());
    }

    @Test
    public void testReadUnknownFile() {
        File file = new File(FILE_PATH_UNKNOWN);


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
        File file = new File(FILE_PATH_GROUPS);
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
}