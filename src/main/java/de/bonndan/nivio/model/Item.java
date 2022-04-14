package de.bonndan.nivio.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import de.bonndan.nivio.assessment.Assessable;
import de.bonndan.nivio.output.Color;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import static de.bonndan.nivio.model.ComponentDiff.compareCollections;
import static de.bonndan.nivio.model.ComponentDiff.compareOptionals;

public class Item extends GraphComponent implements PhysicalComponent, ItemComponent {

    private final Layer layer;

    /**
     * technical address
     */
    private final URI address;

    @JsonManagedReference
    private Set<ServiceInterface> interfaces = new HashSet<>();

    public Item(@NonNull final String identifier,
                @Nullable final String name,
                @Nullable final String owner,
                @Nullable final String contact,
                @Nullable final String description,
                @Nullable final String color,
                @Nullable final String icon,
                @Nullable final String type,
                @Nullable final URI address,
                @Nullable final Layer layer,
                @NonNull final Group parent
    ) {
        super(identifier, name, owner, contact, description, type, Objects.requireNonNull(parent).getFullyQualifiedIdentifier());

        this.address = address;
        this.layer = layer;

        //these are effectively mutable
        this.setLabel(Label.color, Color.safe(color));
        this.setLabel(Label.icon, icon);
    }

    public String getAddress() {
        return address != null ? address.toString() : null;
    }

    public void setInterfaces(Set<ServiceInterface> interfaces) {
        this.interfaces = interfaces;
    }

    public Set<ServiceInterface> getInterfaces() {
        return interfaces;
    }

    @Override
    public String getLayer() {
        if (layer == null) {
            return Layer.domain.name();
        }
        return layer.name();
    }

    /**
     * Compare on field level against a newer version.
     *
     * @param newer the newer version
     * @return a list of changes if any changes are present
     * @throws IllegalArgumentException if the arg is not comparable
     */
    public List<String> getChanges(final Item newer) {
        if (!newer.equals(this)) {
            throw new IllegalArgumentException(String.format("Cannot compare component %s against %s", newer, this));
        }
        List<String> changes1 = super.getChanges(newer);

        List<String> changes = new ArrayList<>();
        changes.addAll(compareOptionals(Optional.ofNullable(this.address), Optional.ofNullable(newer.address), "Address"));

        List<String> collect = this.interfaces.stream().map(ServiceInterface::toString).collect(Collectors.toList());
        List<String> collect2 = newer.getInterfaces().stream().map(ServiceInterface::toString).collect(Collectors.toList());
        changes.addAll(compareCollections(collect, collect2, "Interfaces"));

        changes.addAll(changes1);
        return changes;
    }

    @NonNull
    @Override
    public Group getParent() {
        return _getParent(Group.class);
    }

    @NonNull
    @Override
    public Set<Part> getChildren() {
        return getChildren(component -> true, Part.class);
    }

    @JsonIgnore
    @NonNull
    @Override
    public Set<Relation> getRelations() {
        return indexReadAccess.getRelations(this.getFullyQualifiedIdentifier());
    }

    @NonNull
    @Override
    public Set<Assessable> getAssessables() {
        Set< Assessable> assessables = new HashSet<>();
        assessables.addAll(getRelations());
        assessables.addAll(getChildren());
        return assessables;
    }
}