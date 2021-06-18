package de.bonndan.nivio.assessment;

import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import de.bonndan.nivio.model.Landscape;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

/**
 * Stores and loads and creates Assessments with FullyQualifiedIdentifier as key
 */
@Component
public class AssessmentRepository {

    private final HashMap<FullyQualifiedIdentifier, Assessment> repository;

    public AssessmentRepository() {
        repository = new HashMap<>();
    }

    public void clean() {
        repository.clear();
    }

    @NonNull
    public Assessment createAssessment(@NonNull Landscape landscape) {
        var testedLandscape = Objects.requireNonNull(landscape, "Assessments can't be created from a null value");
        var newAssessment = AssessmentFactory.createAssessment(testedLandscape);
        storeAssessment(testedLandscape.getFullyQualifiedIdentifier(), newAssessment);
        return newAssessment;
    }

    public Optional<Assessment> getAssessment(@Nullable FullyQualifiedIdentifier fullyQualifiedIdentifier) {
        return Optional.ofNullable(repository.get(fullyQualifiedIdentifier));
    }

    private void storeAssessment(@NonNull FullyQualifiedIdentifier fullyQualifiedIdentifier, @NonNull Assessment assessment) {
        repository.put(fullyQualifiedIdentifier, assessment);
    }

}
