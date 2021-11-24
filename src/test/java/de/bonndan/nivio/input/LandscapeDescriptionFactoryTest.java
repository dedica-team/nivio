package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.LandscapeDescriptionFactory;
import de.bonndan.nivio.util.RootPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;


class LandscapeDescriptionFactoryTest {

    final private String SEPARATOR = FileSystems.getDefault().getSeparator();
    final private String FILE_PATH = RootPath.get() + SEPARATOR + "src" + SEPARATOR + "test" + SEPARATOR + "resources" + SEPARATOR + "example" + SEPARATOR;

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
        assertThrows(ReadingException.class, () -> factory.fromString("", ""));
    }

    @Test
     void fromBodyItem() {

        //when
        LandscapeDescription landscapeDescription = factory.fromBodyItems("foo", "body");

        assertNotNull(landscapeDescription);
        assertThat(landscapeDescription.getIdentifier()).isEqualTo("foo");
    }

    @Test
    void addLogoLandscape() throws IOException {
        // given
        File file = new File(FILE_PATH + "inout.yml");
        String yaml = new String(Files.readAllBytes(file.toPath()));

        // when
        LandscapeDescription landscapeDescription = factory.fromString(yaml, file.toString());

        // then
        assertThat(landscapeDescription.getIcon()).isEqualTo("https://dedica.team/images/logo_orange_weiss.png");
    }

}
