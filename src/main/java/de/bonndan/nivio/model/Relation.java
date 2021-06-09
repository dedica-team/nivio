package de.bonndan.nivio.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.*;

import static de.bonndan.nivio.model.ComponentDiff.compareOptionals;
import static de.bonndan.nivio.model.ComponentDiff.compareStrings;

/**
 * Indication of an incoming or outgoing relation like data flow or dependency (provider).
 *
 * Outgoing flows having a target which matches a service identifier will cause a relation to be created.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Relation implements Labeled, Serializable {

    public static final String DELIMITER = ";";

    @JsonIdentityReference(alwaysAsId = true)
    private final Item source;

    @JsonIdentityReference(alwaysAsId = true)
    private final Item target;

    private final String description;

    private final String format;

    private final RelationType type;

    private final Map<String, String> labels = new HashMap<>();

    public Relation(@NonNull final Item source,
                    @NonNull final Item target
    ) {
        this(source, target, null, null, null);
    }

    public Relation(@NonNull final Item source,
                    @NonNull final Item target,
                    final String description,
                    final String format,
                    final RelationType type
    ) {
        if (source.equals(target)) {
            throw new IllegalArgumentException("Relation source and target are equal.");
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
    @JsonAnySetter
    public void setLabel(String key, String value) {
        labels.put(key, value);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    static class ApiModel {

        public static final String INBOUND = "inbound";
        public static final String OUTBOUND = "outbound";

        @JsonIdentityReference(alwaysAsId = true)
        public final Item source;

        @JsonIdentityReference(alwaysAsId = true)
        public final Item target;

        public final String description;

        public final String format;

        public final RelationType type;

        public final String name;

        public final String id;

        public final String direction;

        ApiModel(@NonNull final Relation relation, @NonNull final Item owner) {
            source = relation.source;
            target = relation.target;
            description = relation.description;
            format = relation.format;
            type = relation.type;
            id = relation.getIdentifier();

            if (relation.source.equals(owner)) {
                name = StringUtils.isEmpty(target.getName()) ? target.getIdentifier() : target.getName();
                direction = OUTBOUND;
            } else {
                name = StringUtils.isEmpty(source.getName()) ? source.getIdentifier() : source.getName();
                direction = INBOUND;
            }
        }
    }

    /**
     * Compare on field level against a newer version.
     *
     * @param newer the newer version
     * @return a list of changes if any changes are present
     * @throws IllegalArgumentException if the arg is not comparable
     */
    public List<String> getChanges(Relation newer) {
        if (!newer.equals(this)) {
            throw new IllegalArgumentException(String.format("Cannot compare relation %s against %s", newer, this));
        }

        List<String> changes = new ArrayList<>();
        changes.addAll(compareStrings(this.format, newer.format, "Format"));
        changes.addAll(compareStrings(this.description, newer.description, "Description"));
        changes.addAll(compareOptionals(Optional.ofNullable(this.type), Optional.ofNullable(newer.type), "Type"));

        return changes;
    }
}
