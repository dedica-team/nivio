package de.bonndan.nivio.api;

import de.bonndan.nivio.input.IndexingDispatcher;
import de.bonndan.nivio.input.ProcessLog;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.*;
import de.bonndan.nivio.output.dto.FrontendMappingApiModel;
import de.bonndan.nivio.output.dto.GroupApiModel;
import de.bonndan.nivio.output.dto.ItemApiModel;
import de.bonndan.nivio.output.dto.LandscapeApiModel;
import de.bonndan.nivio.util.FrontendMapping;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.lucene.facet.FacetResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = ApiController.PATH)
public class ApiController {

    public static final String PATH = "/api";
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiController.class);
    private final LandscapeRepository landscapeRepository;
    private final LinkFactory linkFactory;
    private final IndexingDispatcher indexingDispatcher;
    private final FrontendMapping frontendMapping;

    public ApiController(LandscapeRepository landscapeRepository,
                         LinkFactory linkFactory,
                         IndexingDispatcher indexingDispatcher,
                         FrontendMapping frontendMapping) {

        this.landscapeRepository = landscapeRepository;
        this.linkFactory = linkFactory;
        this.indexingDispatcher = indexingDispatcher;
        this.frontendMapping = frontendMapping;
    }

    @Operation(summary = "Overview on all landscape and global configuration")
    @GetMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiRootModel index() {
        return linkFactory.getIndex(landscapeRepository.findAll());
    }


    @Operation(summary = "This resource serves a landscape and can be addressed by using a fully qualified identifier")
    @GetMapping(path = "/{landscapeIdentifier}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LandscapeApiModel> landscape(@PathVariable String landscapeIdentifier) {
        Landscape landscape = landscapeRepository.findDistinctByIdentifier(landscapeIdentifier).orElse(null);
        if (landscape == null) {
            return ResponseEntity.notFound().build();
        }

        LandscapeApiModel landscapeApiModel = new LandscapeApiModel(landscape);
        linkFactory.setLandscapeLinksRecursive(landscapeApiModel);
        return new ResponseEntity<>(landscapeApiModel, HttpStatus.OK);
    }

    @Operation(summary = "This resource serves a group in a landscape and can be addressed by using a fully qualified identifier")
    @GetMapping(path = "/{landscapeIdentifier}/{groupIdentifier}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GroupApiModel> group(@PathVariable String landscapeIdentifier,
                                               @PathVariable String groupIdentifier
    ) {
        Landscape landscape = landscapeRepository.findDistinctByIdentifier(landscapeIdentifier).orElse(null);
        if (landscape == null) {
            return ResponseEntity.notFound().build();
        }

        Optional<Group> group = landscape.getGroup(groupIdentifier);
        if (group.isPresent()) {
            Group group1 = group.get();
            GroupApiModel groupItem = new GroupApiModel(group1, Set.copyOf(group1.getChildren()));
            linkFactory.setGroupLinksRecursive(groupItem);
            return new ResponseEntity<>(groupItem, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "This resource serves an item in a landscape and can be addressed by using a fully qualified identifier")
    @GetMapping(path = "/{landscapeIdentifier}/{groupIdentifier}/{itemIdentifier}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemApiModel> item(@PathVariable String landscapeIdentifier,
                                             @PathVariable String groupIdentifier,
                                             @PathVariable String itemIdentifier
    ) {
        Landscape landscape = landscapeRepository.findDistinctByIdentifier(landscapeIdentifier).orElse(null);
        if (landscape == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            Item item = landscape.getIndexReadAccess().matchOneByIdentifiers(itemIdentifier, groupIdentifier, Item.class).orElseThrow();
            ItemApiModel apiModel = new ItemApiModel(item);
            linkFactory.setItemSelfLink(apiModel);
            return new ResponseEntity<>(apiModel, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Creates or replaces a landscape")
    @PostMapping(path = "/landscape")
    public ResponseEntity<Object> create(@RequestBody String body) {
        LandscapeDescription env = indexingDispatcher.createLandscapeDescriptionFromBody(body);
        Optional<URI> uriForDTO = getURIForDTO(env.getFullyQualifiedIdentifier());
        return uriForDTO
                .map(uri -> ResponseEntity.created(uri).build())
                .orElseGet(() -> ResponseEntity.unprocessableEntity().build());
    }

    @Operation(summary = "Updates a landscape. Values are merged into the current data")
    @PutMapping(path = "/landscape/{identifier}")
    public ResponseEntity<Object> update(@PathVariable String identifier, @RequestBody String body) {
        try {
            LandscapeDescription env = indexingDispatcher.updateLandscapeDescriptionFromBody(body, identifier);
            Optional<URI> uriForDTO = getURIForDTO(env.getFullyQualifiedIdentifier());
            return uriForDTO
                    .map(uri -> ResponseEntity.created(uri).build())
                    .orElseGet(() -> ResponseEntity.unprocessableEntity().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Add items to a landscape")
    @PostMapping(path = "/landscape/{identifier}/items")
    public ResponseEntity<Object> addItems(
            @PathVariable String identifier,
            @RequestHeader(name = "format") String format,
            @RequestBody String body
    ) {
        LandscapeDescription dto = indexingDispatcher.createFromLandscapeDescriptionBodyItems(identifier, body);
        Optional<URI> uriForDTO = getURIForDTO(dto.getFullyQualifiedIdentifier());
        return uriForDTO
                .map(uri -> ResponseEntity.created(uri).build())
                .orElseGet(() -> ResponseEntity.unprocessableEntity().build());
    }

    @Operation(summary = "Returns the last processing log")
    @GetMapping(path = "/landscape/{identifier}/log")
    public ResponseEntity<ProcessLog> log(@PathVariable String identifier) {

        Landscape landscape = landscapeRepository.findDistinctByIdentifier(identifier).orElse(null);
        if (landscape == null) {
            return ResponseEntity.notFound().build();
        }

        return new ResponseEntity<>(landscape.getLog(), HttpStatus.OK);
    }

    @Operation(summary = "Returns search results for the given lucene query")
    @GetMapping(path = "/landscape/{identifier}/search/{query}", produces = "application/json")
    public ResponseEntity<Set<ItemApiModel>> search(@PathVariable String identifier, @PathVariable String query) {

        Landscape landscape = landscapeRepository.findDistinctByIdentifier(identifier).orElse(null);
        if (landscape == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            Set<ItemApiModel> results = landscape.getIndexReadAccess().search(query, Item.class).stream()
                    .map(ItemApiModel::new)
                    .collect(Collectors.toSet());
            return new ResponseEntity<>(results, HttpStatus.OK);
        } catch (RuntimeException error) {
            LOGGER.error("Search query '{}' in landscape {} failed: {}", query, landscape, error.getMessage(), error);
            return ResponseEntity.badRequest().build();
        }

    }

    @Operation(summary = "Returns all search facets for the landscape")
    @GetMapping(path = "/landscape/{identifier}/facets", produces = "application/json")
    public ResponseEntity<List<FacetResult>> facets(@PathVariable String identifier) {

        Landscape landscape = landscapeRepository.findDistinctByIdentifier(identifier).orElse(null);
        if (landscape == null) {
            return ResponseEntity.notFound().build();
        }

        return new ResponseEntity<>(landscape.getIndexReadAccess().getFacets(), HttpStatus.OK);
    }

    @Operation(summary = "Returns the mapping of internally used terms to terms to be displayed")
    @GetMapping(path = "/mapping", produces = "application/json")
    public ResponseEntity<FrontendMappingApiModel> mapping() {
        return new ResponseEntity<>(
                new FrontendMappingApiModel(frontendMapping.getKeys(), frontendMapping.getDescriptions()),
                HttpStatus.OK
        );
    }

    @Operation(summary = "Triggers reindexing of a landscape.")
    @PostMapping(path = "/reindex/{landscape}")
    public ResponseEntity<Object> reindex(@PathVariable String landscape) {
        Landscape existing = landscapeRepository.findDistinctByIdentifier(landscape).orElse(null);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        indexingDispatcher.fromExistingLandscape(existing);
        Optional<URI> uriForDTO = getURIForDTO(existing.getFullyQualifiedIdentifier());
        return uriForDTO
                .map(uri -> ResponseEntity.created(uri).build())
                .orElseGet(() -> ResponseEntity.unprocessableEntity().build());
    }


    private Optional<URI> getURIForDTO(URI fullyQualifiedIdentifier) {
        Optional<Link> link = linkFactory.generateComponentLink(fullyQualifiedIdentifier);
        if (link.isEmpty()) {
            return Optional.empty();
        }

        URL href = link.get().getHref();
        if (href == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(href.toURI());
        } catch (URISyntaxException e) {
            return Optional.empty();
        }
    }
}
