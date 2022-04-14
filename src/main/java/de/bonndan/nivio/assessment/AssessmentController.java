package de.bonndan.nivio.assessment;

import de.bonndan.nivio.model.ComponentClass;
import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.model.LandscapeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping(path = AssessmentController.PATH)
public class AssessmentController {

    public static final String PATH = "/assessment";
    private final LandscapeRepository landscapeRepository;
    private final AssessmentRepository assessmentRepository;

    public AssessmentController(LandscapeRepository landscapeRepository, AssessmentRepository assessmentRepository) {
        this.landscapeRepository = landscapeRepository;
        this.assessmentRepository = assessmentRepository;
    }

    @CrossOrigin(methods = RequestMethod.GET)
    @GetMapping(path = "/{identifier}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Assessment> landscape(@PathVariable String identifier) {
        URI fqi = FullyQualifiedIdentifier.build(ComponentClass.landscape, identifier);
        Landscape landscape = landscapeRepository.findDistinctByIdentifier(identifier).orElse(null);
        if (landscape == null) {
            return ResponseEntity.notFound().build();
        }

        var optionalAssessment = assessmentRepository.getAssessment(fqi);
        if (optionalAssessment.isEmpty()) {
            return new ResponseEntity<>(assessmentRepository.getAssessment(landscape.getFullyQualifiedIdentifier()).orElse(Assessment.empty()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(optionalAssessment.get(), HttpStatus.OK);
        }

    }

}
