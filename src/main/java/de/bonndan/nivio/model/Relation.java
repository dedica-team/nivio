package de.bonndan.nivio.model;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import de.bonndan.nivio.assessment.Assessable;
import de.bonndan.nivio.assessment.StatusValue;
import org.springframework.lang.NonNull;

import java.io.Serializable;
import java.util.*;

import static de.bonndan.nivio.model.ComponentDiff.compareOptionals;
import static de.bonndan.nivio.model.ComponentDiff.compareStrings;

/**
 * Indication of an incoming or outgoing relation like data flow or dependency (provider).
 *
 * Outgoing flows having a target which matches a service identifier will cause a relation to be created.
 */
public class Relation implements Labeled, Assessable, Serializable {

    public static final String DELIMITER = ";";

    @JsonIdentityReference(alwaysAsId = true) //needed for debugging internal models
    private final Item source;

    @JsonIdentityReference(alwaysAsId = true) //needed for debugging internal models
    private final Item target;

    private final String description;

    private final String format;

    private final RelationType type;

    private final Map<String, String> labels = new HashMap<>();

    public Relation(@NonNull final Item source,
                    @NonNull final Item target,
                    final String description,
                    final String format,
                    final RelationType type
    ) {
        if (source.equals(target)) {
            throw new IllegalArgumentException(String.format("Relation source and target are equal.%s %s", source, target));
        }

        this.source = Objects.requireNonNull(source, "Source is null");
        this.target = Objects.requireNonNull(target, "Target is null");
        this.description = description;
        this.format = format;
        this.type = type;
    }

    public String getIdentifier() {
        return source.getFullyQualifiedIdentifier().jsonValue() + DELIMITER + target.getFullyQualifiedIdentifier().jsonValue();
    }

    public RelationType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getFormat() {
        return format;
    }

    public Item getTarget() {
        return target;
    }

    public Item getSource() {
        return source;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Relation relation = (Relation) o;
        return Objects.equals(source, relation.source) && Objects.equals(target, relation.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, target);
    }

    @Override
    public String toString() {
        return "Relation{" + getIdentifier() + '}';
    }

    @Override
    public String getLabel(String key) {
        return getLabels().get(key);
    }

    @NonNull
    @Override
    public Map<String, String> getLabels() {
        return labels;
    }

    @Override
    public void setLabel(String key, String value) {
        labels.put(key, value);
    }

    @Override
    public Set<StatusValue> getAdditionalStatusValues() {
        return StatusValue.fromMapping(indexedByPrefix(Label.status));
    }

    @Override
    public String getAssessmentIdentifier() {
        return getIdentifier();
    }

    @Override
    public List<? extends Assessable> getChildren() {
        return new ArrayList<>();
    }

    /**
     * Compare on field level against a newer version.
     *
     * @param newer the newer version
     * @return a list of changes if any changes are present
     * @throws IllegalArgumentException if the arg is not comparable
     */
    public List<String> getChanges(Relation newer) {
        if (!this.equals(newer)) {
            throw new IllegalArgumentException(String.format("Cannot compare relation %s against %s", newer, this));
        }

        List<String> changes = new ArrayList<>();
        changes.addAll(compareStrings(this.format, newer.format, "Format"));
        changes.addAll(compareStrings(this.description, newer.description, "Description"));
        changes.addAll(compareOptionals(Optional.ofNullable(this.type), Optional.ofNullable(newer.type), "Type"));

        return changes;
    }
}
