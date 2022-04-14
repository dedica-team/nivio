package de.bonndan.nivio.api;

import com.github.jknack.handlebars.internal.Files;
import de.bonndan.nivio.GraphTestSupport;
import de.bonndan.nivio.assessment.Assessment;
import de.bonndan.nivio.input.IndexingDispatcher;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.*;
import de.bonndan.nivio.output.dto.FrontendMappingApiModel;
import de.bonndan.nivio.output.dto.GroupApiModel;
import de.bonndan.nivio.output.dto.ItemApiModel;
import de.bonndan.nivio.output.dto.LandscapeApiModel;
import de.bonndan.nivio.search.LuceneSearchIndex;
import de.bonndan.nivio.util.FrontendMapping;
import de.bonndan.nivio.util.RootPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ApiControllerTest {
    LinkFactory linkFactory;
    IndexingDispatcher indexingDispatcher;
    LandscapeRepository landscapeRepository;
    FrontendMapping frontendMapping;
    ApiController apiController;
    private Landscape landscape;
    private ApiRootModel apiRootModel;
    private GraphTestSupport graph;

    @BeforeEach
    void setUp() {
        linkFactory = Mockito.mock(LinkFactory.class);
        indexingDispatcher = Mockito.mock(IndexingDispatcher.class);
        landscapeRepository = Mockito.mock(LandscapeRepository.class);
        frontendMapping = Mockito.mock(FrontendMapping.class);
        apiController = new ApiController(landscapeRepository, linkFactory, indexingDispatcher, frontendMapping);

        graph = new GraphTestSupport();
        landscape = graph.landscape;
        apiRootModel = Mockito.mock(ApiRootModel.class);
    }

    @Test
    void index() {
        Mockito.when(linkFactory.getIndex(landscapeRepository.findAll())).thenReturn(apiRootModel);
        assertThat(apiController.index()).isEqualTo(apiRootModel);
    }

    @Test
    void landscape() {
        Mockito.when(landscapeRepository.findDistinctByIdentifier("")).thenReturn(Optional.empty());
        assertThat(apiController.landscape("")).isEqualTo(ResponseEntity.notFound().build());
        Mockito.when(landscapeRepository.findDistinctByIdentifier("test")).thenReturn(Optional.of(landscape));
        assertThat(apiController.landscape("test").getClass()).isEqualTo(ResponseEntity.class);
        assertThat(apiController.landscape("test").getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(apiController.landscape("test").getBody()).isEqualToComparingFieldByField(new LandscapeApiModel(landscape));
    }

    @Test
    void group() {
        Mockito.when(landscapeRepository.findDistinctByIdentifier("")).thenReturn(Optional.empty());

        //when
        ResponseEntity<GroupApiModel> group = apiController.group("", "");
        assertThat(group).isEqualTo(ResponseEntity.notFound().build());

        Mockito.when(landscapeRepository.findDistinctByIdentifier(landscape.getIdentifier())).thenReturn(Optional.of(landscape));


        ResponseEntity<GroupApiModel> group1 = apiController.group(landscape.getIdentifier(), graph.groupA.getIdentifier());
        assertThat(group1.getClass()).isEqualTo(ResponseEntity.class);
        assertThat(group1.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(group1.getBody()).isInstanceOf(GroupApiModel.class);
        assertThat(group1.getBody().getFullyQualifiedIdentifier()).isEqualTo(graph.groupA.getFullyQualifiedIdentifier());
    }

    @Test
    void item() {
        Mockito.when(landscapeRepository.findDistinctByIdentifier(landscape.getIdentifier())).thenReturn(Optional.of(landscape));

        ResponseEntity<ItemApiModel> item = apiController.item(landscape.getIdentifier(), "test", "test");
        assertThat(item).isEqualTo(ResponseEntity.notFound().build());

        item = apiController.item(landscape.getIdentifier(), graph.itemAA.getParentIdentifier(), graph.itemAA.getIdentifier());
        assertThat(item.getClass()).isEqualTo(ResponseEntity.class);
        assertThat(item.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(item.getBody()).isNotNull();
    }

    @Test
    @DisplayName("Create endpoint creates new landscape (non-partial)")
    void create() throws IOException {

        //given
        File file = new File(RootPath.get() + "/src/test/resources/example/example_env.yml");
        String body = Files.read(file, Charset.defaultCharset());
        when(indexingDispatcher.createLandscapeDescriptionFromBody(any())).thenReturn(new LandscapeDescription("foo"));
        when(linkFactory.generateComponentLink(any())).thenReturn(Optional.of(new Link(new URL("http://foo.bar.com"))));

        //when
        ResponseEntity<Object> objectResponseEntity = apiController.create(body);

        //then
        assertThat(objectResponseEntity).isNotNull();
        assertThat(objectResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        verify(indexingDispatcher).createLandscapeDescriptionFromBody(any());
    }

    @Test
    @DisplayName("Update endpoint updates a landscape (partial)")
    void update() throws IOException {

        //given
        File file = new File(RootPath.get() + "/src/test/resources/example/example_env.yml");
        String body = Files.read(file, Charset.defaultCharset());
        when(indexingDispatcher.updateLandscapeDescriptionFromBody(any(), eq("foo"))).thenReturn(new LandscapeDescription("foo"));
        when(linkFactory.generateComponentLink(any())).thenReturn(Optional.of(new Link(new URL("http://foo.bar.com"))));

        //when
        ResponseEntity<Object> objectResponseEntity = apiController.update("foo", body);

        //then
        assertThat(objectResponseEntity).isNotNull();
        assertThat(objectResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        verify(indexingDispatcher).updateLandscapeDescriptionFromBody(any(), eq("foo"));
    }

    @Test
    void search() {

        graph = new GraphTestSupport(new Index<>(LuceneSearchIndex.createVolatile()));
        landscape = graph.landscape;
        landscape.getReadAccess().indexForSearch(Assessment.empty());

        Mockito.when(landscapeRepository.findDistinctByIdentifier("")).thenReturn(Optional.empty());
        Mockito.when(landscapeRepository.findDistinctByIdentifier("test")).thenReturn(Optional.of(landscape));

        //when
        ResponseEntity<Set<ItemApiModel>> test = apiController.search("", "test");
        assertThat(test).isEqualTo(ResponseEntity.notFound().build());

        ResponseEntity<Set<ItemApiModel>> search = apiController.search(graph.landscape.getIdentifier(), "identifier:" + graph.itemAA.getIdentifier());
        assertThat(search.getClass()).isEqualTo(ResponseEntity.class);
        assertThat(search.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(search.getBody()).hasSize(1);
    }

    @Test
    void mapping() {

        //given
        Mockito.when(frontendMapping.getKeys()).thenReturn(Map.of("testKey", "testValue"));
        Mockito.when(frontendMapping.getDescriptions()).thenReturn(Map.of("testKey", "description"));

        //when
        FrontendMappingApiModel body = apiController.mapping().getBody();

        //then
        assertThat(body).isNotNull();
        assertThat(body.getKeys()).isEqualTo(Map.of("testKey", "testValue"));
        assertThat(body.getDescriptions()).isEqualTo(Map.of("testKey", "description"));
    }
}
