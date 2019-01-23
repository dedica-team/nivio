package de.bonndan.nivio.api;

import de.bonndan.nivio.api.dto.LandscapeDTO;
import de.bonndan.nivio.input.*;
import de.bonndan.nivio.input.dto.Environment;
import de.bonndan.nivio.input.dto.ServiceDescription;
import de.bonndan.nivio.input.dto.SourceFormat;
import de.bonndan.nivio.landscape.Landscape;
import de.bonndan.nivio.landscape.LandscapeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping(path = "/")
public class ApiController {

    private final LandscapeRepository landscapeRepository;

    private final FileChangeProcessor fileChangeProcessor;

    @Autowired
    public ApiController(LandscapeRepository landscapeRepository, FileChangeProcessor fileChangeProcessor) {
        this.landscapeRepository = landscapeRepository;
        this.fileChangeProcessor = fileChangeProcessor;
    }


    /**
     * Overview on all landscapes.
     *
     * @return dto list
     */
    @RequestMapping(path = "/", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Iterable<LandscapeDTO> landscapes() {
        Iterable<Landscape> all = landscapeRepository.findAll();

        return StreamSupport.stream(all.spliterator(), false)
                .map(LandscapeDTO::from)
                .collect(Collectors.toList());
    }

    @RequestMapping(path = "/landscape/{identifier}")
    public LandscapeDTO landscape(@PathVariable String identifier) {
        return LandscapeDTO.from(landscapeRepository.findDistinctByIdentifier(identifier));
    }

    /**
     * Creates a new landscape
     */
    @RequestMapping(path = "/landscape", method = RequestMethod.POST)
    public ProcessLog create(@RequestBody String body) {
        Environment env = EnvironmentFactory.fromString(body);
        return fileChangeProcessor.process(env);
    }

    @RequestMapping(path = "/landscape/{identifier}/services", method = RequestMethod.POST)
    public ProcessLog indexLandscape(
            @PathVariable String identifier,
            @RequestHeader(name = "format") String format,
            @RequestBody String body
    ) {
        ServiceDescriptionFactory factory = ServiceDescriptionFormatFactory.getFactory(SourceFormat.from(format));
        List<ServiceDescription> serviceDescriptions = factory.fromString(body);

        Environment env = new Environment();
        env.setIdentifier(identifier);
        env.setIsIncrement(true);
        env.setServiceDescriptions(serviceDescriptions);

        return fileChangeProcessor.process(env);
    }

    /**
     * Trigger reindexing of a landscape source.
     */
    @RequestMapping(path = "/reindex/{landscape}", method = RequestMethod.POST)
    public ProcessLog reindex(@PathVariable String landscape) {
        Landscape distinctByIdentifier = landscapeRepository.findDistinctByIdentifier(landscape);
        return fileChangeProcessor.process(distinctByIdentifier);
    }
}
