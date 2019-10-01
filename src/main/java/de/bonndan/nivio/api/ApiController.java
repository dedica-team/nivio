package de.bonndan.nivio.api;

import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.api.dto.LandscapeDTO;
import de.bonndan.nivio.input.*;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.SourceFormat;
import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.model.*;
import de.bonndan.nivio.util.URLHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping(path = "/")
public class ApiController {

    private final LandscapeRepository landscapeRepository;
    private final Indexer indexer;
    private final FileFetcher fileFetcher;

    @Autowired
    public ApiController(LandscapeRepository landscapeRepository, Indexer indexer, FileFetcher fileFetcher) {
        this.landscapeRepository = landscapeRepository;
        this.indexer = indexer;
        this.fileFetcher = fileFetcher;
    }


    /**
     * Overview on all landscapes.
     *
     * @return dto list
     */
    @RequestMapping(path = "/", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Iterable<LandscapeDTO> landscapes() {
        Iterable<LandscapeImpl> all = landscapeRepository.findAll();

        return StreamSupport.stream(all.spliterator(), false)
                .map(LandscapeDTO::from)
                .collect(Collectors.toList());
    }

    @RequestMapping(path = "/landscape/{identifier}")
    public ResponseEntity<LandscapeDTO> landscape(@PathVariable String identifier) {
        LandscapeImpl landscape = landscapeRepository.findDistinctByIdentifier(identifier).orElse(null);
        if (landscape == null)
            return ResponseEntity.notFound().build();

        return new ResponseEntity<>(LandscapeDTO.from(landscape), HttpStatus.OK);
    }

    /**
     * Creates a new landscape
     */
    @RequestMapping(path = "/landscape", method = RequestMethod.POST)
    public ProcessLog create(@RequestBody String body) {
        LandscapeDescription env = EnvironmentFactory.fromString(body);
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

        SourceReference sourceReference = new SourceReference(SourceFormat.from(format));
        sourceReference.setUrl(null);
        sourceReference.setContent(body);

        ItemDescriptionFactory factory = ItemDescriptionFormatFactory.getFactory(sourceReference, env);
        List<ItemDescription> itemDescriptions = factory.getDescriptions(sourceReference);

        env.setItemDescriptions(itemDescriptions);

        return indexer.reIndex(env);
    }

    /**
     * Delete a single service from the landscape.
     *
     * Reindexes the landscape on success.
     *
     * @param identifier landscape
     * @param fqi fully qualified identifier of the item
     * @return the process log
     */
    @RequestMapping(path = "/landscape/{identifier}/items/{fqi}", method = RequestMethod.DELETE)
    public ProcessLog deleteService(
            @PathVariable String identifier,
            @PathVariable String fqi
    ) {
        LandscapeImpl landscape = landscapeRepository.findDistinctByIdentifier(identifier).orElse(null);
        if (landscape == null)
            return new ProcessLog(new ProcessingException(null, "Could not find landscape " + identifier));

        FullyQualifiedIdentifier from = FullyQualifiedIdentifier.from(fqi);
        if (from == null)
            return new ProcessLog(new ProcessingException(landscape, "Could use fully qualified identifier " + fqi));

        Optional<LandscapeItem> item = ServiceItems.find(FullyQualifiedIdentifier.build(from.getLandscape(), from.getGroup(), from.getIdentifier()), landscape.getItems());
        if (!item.isPresent()) {
            return new ProcessLog(new ProcessingException(landscape, "Could find item " + fqi));
        }

        landscape.getItems().remove(item.get());
        return process(landscape);
    }

    @RequestMapping(path = "/landscape/{identifier}/items", method = RequestMethod.GET)
    public ResponseEntity<List<Item>> items(@PathVariable String identifier) {

        LandscapeImpl landscape = landscapeRepository.findDistinctByIdentifier(identifier).orElse(null);
        if (landscape == null)
            return ResponseEntity.notFound().build();

        return new ResponseEntity<>(List.copyOf(landscape.getItems()), HttpStatus.OK);

    }

    /**
     * Trigger reindexing of a landscape source.
     */
    @RequestMapping(path = "/reindex/{landscape}", method = RequestMethod.POST)
    public ProcessLog reindex(@PathVariable String landscape) {
        LandscapeImpl distinctByIdentifier = landscapeRepository.findDistinctByIdentifier(landscape).orElse(null);
        if (distinctByIdentifier == null)
            return new ProcessLog(new ProcessingException(null, "Could not find lanscape " + landscape));

        return process(distinctByIdentifier);
    }

    private ProcessLog process(Landscape landscape) {
        if (landscape == null || StringUtils.isEmpty(landscape.getSource())) {
            return new ProcessLog(new ProcessingException(landscape, "Cannot process empty source."));
        }

        File file = new File(landscape.getSource());
        if (file.exists()) {
            LandscapeDescription landscapeDescription = EnvironmentFactory.fromYaml(file);
            return indexer.reIndex(landscapeDescription);
        }

        URL url = URLHelper.getURL(landscape.getSource());
        if (url != null) {
            return process(EnvironmentFactory.fromString(fileFetcher.get(url), url));
        }

        return process(EnvironmentFactory.fromString(landscape.getSource()));
    }
}
