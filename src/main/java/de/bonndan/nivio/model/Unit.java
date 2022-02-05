package de.bonndan.nivio.model;

import org.springframework.lang.NonNull;

import java.util.Objects;
import java.util.Set;

/**
 * A unit contains context.
 *
 *
 * parent: {@link Landscape}
 * children: {@link Context}
 */
public class Unit extends GraphComponent {

    Unit(String identifier, String name, String owner, String contact, String description, String type, Landscape landscape) {
        super(identifier, name, owner, contact, description, type, Objects.requireNonNull(landscape).getFullyQualifiedIdentifier());
    }

    @NonNull
    @Override
    public Landscape getParent() {
        return _getParent(Landscape.class);
    }

    @NonNull
    @Override
    public Set<Group> getChildren() {
        return getChildren(component -> true, Group.class);
    }
}
