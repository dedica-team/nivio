package de.bonndan.nivio.input.customjson;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.bonndan.nivio.IntegrationTestSupport;
import de.bonndan.nivio.input.ProcessingException;
import de.bonndan.nivio.input.SeedConfigurationFactory;
import de.bonndan.nivio.input.SourceReference;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.search.ItemIndex;
import de.bonndan.nivio.util.RootPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class InputFormatHandlerCustomJSONTest {

    private SeedConfigurationFactory factory;
    private InputFormatHandlerCustomJSON handler;
    private File file;
    private LandscapeDescription defaultLandscapeDTO;

    @BeforeEach
    public void setup() {
        IntegrationTestSupport integrationTestSupport = new IntegrationTestSupport();
        factory = integrationTestSupport.getSeedConfigurationFactory();
        handler = new InputFormatHandlerCustomJSON(integrationTestSupport.getFileFetcher(), new ObjectMapper());
        file = new File(RootPath.get() + "/src/test/resources/example/example_json.yml");
        defaultLandscapeDTO = new LandscapeDescription("test");
    }

    @Test
    void readMappedFields() {

        //given
        SourceReference json = getSourceRef(file);

        //when
        List<LandscapeDescription> landscapeDescriptions = handler.applyData(json, defaultLandscapeDTO);

        //then
        assertThat(defaultLandscapeDTO.getItemDescriptions().all()).isEmpty();
        assertThat(landscapeDescriptions).hasSize(2);

        LandscapeDescription other = landscapeDescriptions.get(1);
        assertThat(other.getItemDescriptions().all()).isNotEmpty();
        ItemDescription asd = other.getItemDescriptions().findOneBy("asd", null);
        assertThat(asd).isNotNull();
        assertThat(asd.getIdentifier()).isEqualTo("asd");
        assertThat(asd.getLabel("endoflife")).isEqualTo("2022-12-31T00:00:00+01:00");
        assertThat(asd.getLabel("nivio.link.homepage")).isEqualTo("https://foo.bar.com");
    }

    @Test
    void assignsFieldsWithSameName() {

        //given
        SourceReference json = getSourceRef(file);

        //when
        List<LandscapeDescription> landscapeDescriptions = handler.applyData(json, defaultLandscapeDTO);

        //then
        ItemIndex<ItemDescription> itemDescriptions = landscapeDescriptions.get(1).getItemDescriptions();
        Set<ItemDescription> items = itemDescriptions.all();
        assertThat(items).isNotEmpty();
        ItemDescription asd = itemDescriptions.findOneBy("asd", null);
        assertThat(asd).isNotNull();
        assertThat(asd.getContact()).isEqualTo("John Doe");
    }

    @Test
    void handlesPiping() {

        //given
        SourceReference json = getSourceRef(file);

        //when
        List<LandscapeDescription> landscapeDescriptions = handler.applyData(json, defaultLandscapeDTO);

        //then
        ItemIndex<ItemDescription> itemDescriptions = landscapeDescriptions.get(1).getItemDescriptions();
        ItemDescription asd = itemDescriptions.findOneBy("asd", null);
        assertThat(asd).isNotNull();
        assertThat(asd.getLabel("nivio.relations.upstream")).isEqualTo("foo,bar");
    }


    @Test
    void handlesPipingWithBrokenUrls() {
        //given
        File file = new File(RootPath.get() + "/src/test/resources/example/example_json_broken2.yml");
        SourceReference json = getSourceRef(file);

        //when
        handler.applyData(json, defaultLandscapeDTO);

        //then
        Set<ItemDescription> items = defaultLandscapeDTO.getItemDescriptions().all();
        assertThat(items).isNotEmpty();
        ItemDescription asd = defaultLandscapeDTO.getItemDescriptions().findOneBy("asd", null);
        assertThat(asd).isNotNull();
    }

    @Test
    void handlesBrokenPaths() {
        //given
        File file = new File(RootPath.get() + "/src/test/resources/example/example_json_broken1.yml");
        SourceReference json = getSourceRef(file);

        //when
        assertThatExceptionOfType(ProcessingException.class).isThrownBy(() -> handler.applyData(json, defaultLandscapeDTO));
    }

    @Test
    void withoutMapping() {
        //given
        File file = new File(RootPath.get() + "/src/test/resources/example/example_json_bare.yml");
        SourceReference json = getSourceRef(file);

        //when
        assertThatNoException().isThrownBy(() -> handler.applyData(json, defaultLandscapeDTO));

        //then
        assertThat(defaultLandscapeDTO.getItemDescriptions().all()).isEmpty();
    }

    private SourceReference getSourceRef(File file) {
        var seedConfiguration = factory.fromFile(file);
        return seedConfiguration.getSourceReferences().get(0);
    }
}
