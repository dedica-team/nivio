package de.bonndan.nivio.model;

/**
 * A contexts contains groups.
 *
 *
 * parent: {@link Unit}
 * children: {@link Group}
 */
public class Context extends GraphNode<Unit, Group> {

    public Context(String identifier, String name, String owner, String contact, String description, String type, Unit parent) {
        super(identifier, name, owner, contact, description, type, parent);
    }
}
