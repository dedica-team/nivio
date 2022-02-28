package de.bonndan.nivio.input;

import de.bonndan.nivio.assessment.Assessment;
import de.bonndan.nivio.input.dto.ContextDescription;
import de.bonndan.nivio.input.dto.GroupDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.LandscapeDescriptionFactory;
import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import de.bonndan.nivio.util.RootPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;


class LandscapeDescriptionFactoryTest {

    final private String SEPARATOR = FileSystems.getDefault().getSeparator();
    final private String FILE_PATH = RootPath.get() + SEPARATOR + "src" + SEPARATOR + "test" + SEPARATOR + "resources" + SEPARATOR + "example" + SEPARATOR;

    private LandscapeDescriptionFactory factory;

    @BeforeEach
    public void setup() {
        factory = new LandscapeDescriptionFactory();
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

    @Test
    void refreshedCopy() {

        //given
        LandscapeDescription in = new LandscapeDescription("test");
        in.setIsPartial(true);

        ContextDescription context = new ContextDescription();
        context.setIdentifier("aContext");
        in.getWriteAccess().addOrReplaceChild(context);

        GroupDescription group = new GroupDescription("a");
        group.setName("foo");
        in.getWriteAccess().addOrReplaceChild(group);

        in.getReadAccess().indexForSearch(Assessment.empty());
        group.setContext(context.getIdentifier()); //now the index is stale, node fqi in index is not correct

        var shouldBeKey = FullyQualifiedIdentifier.forDescription(GroupDescription.class, null, null, group.getParentIdentifier(), group.getIdentifier(), null, null);
        assertThat(in.getReadAccess().get(shouldBeKey)).isEmpty();

        //when
        var out = LandscapeDescriptionFactory.refreshedCopyOf(in);

        //then
        assertThat(out)
                .isNotSameAs(in)
                .isEqualTo(in);
        assertThat(out.getConfig()).isEqualTo(in.getConfig());
        assertThat(out.getTemplates()).isEqualTo(in.getTemplates());
        assertThat(out.getLinks()).isEqualTo(in.getLinks());
        assertThat(out.getName()).isEmpty(); //in had null
        assertThat(out.getDescription()).isEqualTo(in.getDescription());
        assertThat(out.isPartial()).isTrue();

        assertThat(out.getReadAccess().get(shouldBeKey)).isPresent();
    }

}
