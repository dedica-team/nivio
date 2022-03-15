package de.bonndan.nivio.model;

import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Objects;

/**
 * Part of a {@link Process}
 */
public class Branch {

    private final List<Relation> edges;

    /**
     * @param edges add relations of this branch. Start and/or end are part of other branches.
     */
    public Branch(@NonNull final List<Relation> edges) {
        this.edges = Objects.requireNonNull(edges);
    }

    public List<Relation> getEdges() {
        return edges;
    }
}
