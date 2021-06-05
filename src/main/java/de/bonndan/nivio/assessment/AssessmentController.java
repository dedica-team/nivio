package de.bonndan.nivio.assessment;

import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.model.LandscapeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = AssessmentController.PATH)
public class AssessmentController {

    public static final String PATH = "/assessment";
    private final LandscapeRepository landscapeRepository;

    public AssessmentController(LandscapeRepository landscapeRepository) {
        this.landscapeRepository = landscapeRepository;
    }

    @CrossOrigin(methods = RequestMethod.GET)
    @GetMapping(path = "/{identifier}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Assessment> landscape(@PathVariable String identifier) {
        FullyQualifiedIdentifier fqi = FullyQualifiedIdentifier.from(identifier);
        Landscape landscape = landscapeRepository.findDistinctByIdentifier(fqi.getLandscape()).orElse(null);
        if (landscape == null) {
            return ResponseEntity.notFound().build();
        }

        Assessment assessment = new Assessment(landscape.applyKPIs(landscape.getKpis()));
        return new ResponseEntity<>(assessment, HttpStatus.OK);
    }

}
