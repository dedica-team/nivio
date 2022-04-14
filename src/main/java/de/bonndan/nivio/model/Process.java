package de.bonndan.nivio.model;

import de.bonndan.nivio.assessment.Assessable;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A directed subgraph with possibly more than one start or end points describing a process in the landscape.
 */
public class Process extends GraphComponent {

    @NonNull
    private final List<Branch> branches;

    public Process(@NonNull final String identifier,
                   @Nullable final String name,
                   @Nullable final String owner,
                   @Nullable final String contact,
                   @Nullable final String description,
                   @Nullable final String type,
                   @NonNull final List<Branch> branches,
                   @NonNull final Landscape parent
    ) {
        super(identifier, name, owner, contact, description, type, Objects.requireNonNull(parent).getFullyQualifiedIdentifier());
        this.branches = Objects.requireNonNull(branches);
    }

    @NonNull
    @Override
    public Landscape getParent() {
        return _getParent(Landscape.class);
    }

    @NonNull
    @Override
    public Set<? extends GraphComponent> getChildren() {
        return Collections.emptySet();
    }

    @NonNull
    public List<Branch> getBranches() {
        return branches;
    }

    @NonNull
    @Override
    public Set<Assessable> getAssessables() {
        return branches.stream()
                .flatMap(branch -> branch.getEdges().stream())
                .map(uri -> indexReadAccess.findRelation(Relation.parseSourceURI(uri), Relation.parseTargetURI(uri))
                        .orElseThrow(() -> new NoSuchElementException(String.format("%s not present", uri)))
                )
                .collect(Collectors.toSet());
    }

    /**
     * Returns true if the relation is part of the process.
     *
     * @param relation relation to search for
     * @return true if a branch contains the relation
     */
    public boolean contains(@NonNull final Relation relation) {
        final URI fullyQualifiedIdentifier = Objects.requireNonNull(relation).getFullyQualifiedIdentifier();
        return branches.stream()
                .flatMap(branch -> branch.getEdges().stream())
                .anyMatch(uri -> uri.equals(fullyQualifiedIdentifier));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Process)) return false;
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), branches);
    }
}
