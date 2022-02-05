package de.bonndan.nivio.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.bonndan.nivio.assessment.Assessable;
import de.bonndan.nivio.assessment.StatusValue;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import static de.bonndan.nivio.model.ComponentDiff.compareCollections;
import static de.bonndan.nivio.model.ComponentDiff.compareStrings;

/**
 * Base class for graph elements
 */
public abstract class GraphComponent implements Component, Assessable {

    @NotNull
    @Pattern(regexp = IdentifierValidation.PATTERN)
    protected final String identifier;

    private final String name;

    private final String owner;

    private final String contact;

    private final String description;

    private final Map<String, Link> links = new HashMap<>();

    private final Map<String, String> labels = new ConcurrentHashMap<>();

    private final String type;

    private final URI fullyQualifiedIdentifier;

    private final URI parent;

    protected IndexReadAccess<? extends GraphComponent> indexReadAccess;

    protected GraphComponent(@NonNull final String identifier,
                             @Nullable final String name,
                             @Nullable final String owner,
                             @Nullable final String contact,
                             @Nullable final String description,
                             @Nullable final String type,
                             @NonNull final URI parent
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
        this.parent = verifyParent(parent);
        this.fullyQualifiedIdentifier = FullyQualifiedIdentifier.from(parent, this);
    }

    protected void attach(@NonNull final IndexReadAccess<? extends GraphComponent> index) {
        this.indexReadAccess = Objects.requireNonNull(index);
    }

    protected void detach() {
        this.indexReadAccess = null;
    }

    /**
     * @return true if the index has been attached
     */
    public boolean isAttached() {
        return indexReadAccess != null;
    }

    protected URI verifyParent(final URI parent) {
        return Objects.requireNonNull(parent, "parent must not be null");
    }

    @NonNull
    public String getIdentifier() {
        return identifier;
    }

    @NonNull
    public URI getFullyQualifiedIdentifier() {
        return fullyQualifiedIdentifier;
    }

    @Override
    public String getType() {
        return type;
    }

    @NonNull
    @Override
    public Set<StatusValue> getAdditionalStatusValues() {
        return StatusValue.fromMapping(getFullyQualifiedIdentifier(), indexedByPrefix(Label.status));
    }

    @NonNull
    @Override
    public Set<Assessable> getAssessables() {
        return Set.copyOf(getChildren(component -> true, GraphComponent.class));
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
    public List<String> getChanges(final GraphComponent newer) {
        if (!newer.equals(this)) {
            throw new IllegalArgumentException(String.format("Cannot compare component %s against %s", newer, this));
        }

        List<String> changes = new ArrayList<>();
        changes.addAll(compareStrings(this.contact, newer.contact, "Contact"));
        changes.addAll(compareStrings(this.description, newer.description, "Description"));
        changes.addAll(compareStrings(this.name, newer.name, "Name"));
        changes.addAll(compareStrings(this.owner, newer.owner, "Owner"));
        changes.addAll(compareCollections(this.links.keySet(), newer.links.keySet(), "Links"));

        changes.addAll(compareStrings(this.type, newer.type, "Type"));
        changes.addAll(newer.diff(this));

        return changes;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;

        if (!(o instanceof GraphComponent))
            return false;

        GraphComponent node = (GraphComponent) o;
        if (toString() == null)
            return false;

        return toString().equals(node.toString());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    /**
     * @return the fully qualified identifier for this service
     */
    @Override
    public String toString() {
        return fullyQualifiedIdentifier == null ? identifier : getFullyQualifiedIdentifier().toString();
    }


    protected <T extends GraphComponent> T _getParent(Class<T> cls) {
        if (parent == null) {
            return null;
        }
        if (Landscape.class.equals(cls)) {
            return (T) indexReadAccess.get(FullyQualifiedIdentifier.build(Landscape.class, parent.getAuthority())).orElseThrow();
        }
        return indexReadAccess.get(parent)
                .map(graphComponent -> (T) graphComponent)
                .orElseThrow(() -> new NoSuchElementException(String.format("Parent %s not present in graph", parent)));
    }


    @NonNull
    protected <T extends Component> Set<T> getChildren(@NonNull final Predicate<Component> predicate, @NonNull final Class<T> cls) {
        Set<T> set = new LinkedHashSet<>();
        indexReadAccess.getChildren(this.getFullyQualifiedIdentifier()).stream()
                .filter(predicate)
                .forEach(component -> set.add((T) component));
        return set;
    }

    @JsonIgnore //for internal debuggin
    @NonNull
    public abstract GraphComponent getParent();

    @NonNull
    public abstract Set<? extends GraphComponent> getChildren();

    @NonNull
    @Override
    public String getParentIdentifier() {
        return getParent().getIdentifier();
    }
}
