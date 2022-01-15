package de.bonndan.nivio.assessment;

import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.model.Labeled;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.*;

/**
 * This is a decorator for {@link Group} when creating {@link de.bonndan.nivio.assessment.Assessment}s.
 */
public class AssessableGroup implements Assessable, Labeled {

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

    @Override
    public String getLabel(String key) {
        return group.getLabel(key);
    }

    @Override
    @NonNull
    public Map<String, String> getLabels() {
        return group.getLabels();
    }

    @Override
    public void setLabel(@NonNull final String key, @Nullable final String value) {
        // the decorator is not meant to be modified
    }
}
