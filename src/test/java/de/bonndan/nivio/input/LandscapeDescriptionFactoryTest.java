package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.LandscapeDescriptionFactory;
import de.bonndan.nivio.util.RootPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


class LandscapeDescriptionFactoryTest {

    final private String SEPARATOR = FileSystems.getDefault().getSeparator();
    final private String FILE_PATH = RootPath.get() + SEPARATOR + "src" + SEPARATOR + "test" + SEPARATOR + "resources" + SEPARATOR + "example" + SEPARATOR;
    final private String FILE_PATH_ENV = FILE_PATH + "example_env.yml";
    final private String FILE_PATH_TEMPLATES = FILE_PATH + "example_templates.yml";

    private LandscapeDescriptionFactory factory;

    @BeforeEach
    public void setup() {
        factory = new LandscapeDescriptionFactory();
    }

    @Test
    void readsMinimalWithIdentifier() {
        assertDoesNotThrow(() -> new LandscapeDescriptionFactory().fromString("yaml", ""));
    }

    @Test
    void readFails() {
        assertThrows(ReadingException.class, () -> new LandscapeDescriptionFactory().fromString("", ""));
    }

    @Test
    void readYamlStr() throws IOException {
        File file = new File(FILE_PATH_ENV);
        String yaml = new String(Files.readAllBytes(file.toPath()));

        //when
        LandscapeDescription landscapeDescription = factory.fromString(yaml, file.toString());

        //then
        assertEquals("Landscape example", landscapeDescription.getName());
        assertEquals("nivio:example", landscapeDescription.getIdentifier());
        assertEquals("mail@acme.org", landscapeDescription.getContact());
        assertTrue(landscapeDescription.getDescription().contains("demonstrate"));
        assertTrue(landscapeDescription.getSource().getStaticSource().contains("name: Landscape example"));
    }

    @Test
    void readYamlStrWithUrlSource() throws IOException {

        File file = new File(FILE_PATH_ENV);
        String yaml = new String(Files.readAllBytes(file.toPath()));
        LandscapeDescription landscapeDescription = factory.fromString(yaml, file.toURI().toURL());
        assertEquals(file.toURI().toURL().toString(), landscapeDescription.getSource().getURL().get().toString());
    }

    @Test
     void fromBodyItem() {

        //when
        LandscapeDescription landscapeDescription = factory.fromBodyItems("foo", "nivio", "body");

        assertNotNull(landscapeDescription);
        assertThat(landscapeDescription.getIdentifier()).isEqualTo("foo");
    }
}
