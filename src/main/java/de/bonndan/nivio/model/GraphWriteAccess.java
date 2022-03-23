package de.bonndan.nivio.model;

import org.springframework.lang.NonNull;

import java.net.URI;
import java.util.Objects;


public class GraphWriteAccess<T extends GraphComponent> {

    private final Index<T> index;
    private final IndexReadAccess<T> readAccess;

    public GraphWriteAccess(@NonNull final Index<T> index, @NonNull final IndexReadAccess<T> readAccess) {
        this.index = Objects.requireNonNull(index);
        this.readAccess = Objects.requireNonNull(readAccess);
    }

    /**
     * Adds a child or replaces the similar one.
     *
     * Prevents that a sibling with a different parent is replaced.
     *
     * @param added to add or replace
     * @return the old element
     */
    public T addOrReplaceChild(T added) {
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
        var existing = index.addOrReplace(added).map(old -> {
            old.detach();
            return old;
        }).orElse(null);
        addOrReplaceRelation(RelationFactory.createChild(parentComponent, added));
        return existing;
    }

    /**
     * Removes a node.
     *
     * @param node ndoe
     * @return true if successful
     */
    public boolean removeChild(T node) {
        @SuppressWarnings("unchecked") T parent = (T) node.getParent();
        return index.removeChild(parent, node);
    }

    /**
     * Adds a relation to the index.
     *
     * If a similar relation exists, it is removed and detached from index access.
     *
     * @param relation relation to add
     */
    public void addOrReplaceRelation(@NonNull final Relation relation) {
        Objects.requireNonNull(relation).attach(readAccess);
        index.addOrReplace(relation).ifPresent(present -> {
            if (present != relation) { //safety net
                present.detach();
            }
        });
    }

    public void removeRelation(Relation relation) {
        index.removeRelation(relation);
    }
}
