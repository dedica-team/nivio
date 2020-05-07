package de.bonndan.nivio.assessment;

import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import de.bonndan.nivio.model.LandscapeImpl;
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
    private final AssessmentFactory factory;

    public AssessmentController(LandscapeRepository landscapeRepository, AssessmentFactory factory) {
        this.landscapeRepository = landscapeRepository;
        this.factory = factory;
    }

    @CrossOrigin(methods = RequestMethod.GET)
    @RequestMapping(path = "/{identifier}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Assessment> landscape(@PathVariable String identifier) {
        FullyQualifiedIdentifier fqi = FullyQualifiedIdentifier.from(identifier);
        LandscapeImpl landscape = landscapeRepository.findDistinctByIdentifier(fqi.getLandscape()).orElse(null);
        if (landscape == null) {
            return ResponseEntity.notFound().build();
        }

        return new ResponseEntity<>(factory.assess(landscape), HttpStatus.OK);
    }

}
