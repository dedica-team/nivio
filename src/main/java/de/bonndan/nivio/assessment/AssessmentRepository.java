package de.bonndan.nivio.assessment;

import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stores and loads and creates Assessments with FullyQualifiedIdentifier as key
 */
@Component
public class AssessmentRepository {

    private final Map<FullyQualifiedIdentifier, Assessment> repository;

    public AssessmentRepository() {
        repository = new ConcurrentHashMap<>();
    }

    public void clean() {
        repository.clear();
    }

    /**
     * Returns the current assessment for the landscape.
     *
     * @param fullyQualifiedIdentifier landscape identifier
     * @return an assessment if present
     */
    @NonNull
    public Optional<Assessment> getAssessment(@NonNull final FullyQualifiedIdentifier fullyQualifiedIdentifier) {
        return Optional.ofNullable(repository.get(Objects.requireNonNull(fullyQualifiedIdentifier, "Null instead of FQI given")));
    }

    /**
     * Saves the given assessment
     *
     * @param fqi        landscape identifier
     * @param assessment assessment
     */
    public void save(@NonNull final FullyQualifiedIdentifier fqi, @NonNull final Assessment assessment) {
        repository.put(fqi, assessment);
    }
}
