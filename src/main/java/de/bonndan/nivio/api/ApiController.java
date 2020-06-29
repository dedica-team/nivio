package de.bonndan.nivio.api;

import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.input.*;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.model.*;
import de.bonndan.nivio.util.URLHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.net.URL;
import java.util.*;

@RestController
@RequestMapping(path = ApiController.PATH)
public class ApiController {

    public static final String PATH = "/api";

    private final LandscapeRepository landscapeRepository;
    private final LandscapeDescriptionFactory landscapeDescriptionFactory;
    private final ItemDescriptionFormatFactory formatFactory;
    private final Indexer indexer;
    private final LinkFactory linkFactory;

    public ApiController(LandscapeRepository landscapeRepository,
                         LandscapeDescriptionFactory landscapeDescriptionFactory,
                         ItemDescriptionFormatFactory formatFactory,
                         Indexer indexer,
                         FileFetcher fileFetcher,
                         LinkFactory linkFactory
    ) {
        this.landscapeRepository = landscapeRepository;
        this.landscapeDescriptionFactory = landscapeDescriptionFactory;
        this.formatFactory = formatFactory;
        this.indexer = indexer;
        this.linkFactory = linkFactory;
    }

    /**
     * Overview on all landscapes.
     *
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
    public ResponseEntity<LandscapeImpl> landscape(@PathVariable String landscapeIdentifier) {
        LandscapeImpl landscape = landscapeRepository.findDistinctByIdentifier(landscapeIdentifier).orElse(null);
        if (landscape == null) {
            return ResponseEntity.notFound().build();
        }
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
        LandscapeImpl landscape = landscapeRepository.findDistinctByIdentifier(landscapeIdentifier).orElse(null);
        if (landscape == null) {
            return ResponseEntity.notFound().build();
        }

        return new ResponseEntity<>(landscape.getGroup(groupIdentifier), HttpStatus.OK);
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
        LandscapeImpl landscape = landscapeRepository.findDistinctByIdentifier(landscapeIdentifier).orElse(null);
        if (landscape == null) {
            return ResponseEntity.notFound().build();
        }

        Optional<Item> item = landscape.getItems().find(itemIdentifier, groupIdentifier);
        if (item.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(item.get(), HttpStatus.OK);
    }

    /**
     * Creates a new landscape
     */
    @RequestMapping(path = "/landscape", method = RequestMethod.POST)
    public ProcessLog create(@RequestBody String body) {
        LandscapeDescription env = LandscapeDescriptionFactory.fromString(body, "request body");
        return indexer.reIndex(env);
    }

    @RequestMapping(path = "/landscape/{identifier}/services", method = RequestMethod.POST)
    public ProcessLog indexLandscape(
            @PathVariable String identifier,
            @RequestHeader(name = "format") String format,
            @RequestBody String body
    ) {
        LandscapeDescription env = new LandscapeDescription();
        env.setIdentifier(identifier);
        env.setIsPartial(true);

        SourceReference sourceReference = new SourceReference(null, format);
        sourceReference.setContent(body);

        ItemDescriptionFactory factory = formatFactory.getFactory(sourceReference, env);
        URL baseUrl = URLHelper.getParentPath(env.getSource());

        List<ItemDescription> itemDescriptions = factory.getDescriptions(sourceReference, baseUrl);

        env.setItemDescriptions(itemDescriptions);

        return indexer.reIndex(env);
    }

    @CrossOrigin(methods = RequestMethod.GET)
    @RequestMapping(path = "/landscape/{identifier}/log", method = RequestMethod.GET)
    public ResponseEntity<ProcessLog> log(@PathVariable String identifier) {

        LandscapeImpl landscape = landscapeRepository.findDistinctByIdentifier(identifier).orElse(null);
        if (landscape == null) {
            return ResponseEntity.notFound().build();
        }

        return new ResponseEntity<>(landscape.getLog(), HttpStatus.OK);

    }

    /**
     * Trigger reindexing of a landscape source.
     */
    @RequestMapping(path = "/reindex/{landscape}", method = RequestMethod.POST)
    public ProcessLog reindex(@PathVariable String landscape) {
        LandscapeImpl distinctByIdentifier = landscapeRepository.findDistinctByIdentifier(landscape).orElse(null);
        if (distinctByIdentifier == null) {
            return new ProcessLog(new ProcessingException(null, "Could not find landscape " + landscape));
        }

        return process(distinctByIdentifier);
    }

    private ProcessLog process(Landscape landscape) {
        if (landscape == null || StringUtils.isEmpty(landscape.getSource())) {
            return new ProcessLog(new ProcessingException(landscape, "Cannot process empty source."));
        }

        File file = new File(landscape.getSource());
        if (file.exists()) {
            LandscapeDescription landscapeDescription = landscapeDescriptionFactory.fromYaml(file);
            return indexer.reIndex(Objects.requireNonNull(landscapeDescription));
        }

        URL url = URLHelper.getURL(landscape.getSource());
        if (url != null) {
            return process(landscapeDescriptionFactory.from(url));
        }

        return process(LandscapeDescriptionFactory.fromString(landscape.getSource(), landscape.getIdentifier() + " source"));
    }

}
