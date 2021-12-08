package de.bonndan.nivio.api;

import de.bonndan.nivio.input.IndexingDispatcher;
import de.bonndan.nivio.input.ProcessLog;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.*;
import de.bonndan.nivio.output.dto.GroupApiModel;
import de.bonndan.nivio.output.dto.ItemApiModel;
import de.bonndan.nivio.output.dto.LandscapeApiModel;
import de.bonndan.nivio.util.FrontendMapping;
import org.apache.lucene.facet.FacetResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;
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
                         FrontendMapping frontendMapping
    ) {
        this.landscapeRepository = landscapeRepository;
        this.linkFactory = linkFactory;
        this.indexingDispatcher = indexingDispatcher;
        this.frontendMapping = frontendMapping;
    }

    /**
     * Overview on all landscapes.
     */
    @CrossOrigin(methods = RequestMethod.GET)
    @GetMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public Index index() {
        return linkFactory.getIndex(landscapeRepository.findAll());
    }


    /**
     * This resource serves a landscape DTO and can be addressed by using a {@link FullyQualifiedIdentifier}
     *
     * @return response entity of landscape
     */
    @CrossOrigin(methods = RequestMethod.GET)
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

    /**
     * This resource serves  a group in a landscape and can be addressed by using a {@link FullyQualifiedIdentifier}
     */
    @CrossOrigin(methods = RequestMethod.GET)
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
            GroupApiModel groupItem = new GroupApiModel(group.get(), landscape.getItems().retrieve(group.get().getItems()));
            linkFactory.setGroupLinksRecursive(groupItem);
            return new ResponseEntity<>(groupItem, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * This resource serves an item in a landscape and can be addressed by using a {@link FullyQualifiedIdentifier}
     */
    @CrossOrigin(methods = RequestMethod.GET)
    @GetMapping(path = "/{landscapeIdentifier}/{groupIdentifier}/{itemIdentifier}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemApiModel> item(@PathVariable String landscapeIdentifier,
                                             @PathVariable String groupIdentifier,
                                             @PathVariable String itemIdentifier
    ) {
        Landscape landscape = landscapeRepository.findDistinctByIdentifier(landscapeIdentifier).orElse(null);
        if (landscape == null) {
            return ResponseEntity.notFound().build();
        }

        Optional<Item> item = landscape.getItems().find(itemIdentifier, groupIdentifier);
        if (item.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Group group = landscape.getGroup(groupIdentifier).orElseThrow();
        ItemApiModel apiModel = new ItemApiModel(item.get(), group);
        linkFactory.setItemSelfLink(apiModel);
        return new ResponseEntity<>(apiModel, HttpStatus.OK);
    }

    /**
     * Creates a new landscape
     */
    @PostMapping(path = "/landscape")
    public ResponseEntity<Object> create(@RequestBody String body) {
        LandscapeDescription env = indexingDispatcher.createLandscapeDescriptionFromBody(body);
        Optional<URI> uriForDTO = getURIForDTO(env.getFullyQualifiedIdentifier());
        return uriForDTO
                .map(uri -> ResponseEntity.created(uri).build())
                .orElseGet(() -> ResponseEntity.unprocessableEntity().build());
    }

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

    @CrossOrigin(methods = RequestMethod.GET)
    @GetMapping(path = "/landscape/{identifier}/log")
    public ResponseEntity<ProcessLog> log(@PathVariable String identifier) {

        Landscape landscape = landscapeRepository.findDistinctByIdentifier(identifier).orElse(null);
        if (landscape == null) {
            return ResponseEntity.notFound().build();
        }

        return new ResponseEntity<>(landscape.getLog(), HttpStatus.OK);
    }

    @CrossOrigin(methods = RequestMethod.GET)
    @GetMapping(path = "/landscape/{identifier}/search/{query}", produces = "application/json")
    public ResponseEntity<Set<ItemApiModel>> search(@PathVariable String identifier, @PathVariable String query) {

        Landscape landscape = landscapeRepository.findDistinctByIdentifier(identifier).orElse(null);
        if (landscape == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Group> groups = landscape.getGroups();
        try {
            Set<ItemApiModel> results = landscape.search(query).stream()
                    .map(item -> new ItemApiModel(item, groups.get(item.getGroup())))
                    .collect(Collectors.toSet());
            return new ResponseEntity<>(results, HttpStatus.OK);
        } catch (RuntimeException error) {
            LOGGER.error("Search query '{}' in landscape {} failed: {}", query, landscape, error.getMessage(), error);
            return ResponseEntity.badRequest().build();
        }

    }

    @CrossOrigin(methods = RequestMethod.GET)
    @GetMapping(path = "/landscape/{identifier}/facets", produces = "application/json")
    public ResponseEntity<List<FacetResult>> facets(@PathVariable String identifier) {

        Landscape landscape = landscapeRepository.findDistinctByIdentifier(identifier).orElse(null);
        if (landscape == null) {
            return ResponseEntity.notFound().build();
        }

        return new ResponseEntity<>(landscape.getSearchIndex().facets(), HttpStatus.OK);
    }


    @CrossOrigin(methods = RequestMethod.GET)
    @GetMapping(path = "/mapping", produces = "application/json")
    public ResponseEntity<Map<String, String>> mapping() {
        return new ResponseEntity<>(frontendMapping.getKeys(), HttpStatus.OK);
    }


    @CrossOrigin(methods = RequestMethod.GET)
    @GetMapping(path = "/description", produces = "application/json")
    public ResponseEntity<Map<String, String>> description() {
        return new ResponseEntity<>(frontendMapping.getDescriptions(), HttpStatus.OK);
    }

    /**
     * Trigger reindexing of a landscape source.
     */
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

    @CrossOrigin(methods = RequestMethod.GET)
    @GetMapping(path = "/user")
    public ResponseEntity<String> whoAmI(OAuth2AuthenticationToken principal) {
        if (principal != null) {
            return ResponseEntity.of(Optional.ofNullable(principal.getPrincipal().getAttribute("login")));
        } else {
            return ResponseEntity.of(Optional.of("anonymous"));
        }
    }


    private Optional<URI> getURIForDTO(FullyQualifiedIdentifier fullyQualifiedIdentifier) {
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
