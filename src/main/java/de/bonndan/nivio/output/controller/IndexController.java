package de.bonndan.nivio.output.controller;

import de.bonndan.nivio.input.FileChangeProcessor;
import de.bonndan.nivio.landscape.Landscape;
import de.bonndan.nivio.landscape.LandscapeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RestController
@RequestMapping(path = "/")
public class IndexController {

    private final LandscapeRepository landscapeRepository;

    private final FileChangeProcessor fileChangeProcessor;

    @Autowired
    public IndexController(LandscapeRepository landscapeRepository, FileChangeProcessor fileChangeProcessor) {
        this.landscapeRepository = landscapeRepository;
        this.fileChangeProcessor = fileChangeProcessor;
    }

    @RequestMapping(path = "/", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Iterable<Landscape> landscapes() {
        return landscapeRepository.findAll();
    }

    @RequestMapping(path = "/landscape/{identifier}")
    public Landscape landscape(@PathVariable String identifier) {
        return landscapeRepository.findDistinctByIdentifier(identifier);
    }

    /**
     * Trigger reindexing of a landscape source.
     *
     */
    @RequestMapping(path = "/landscape/{identifier}", method = RequestMethod.POST)
    public Landscape updateLandscape(@PathVariable String identifier) {
        Landscape distinctByIdentifier = landscapeRepository.findDistinctByIdentifier(identifier);
        String path = distinctByIdentifier.getPath();
        fileChangeProcessor.process(new File(path));
        return distinctByIdentifier;
    }
}
