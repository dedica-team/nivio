package de.bonndan.nivio.api;

import de.bonndan.nivio.input.IndexingDispatcher;
import de.bonndan.nivio.input.ProcessLog;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.*;
import org.apache.lucene.facet.FacetResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping(path = ApiController.PATH)
public class ApiController {

    public static final String PATH = "/api";

    private final LandscapeRepository landscapeRepository;
    private final LinkFactory linkFactory;
    private final IndexingDispatcher indexingDispatcher;

    public ApiController(LandscapeRepository landscapeRepository,
                         LinkFactory linkFactory,
                         IndexingDispatcher indexingDispatcher
    ) {
        this.landscapeRepository = landscapeRepository;
        this.linkFactory = linkFactory;
        this.indexingDispatcher = indexingDispatcher;
    }

    /**
     * Overview on all landscapes.
     */
    @CrossOrigin(methods = RequestMethod.GET)
    @RequestMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public Index index() {
        return linkFactory.getIndex(landscapeRepository.findAll());
    }


    /**
     * This resource serves a landscape DTO and can be addressed by using a {@link FullyQualifiedIdentifier}
     *
     * @return response entity of landscape
     */
    @CrossOrigin(methods = RequestMethod.GET)
    @RequestMapping(path = "/{landscapeIdentifier}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Landscape> landscape(@PathVariable String landscapeIdentifier) {
        Landscape landscape = landscapeRepository.findDistinctByIdentifier(landscapeIdentifier).orElse(null);
        if (landscape == null) {
            return ResponseEntity.notFound().build();
        }

        //TODO this modifies the landscape components by adding SELF links
        linkFactory.setLandscapeLinksRecursive(landscape);
        return new ResponseEntity<>(landscape, HttpStatus.OK);
    }

    /**
     * This resource serves  a group in a landscape and can be addressed by using a {@link FullyQualifiedIdentifier}
     */
    @CrossOrigin(methods = RequestMethod.GET)
    @RequestMapping(path = "/{landscapeIdentifier}/{groupIdentifier}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Group> group(@PathVariable String landscapeIdentifier,
                                       @PathVariable String groupIdentifier
    ) {
        Landscape landscape = landscapeRepository.findDistinctByIdentifier(landscapeIdentifier).orElse(null);
        if (landscape == null) {
            return ResponseEntity.notFound().build();
        }

        //TODO this modifies the landscape components by adding SELF links
        Optional<Group> group = landscape.getGroup(groupIdentifier);
        if (group.isPresent()) {
            linkFactory.setGroupLinksRecursive(group.get());
            return new ResponseEntity<>(group.get(), HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * This resource serves an item in a landscape and can be addressed by using a {@link FullyQualifiedIdentifier}
     */
    @CrossOrigin(methods = RequestMethod.GET)
    @RequestMapping(path = "/{landscapeIdentifier}/{groupIdentifier}/{itemIdentifier}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Item> item(@PathVariable String landscapeIdentifier,
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
        Item item1 = item.get();
        linkFactory.setItemSelfLink(item1);
        return new ResponseEntity<>(item1, HttpStatus.OK);
    }

    /**
     * Creates a new landscape
     */
    @RequestMapping(path = "/landscape", method = RequestMethod.POST)
    public ResponseEntity<Object> create(@RequestBody String body) throws URISyntaxException {
        LandscapeDescription env = indexingDispatcher.createFromBody(body);
        Optional<URI> uriForDTO = getURIForDTO(env);
        return uriForDTO
                .map(uri -> ResponseEntity.created(uri).build())
                .orElseGet(() -> ResponseEntity.unprocessableEntity().build());
    }

    @RequestMapping(path = "/landscape/{identifier}/items", method = RequestMethod.POST)
    public ResponseEntity<Object> addItems(
            @PathVariable String identifier,
            @RequestHeader(name = "format") String format,
            @RequestBody String body
    ) throws URISyntaxException {
        LandscapeDescription dto = indexingDispatcher.createFromBodyItems(identifier, format, body);
        Optional<URI> uriForDTO = getURIForDTO(dto);
        return uriForDTO
                .map(uri -> ResponseEntity.created(uri).build())
                .orElseGet(() -> ResponseEntity.unprocessableEntity().build());
    }

    @CrossOrigin(methods = RequestMethod.GET)
    @RequestMapping(path = "/landscape/{identifier}/log", method = RequestMethod.GET)
    public ResponseEntity<ProcessLog> log(@PathVariable String identifier) {

        Landscape landscape = landscapeRepository.findDistinctByIdentifier(identifier).orElse(null);
        if (landscape == null) {
            return ResponseEntity.notFound().build();
        }

        return new ResponseEntity<>(landscape.getLog(), HttpStatus.OK);
    }

    @CrossOrigin(methods = RequestMethod.GET)
    @RequestMapping(path = "/landscape/{identifier}/search/{query}", method = RequestMethod.GET)
    public ResponseEntity<Set<Item>> search(@PathVariable String identifier, @PathVariable String query) {

        Landscape landscape = landscapeRepository.findDistinctByIdentifier(identifier).orElse(null);
        if (landscape == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            return new ResponseEntity<>(landscape.getItems().search(query), HttpStatus.OK);
        } catch (RuntimeException ignored) {
            return ResponseEntity.badRequest().build();
        }

    }

    @CrossOrigin(methods = RequestMethod.GET)
    @RequestMapping(path = "/landscape/{identifier}/facets", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<List<FacetResult>> facets(@PathVariable String identifier) {

        Landscape landscape = landscapeRepository.findDistinctByIdentifier(identifier).orElse(null);
        if (landscape == null) {
            return ResponseEntity.notFound().build();
        }

        return new ResponseEntity<>(landscape.getItems().facets(), HttpStatus.OK);
    }


    /**
     * Trigger reindexing of a landscape source.
     */
    @RequestMapping(path = "/reindex/{landscape}", method = RequestMethod.POST)
    public ResponseEntity<Object> reindex(@PathVariable String landscape) throws URISyntaxException {
        Landscape existing = landscapeRepository.findDistinctByIdentifier(landscape).orElse(null);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        LandscapeDescription env = indexingDispatcher.fromIncoming(existing);
        Optional<URI> uriForDTO = getURIForDTO(env);
        return uriForDTO
                .map(uri -> ResponseEntity.created(uri).build())
                .orElseGet(() -> ResponseEntity.unprocessableEntity().build());
    }

    private Optional<URI> getURIForDTO(LandscapeDescription env) {
        Optional<Link> link = Optional.ofNullable(linkFactory.generateComponentLink(env.getFullyQualifiedIdentifier()));
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
