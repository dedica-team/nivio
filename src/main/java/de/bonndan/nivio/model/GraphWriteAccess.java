package de.bonndan.nivio.model;

import java.net.URI;
import java.util.Objects;


public class GraphWriteAccess<T extends GraphComponent> {

    private final Index<T> index;
    private final IndexReadAccess<T> readAccess;

    public GraphWriteAccess(Index<T> index, IndexReadAccess<T> readAccess) {
        this.index = index;
        this.readAccess = readAccess;
    }

    /**
     * Adds a child or replaces the similar one.
     *
     * Prevents that a sibling with a different parent is replaced.
     *
     * @param added to add or replace
     */
    public void addOrReplaceChild(T added) {
        Objects.requireNonNull(added, "child is null");
        index.get(added.getFullyQualifiedIdentifier()).ifPresent(t -> {
            if (t == added) {
                throw new IllegalArgumentException(String.format("Child %s is already in graph (same object).", added));
            }
        });

        added.attach(readAccess);
        URI parentFQI = added.getParent().getFullyQualifiedIdentifier();
        GraphComponent parentComponent = index.get(parentFQI).orElseThrow();
        if (!parentFQI.equals(parentComponent.getFullyQualifiedIdentifier())) {
            added.detach();
            throw new IllegalArgumentException(String.format("Wrong parent %s", parentFQI));
        }
        index.addOrReplace(added).ifPresent(GraphComponent::detach);
        addOrReplaceRelation(RelationFactory.createChild(parentComponent, added));
    }

    /**
     * Removes a node.
     *
     * @param node ndoe
     * @return true if successful
     */
    public boolean removeChild(T node) {
        T parent = (T) node.getParent();
        return index.removeChild(parent, node);
    }

    public void addOrReplaceRelation(Relation relation) {
        relation.attach(readAccess);
        index.addOrReplace(relation).ifPresent(Relation::detach);
    }

    public void removeRelation(Relation relation) {
        index.removeRelation(relation);
    }
}
