package de.bonndan.nivio.model;

/**
 * A unit contains context.
 *
 *
 * parent: {@link Landscape}
 * children: {@link Context}
 */
public class Unit extends GraphNode<Landscape, Context> {

    public Unit(String identifier, String name, String owner, String contact, String description, String type, Landscape landscape) {
        super(identifier, name, owner, contact, description, type, landscape);
    }
}
