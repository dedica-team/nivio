package de.bonndan.nivio.model;

import de.bonndan.nivio.assessment.Assessable;
import de.bonndan.nivio.assessment.StatusValue;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * This is a decorator for {@link Group} when creating {@link de.bonndan.nivio.assessment.Assessment}s.
 */
public class AssessableGroup implements Assessable {

    @NonNull
    private final Group group;

    @NonNull
    private final List<Item> items;

    public AssessableGroup(@NonNull final Group group, @NonNull final Set<Item> items) {
        this.group = Objects.requireNonNull(group);
        this.items = new ArrayList<>(Objects.requireNonNull(items));
    }

    @Override
    public Set<StatusValue> getAdditionalStatusValues() {
        return StatusValue.fromMapping(group.indexedByPrefix(Label.status));
    }

    @Override
    public String getAssessmentIdentifier() {
        return group.getFullyQualifiedIdentifier().toString();
    }

    @Override
    public List<? extends Assessable> getChildren() {
        return items;
    }
}
