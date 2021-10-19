package de.bonndan.nivio.api;

import de.bonndan.nivio.input.IndexingDispatcher;
import de.bonndan.nivio.model.*;
import de.bonndan.nivio.output.dto.*;
import de.bonndan.nivio.search.ItemIndex;
import de.bonndan.nivio.util.FrontendMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ApiControllerTest {
    LinkFactory linkFactory;
    IndexingDispatcher indexingDispatcher;
    LandscapeRepository landscapeRepository;
    FrontendMapping frontendMapping;
    ApiController apiController;

    @BeforeEach
    void setUp() {
        linkFactory = Mockito.mock(LinkFactory.class);
        indexingDispatcher = Mockito.mock(IndexingDispatcher.class);
        landscapeRepository = Mockito.mock(LandscapeRepository.class);
        frontendMapping = Mockito.mock(FrontendMapping.class);
        apiController = new ApiController(landscapeRepository, linkFactory, indexingDispatcher, frontendMapping);
    }

    @Test
    void index() {
        var index = Mockito.mock(Index.class);
        Mockito.when(linkFactory.getIndex(landscapeRepository.findAll())).thenReturn(index);
        assertThat(apiController.index()).isEqualTo(index);
    }

    @Test
    void landscape() {
        var landscape = Mockito.mock(Landscape.class);
        Mockito.when(landscapeRepository.findDistinctByIdentifier("")).thenReturn(Optional.empty());
        assertThat(apiController.landscape("")).isEqualTo(ResponseEntity.notFound().build());
        Mockito.when(landscapeRepository.findDistinctByIdentifier("test")).thenReturn(Optional.of(landscape));
        assertThat(apiController.landscape("test").getClass()).isEqualTo(ResponseEntity.class);
        assertThat(apiController.landscape("test").getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(apiController.landscape("test").getBody()).isEqualToComparingFieldByField(new LandscapeApiModel(landscape));
    }

    @Test
    void group() {
        var landscape = Mockito.mock(Landscape.class);
        Mockito.when(landscapeRepository.findDistinctByIdentifier("")).thenReturn(Optional.empty());
        assertThat(apiController.group("", "")).isEqualTo(ResponseEntity.notFound().build());
        Mockito.when(landscapeRepository.findDistinctByIdentifier("test")).thenReturn(Optional.of(landscape));
        Mockito.when(landscape.getGroup("test")).thenReturn(Optional.of(new Group("test", "test")));
        Mockito.when(landscape.getItems()).thenReturn(new ItemIndex<>(Item.class));

        assertThat(apiController.group("test", "test").getClass()).isEqualTo(ResponseEntity.class);
        assertThat(apiController.group("test", "test").getStatusCode()).isEqualTo(HttpStatus.OK);
        Group test = landscape.getGroup("test").get();
        assertThat(apiController.group("test", "test").getBody()).isEqualToComparingFieldByField(new GroupApiModel(test, landscape.getItems().retrieve(test.getItems())));
    }

    @Test
    void item() {
        var landscape = Mockito.mock(Landscape.class);
        var item = Mockito.mock(Item.class);
        var itemIndex = new ItemIndex<>(Item.class);
        Mockito.when(item.getFullyQualifiedIdentifier()).thenReturn(FullyQualifiedIdentifier.build("test", "test", "test"));
        Mockito.when(landscapeRepository.findDistinctByIdentifier("")).thenReturn(Optional.empty());
        assertThat(apiController.item("test", "test", "test")).isEqualTo(ResponseEntity.notFound().build());
        Mockito.when(landscape.getItems()).thenReturn(itemIndex);
        assertThat(apiController.item("test", "test", "test")).isEqualTo(ResponseEntity.notFound().build());
        itemIndex.add(item);

        Mockito.when(landscapeRepository.findDistinctByIdentifier("test")).thenReturn(Optional.of(landscape));
        Mockito.when(landscape.getGroup("test")).thenReturn(Optional.of(new Group("test", "test")));
        assertThat(apiController.item("test", "test", "test").getClass()).isEqualTo(ResponseEntity.class);
        assertThat(apiController.item("test", "test", "test").getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(apiController.item("test", "test", "test").getBody()).isEqualToComparingFieldByField(new ItemApiModel(item, landscape.getGroup("test").get()));
    }


    @Test
    void search() {
        var landscape = Mockito.mock(Landscape.class);
        var item1 = Mockito.mock(Item.class);
        var item2 = Mockito.mock(Item.class);
        Mockito.when(item1.getGroup()).thenReturn("test");
        Mockito.when(item2.getGroup()).thenReturn("test");
        Mockito.when(landscapeRepository.findDistinctByIdentifier("")).thenReturn(Optional.empty());
        assertThat(apiController.search("", "test")).isEqualTo(ResponseEntity.notFound().build());
        Mockito.when(landscapeRepository.findDistinctByIdentifier("test")).thenReturn(Optional.of(landscape));
        Mockito.when(landscape.getGroups()).thenReturn(Map.of("test", new Group("test", "test")));
        Mockito.when(landscape.getGroup("test")).thenReturn(Optional.of(new Group("test", "test")));
        Mockito.when(landscape.search("test")).thenReturn(Set.of(item1, item2));
        assertThat(apiController.search("test", "test").getClass()).isEqualTo(ResponseEntity.class);
        assertThat(apiController.search("test", "test").getStatusCode()).isEqualTo(HttpStatus.OK);
        var hashSet = new HashSet<ItemApiModel>();
        assertThat(apiController.search("test", "test").getBody().size()).isEqualTo(2);
    }

    @Test
    void mapping() {
        Mockito.when(frontendMapping.getKeys()).thenReturn(Map.of("testKey", "testValue"));
        assertThat(apiController.mapping().getBody().getClass()).isEqualTo(MappingApiModel.class);
    }

    @Test
    void description() {
        Mockito.when(frontendMapping.getDescriptions()).thenReturn(Map.of("testKey", "testValue"));
        assertThat(apiController.description().getBody().getClass()).isEqualTo(DescriptionApiModel.class);
    }
}
