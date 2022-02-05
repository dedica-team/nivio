package de.bonndan.nivio.model;

import org.springframework.lang.NonNull;

import java.util.Set;

/**
 * A context contains groups.
 *
 *
 * parent: {@link Unit}
 * children: {@link Group}
 */
public class Context extends GraphComponent {

    public Context(String identifier, String name, String owner, String contact, String description, String type, Unit parent) {
        super(identifier, name, owner, contact, description, type, parent.getFullyQualifiedIdentifier());
    }

    @NonNull
    @Override
    public Unit getParent() {
        return _getParent(Unit.class);
    }

    @NonNull
    @Override
    public Set<Group> getChildren() {
        return getChildren(component -> true, Group.class);
    }

}
