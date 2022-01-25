package de.bonndan.nivio.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.bonndan.nivio.assessment.Assessable;
import de.bonndan.nivio.assessment.StatusValue;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static de.bonndan.nivio.model.ComponentDiff.compareCollections;
import static de.bonndan.nivio.model.ComponentDiff.compareStrings;

/**
 * Base class for graph elements
 *
 * @param <P> Parent
 * @param <C> Child
 */
public /* abstract */ class GraphNode<P extends GraphNode, C extends GraphNode> implements Component, Labeled, Linked, Assessable {

    @NotNull
    @Pattern(regexp = IdentifierValidation.PATTERN)
    private final String identifier;

    private final String name;

    private final String owner;

    private final String contact;

    private final String description;

    private final String type;

    private final Set<C> children = ConcurrentHashMap.newKeySet();

    private final Map<String, Link> links = new HashMap<>();

    private final Map<String, String> labels = new ConcurrentHashMap<>();

    private P parent;

    private final FullyQualifiedIdentifier fullyQualifiedIdentifier;

    protected GraphNode(@NonNull final String identifier,
                        @Nullable final String name,
                        @Nullable final String owner,
                        @Nullable final String contact,
                        @Nullable final String description,
                        @Nullable final String type,
                        @NonNull final P parent
    ) {
        if (!StringUtils.hasLength(identifier)) {
            throw new IllegalArgumentException("Identifier must not be empty");
        }
        this.identifier = identifier.toLowerCase();
        this.name = name;
        this.owner = owner;
        this.contact = contact;
        this.description = description;
        this.type = type;
        this.parent = Objects.requireNonNull(parent);
        this.fullyQualifiedIdentifier = FullyQualifiedIdentifier.from(parent.getFullyQualifiedIdentifier(), this.identifier);
    }


    @NonNull
    public String getIdentifier() {
        return identifier;
    }

    @NonNull
    @Override
    public FullyQualifiedIdentifier getFullyQualifiedIdentifier() {
        return Objects.requireNonNull(fullyQualifiedIdentifier, "FQI not present, nodes requires parent to be set");
    }

    @Override
    @NonNull
    public String getName() {
        return name == null ? "" : name;
    }

    public String getOwner() {
        return owner;
    }

    @Override
    public String getIcon() {
        return getLabel(Label.icon);
    }

    @Override
    public String getColor() {
        return getLabel(Label.color);
    }

    public String getContact() {
        return contact;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getType() {
        return type;
    }

    @JsonIgnore
    @NonNull
    public P getParent() {
        return parent;
    }

    @NonNull
    @Override
    public Set<StatusValue> getAdditionalStatusValues() {
        return StatusValue.fromMapping(getAssessmentIdentifier(), indexedByPrefix(Label.status));
    }

    @NonNull
    @Override
    public String getAssessmentIdentifier() {
        return getFullyQualifiedIdentifier().toString();
    }

    @NonNull
    @Override
    public Set<? extends Assessable> getAssessables() {
        return getChildren();
    }

    /**
     * Returns an immutable copy of the child nodes.
     */
    @JsonIgnore
    @NonNull
    public Set<C> getChildren() {
        return Set.copyOf(children);
    }

    /**
     * Adds a child or replaces the similar one.
     *
     * @param child to add or replace
     */
    public void addOrReplaceChild(@NonNull final C child) {
        Objects.requireNonNull(child, "child is null");
        if (child.getParent() != this) {
            throw new IllegalArgumentException("GraphNode cannot be added as child, has different parent");
        }
        children.stream().filter(graphNode -> graphNode.equals(child)).findFirst().ifPresent(this::removeChild);
        children.add(child);
    }

    /**
     * @param child to delete
     * @return true if the child has been removed or was not present.
     */
    public boolean removeChild(@NonNull final C child) {
        child.detach();
        return children.remove(child);
    }

    /**
     * Clears the references to parent and children
     */
    protected void detach() {
        parent = null;
        children.clear();
    }


    @Override
    public String getLabel(String key) {
        return labels.get(key);
    }

    @NonNull
    @Override
    public Map<String, String> getLabels() {
        return labels;
    }

    @Override
    public Map<String, Link> getLinks() {
        return links;
    }

    /**
     * Compare on field level against a newer version.
     *
     * @param newer the newer version
     * @return a list of changes if any changes are present
     * @throws IllegalArgumentException if the arg is not comparable
     */
    public List<String> getChanges(final GraphNode<P,C> newer) {
        if (!newer.equals(this)) {
            throw new IllegalArgumentException(String.format("Cannot compare component %s against %s", newer, this));
        }

        List<String> changes = new ArrayList<>();
        changes.addAll(compareStrings(this.contact, newer.contact, "Contact"));
        changes.addAll(compareStrings(this.description, newer.description, "Description"));
        changes.addAll(compareStrings(this.name, newer.name, "Name"));
        changes.addAll(compareStrings(this.owner, newer.owner, "Owner"));
        changes.addAll(compareStrings(this.type, newer.type, "Type"));
        changes.addAll(compareCollections(this.links.keySet(), newer.links.keySet(), "Links"));
        changes.addAll(newer.diff(this));

        return changes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;

        if (!(o instanceof Item))
            return false;

        GraphNode node = (GraphNode) o;
        if (toString() == null)
            return false;

        return toString().equals(node.toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(toString());
    }

    /**
     * @return the fully qualified identifier for this service
     */
    @Override
    public String toString() {
        return getFullyQualifiedIdentifier().toString();
    }
}
