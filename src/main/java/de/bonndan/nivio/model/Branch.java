package de.bonndan.nivio.model;

import org.springframework.lang.NonNull;

import java.net.URI;
import java.util.List;
import java.util.Objects;

/**
 * Part of a {@link Process}
 */
public class Branch {

    private final List<URI> edges;

    /**
     * @param edges add relations of this branch. Start and/or end are part of other branches.
     */
    public Branch(@NonNull final List<URI> edges) {
        this.edges = Objects.requireNonNull(edges);
    }

    public List<URI> getEdges() {
        return edges;
    }
}
