package de.bonndan.nivio.assessment;

import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Label;
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
    @NonNull
    public Set<StatusValue> getAdditionalStatusValues() {
        return StatusValue.fromMapping(getAssessmentIdentifier(), group.indexedByPrefix(Label.status));
    }

    @Override
    @NonNull
    public String getAssessmentIdentifier() {
        return group.getFullyQualifiedIdentifier().toString();
    }

    @Override
    @NonNull
    public List<? extends Assessable> getChildren() {
        return items;
    }
}
