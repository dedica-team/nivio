package de.bonndan.nivio.model;

import java.util.Objects;


public class IndexWriteAccess<T extends Component> {

    private final Index<T> index;

    public IndexWriteAccess(Index<T> index) {
        this.index = index;
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
        index.addOrReplace(added);
    }

    /**
     * Removes a node.
     *
     * @param node ndoe
     * @return true if successful
     */
    public boolean removeChild(T node) {
        return index.removeChild(null, node);
    }
}
