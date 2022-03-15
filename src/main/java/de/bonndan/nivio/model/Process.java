package de.bonndan.nivio.model;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * A directed subgraph with possibly more than one start or end points describing a process in the landscape.
 *
 *
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
    public GraphComponent getParent() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Process)) return false;
        if (!super.equals(o)) return false;
        Process process = (Process) o;
        return branches.equals(process.branches);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), branches);
    }
}
