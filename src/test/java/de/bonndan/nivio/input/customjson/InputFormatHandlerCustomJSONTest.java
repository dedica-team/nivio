package de.bonndan.nivio.input.customjson;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.bonndan.nivio.input.FileFetcher;
import de.bonndan.nivio.input.LandscapeDescriptionFactory;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.input.http.HttpService;
import de.bonndan.nivio.util.RootPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.MalformedURLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class InputFormatHandlerCustomJSONTest {

    private LandscapeDescriptionFactory factory;
    private InputFormatHandlerCustomJSON handler;
    private File file;
    private LandscapeDescription landscapeDescription;

    @BeforeEach
    public void setup() {
        FileFetcher fileFetcher = new FileFetcher(mock(HttpService.class));
        factory = new LandscapeDescriptionFactory(fileFetcher);
        handler = new InputFormatHandlerCustomJSON(fileFetcher, new ObjectMapper());
        file = new File(RootPath.get() + "/src/test/resources/example/example_json.yml");
        landscapeDescription = factory.fromYaml(file);
    }

    @Test
    void readMappedFields() throws MalformedURLException {

        //given
        SourceReference json = landscapeDescription.getSourceReferences().get(0);

        //when
        handler.applyData(json, file.getParentFile().toURI().toURL(), landscapeDescription);

        //then
        assertThat(landscapeDescription.getItemDescriptions().all()).isNotEmpty();
        ItemDescription asd = landscapeDescription.getItemDescriptions().findOneBy("asd", null);
        assertThat(asd).isNotNull();
        assertThat(asd.getIdentifier()).isEqualTo("asd");
        assertThat(asd.getLabel("endoflife")).isEqualTo("2022-12-31T00:00:00+01:00");
        assertThat(asd.getLabel("nivio.link.homepage")).isEqualTo("https://foo.bar.com");
    }

    @Test
    void assignsFieldsWithSameName() throws MalformedURLException {

        //given
        File file = new File(RootPath.get() + "/src/test/resources/example/example_json.yml");
        LandscapeDescription landscapeDescription = factory.fromYaml(file);
        SourceReference json = landscapeDescription.getSourceReferences().get(0);

        //when
        handler.applyData(json, file.getParentFile().toURI().toURL(), landscapeDescription);

        //then
        assertThat(landscapeDescription.getItemDescriptions().all()).isNotEmpty();
        ItemDescription asd = landscapeDescription.getItemDescriptions().findOneBy("asd", null);
        assertThat(asd).isNotNull();
        assertThat(asd.getContact()).isEqualTo("John Doe");
    }

    @Test
    void handlesPiping() throws MalformedURLException {

        //given
        File file = new File(RootPath.get() + "/src/test/resources/example/example_json.yml");
        LandscapeDescription landscapeDescription = factory.fromYaml(file);
        SourceReference json = landscapeDescription.getSourceReferences().get(0);

        //when
        handler.applyData(json, file.getParentFile().toURI().toURL(), landscapeDescription);

        //then
        assertThat(landscapeDescription.getItemDescriptions().all()).isNotEmpty();
        ItemDescription asd = landscapeDescription.getItemDescriptions().findOneBy("asd", null);
        assertThat(asd).isNotNull();
        assertThat(asd.getLabel("nivio.relations.upstream")).isEqualTo("foo,bar");
    }
}