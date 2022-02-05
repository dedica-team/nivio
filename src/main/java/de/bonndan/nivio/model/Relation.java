package de.bonndan.nivio.model;

import de.bonndan.nivio.assessment.Assessable;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.input.ProcessingException;
import org.springframework.lang.NonNull;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static de.bonndan.nivio.model.ComponentDiff.compareOptionals;
import static de.bonndan.nivio.model.ComponentDiff.compareStrings;

/**
 * Indication of an incoming or outgoing relation like data flow or dependency (provider).
 *
 * Outgoing flows having a target which matches a service identifier will cause a relation to be created.
 */
public class Relation implements Component, Assessable {

    public static final String DELIMITER = ";";

    protected final URI sourceURI;

    protected final URI targetURI;

    private final String description;

    private final String format;

    private final RelationType type;

    private final Map<String, String> labels = new ConcurrentHashMap<>();

    private final URI fullyQualifiedIdentifier;
    private IndexReadAccess<? extends GraphComponent> indexReadAccess;

    public Relation(@NonNull final GraphComponent source,
                    @NonNull final GraphComponent target,
                    final String description,
                    final String format,
                    final RelationType type
    ) {
        if (source.equals(target)) {
            throw new IllegalArgumentException(String.format("Relation source and target are equal.%s %s", source, target));
        }

        this.sourceURI = Objects.requireNonNull(source, "Source is null").getFullyQualifiedIdentifier();
        this.targetURI = Objects.requireNonNull(target, "Target is null").getFullyQualifiedIdentifier();
        this.description = description;
        this.format = format;
        this.type = type;
        try {
            this.fullyQualifiedIdentifier = new URI(Relation.class.getSimpleName().toLowerCase(Locale.ROOT),
                    source.getFullyQualifiedIdentifier().getAuthority(),
                    source.getFullyQualifiedIdentifier().getPath(),
                    "to=" + target.getFullyQualifiedIdentifier(),
                    null
            );
        } catch (URISyntaxException e) {
            throw new ProcessingException("Failed to generate fqi", e);
        }
    }

    @Override
    @NonNull
    public String getIdentifier() {
        return getFullyQualifiedIdentifier().toString();
    }

    @Override
    @NonNull
    public String getName() {
        return getFullyQualifiedIdentifier().toString();
    }

    @Override
    public String getOwner() {
        return null;
    }

    @Override
    public String getIcon() {
        return null;
    }

    @Override
    public String getColor() {
        return null;
    }

    @Override
    @NonNull
    public URI getFullyQualifiedIdentifier() {
        return fullyQualifiedIdentifier;
    }

    @NonNull
    @Override
    public String getParentIdentifier() {
        return getSource().getFullyQualifiedIdentifier().toString();
    }

    public String getType() {
        if (type == null) {
            return null;
        }
        return type.name();
    }

    public String getDescription() {
        return description;
    }

    public String getFormat() {
        return format;
    }

    protected URI getSourceURI() {
        return sourceURI;
    }

    public URI getTargetURI() {
        return targetURI;
    }

    @NonNull
    public Item getTarget() {
        return (Item) indexReadAccess.get(targetURI)
                .orElseThrow(() -> new NoSuchElementException(String.format("Source %s not in index.", sourceURI)));
    }

    @NonNull
    public Item getSource() {
        return (Item) indexReadAccess.get(sourceURI)
                .orElseThrow(() -> new NoSuchElementException(String.format("Source %s not in index.", sourceURI)));
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
    @NonNull
    public Set<StatusValue> getAdditionalStatusValues() {
        return StatusValue.fromMapping(getFullyQualifiedIdentifier(), indexedByPrefix(Label.status));
    }

    @Override
    @NonNull
    public Set<Assessable> getAssessables() {
        return Set.of();
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

    @Override
    public Map<String, Link> getLinks() {
        return Map.of();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Relation)) return false;
        Relation relation = (Relation) o;
        return Objects.equals(sourceURI, relation.sourceURI) && Objects.equals(targetURI, relation.targetURI);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceURI, targetURI);
    }

    @Override
    public String toString() {
        return fullyQualifiedIdentifier.toString();
    }

    void attach(IndexReadAccess<? extends GraphComponent> indexReadAccess) {
        this.indexReadAccess = indexReadAccess;
    }

    void detach() {
        this.indexReadAccess = null;
    }

    boolean isAttached() {
        return indexReadAccess != null;
    }
}
