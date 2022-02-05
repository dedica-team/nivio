package de.bonndan.nivio.api;

import com.github.jknack.handlebars.internal.Files;
import de.bonndan.nivio.input.IndexingDispatcher;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.*;
import de.bonndan.nivio.output.dto.FrontendMappingApiModel;
import de.bonndan.nivio.output.dto.GroupApiModel;
import de.bonndan.nivio.output.dto.ItemApiModel;
import de.bonndan.nivio.output.dto.LandscapeApiModel;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    private Index<GraphComponent> index;

    @BeforeEach
    void setUp() {
        linkFactory = Mockito.mock(LinkFactory.class);
        indexingDispatcher = Mockito.mock(IndexingDispatcher.class);
        landscapeRepository = Mockito.mock(LandscapeRepository.class);
        frontendMapping = Mockito.mock(FrontendMapping.class);
        apiController = new ApiController(landscapeRepository, linkFactory, indexingDispatcher, frontendMapping);

        landscape = Mockito.mock(Landscape.class);
        apiRootModel = Mockito.mock(ApiRootModel.class);
        index = Mockito.mock(Index.class);
        when(landscape.getIndexReadAccess()).thenReturn(new IndexReadAccess<>(index));
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
        assertThat(apiController.group("", "")).isEqualTo(ResponseEntity.notFound().build());
        Mockito.when(landscapeRepository.findDistinctByIdentifier("test")).thenReturn(Optional.of(landscape));
        Mockito.when(landscape.getGroup("test")).thenReturn(Optional.of(GroupBuilder.aTestGroup("test").build()));

        assertThat(apiController.group("test", "test").getClass()).isEqualTo(ResponseEntity.class);
        assertThat(apiController.group("test", "test").getStatusCode()).isEqualTo(HttpStatus.OK);
        Group test = landscape.getGroup("test").get();
        assertThat(apiController.group("test", "test").getBody())
                .isEqualToComparingFieldByField(new GroupApiModel(test, test.getChildren()));
    }

    @Test
    void item() {
        var landscape = Mockito.mock(Landscape.class);
        var item = Mockito.mock(Item.class);
        Mockito.when(item.getFullyQualifiedIdentifier()).thenReturn(FullyQualifiedIdentifier.build(Item.class, "test", "test", "test", null));
        Mockito.when(landscapeRepository.findDistinctByIdentifier("")).thenReturn(Optional.empty());
        assertThat(apiController.item("test", "test", "test")).isEqualTo(ResponseEntity.notFound().build());
        assertThat(apiController.item("test", "test", "test")).isEqualTo(ResponseEntity.notFound().build());

        Mockito.when(landscapeRepository.findDistinctByIdentifier("test")).thenReturn(Optional.of(landscape));
        Mockito.when(landscape.getGroup("test")).thenReturn(Optional.of(GroupBuilder.aTestGroup("test").build()));
        assertThat(apiController.item("test", "test", "test").getClass()).isEqualTo(ResponseEntity.class);
        assertThat(apiController.item("test", "test", "test").getStatusCode()).isEqualTo(HttpStatus.OK);
        ItemApiModel test = new ItemApiModel(item);
        assertThat(apiController.item("test", "test", "test").getBody())
                .isEqualToComparingFieldByField(test);
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
        var landscape = Mockito.mock(Landscape.class);
        var item1 = Mockito.mock(GraphComponent.class);
        var item2 = Mockito.mock(GraphComponent.class);
        Mockito.when(landscapeRepository.findDistinctByIdentifier("")).thenReturn(Optional.empty());
        assertThat(apiController.search("", "test")).isEqualTo(ResponseEntity.notFound().build());
        Mockito.when(landscapeRepository.findDistinctByIdentifier("test")).thenReturn(Optional.of(landscape));
        Group test = GroupBuilder.aTestGroup("test").build();
        Mockito.when(landscape.getGroups()).thenReturn(Map.of(test.getFullyQualifiedIdentifier(), test));
        Mockito.when(landscape.getGroup("test")).thenReturn(Optional.of(test));
        Mockito.when(index.search("test")).thenReturn(List.of(item1, item2));
        assertThat(apiController.search("test", "test").getClass()).isEqualTo(ResponseEntity.class);
        assertThat(apiController.search("test", "test").getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(apiController.search("test", "test").getBody()).hasSize(2);
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
