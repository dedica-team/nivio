package de.bonndan.nivio.assessment;

import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import de.bonndan.nivio.model.Landscape;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * Stores and loads and creates Assessments with FullyQualifiedIdentifier as key
 */
@Component
public class AssessmentRepository {

    private HashMap<FullyQualifiedIdentifier, Assessment> repository;

    public AssessmentRepository() {
        repository = new HashMap<>();
    }

    public void clean() {
        repository = new HashMap<>();
    }

    public Assessment createAssessment(@NonNull Landscape landscape) {
        if (landscape != null) {
            var newAssessment = AssessmentFactory.createAssessment(landscape);
            storeAssessment(landscape.getFullyQualifiedIdentifier(), newAssessment);
            return newAssessment;
        } else {
            return null;
        }
    }

    public Assessment getAssessment(@NonNull FullyQualifiedIdentifier fullyQualifiedIdentifier) {
        if (fullyQualifiedIdentifier != null) {
            return repository.get(fullyQualifiedIdentifier);
        } else {
            return null;
        }
    }

    private void storeAssessment(@NonNull FullyQualifiedIdentifier fullyQualifiedIdentifier, @NonNull Assessment assessment) {
        var newValue = repository.put(fullyQualifiedIdentifier, assessment);
    }

}