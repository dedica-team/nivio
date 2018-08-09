package de.bonndan.nivio.output.controller;

import de.bonndan.nivio.landscape.Landscape;
import de.bonndan.nivio.landscape.LandscapeRepository;
import de.bonndan.nivio.output.dld4e.Dld4eRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.persistence.EntityNotFoundException;

@Controller
@RequestMapping(path = "/render")
public class RenderController {

    private final LandscapeRepository landscapeRepository;

    @Autowired
    public RenderController(LandscapeRepository landscapeRepository) {
        this.landscapeRepository = landscapeRepository;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/dld4e/{landscape}")
    public ResponseEntity<String> dld4eResource(@PathVariable(name = "landscape") final String landscapeIdentifier) {
        Landscape landscape = landscapeRepository.findDistinctByIdentifier(landscapeIdentifier);
        if (landscape == null)
            throw new EntityNotFoundException("Not found");

        Dld4eRenderer graphRenderer = new Dld4eRenderer();
        return new ResponseEntity<>(graphRenderer.render(landscape), HttpStatus.OK);
    }
}
