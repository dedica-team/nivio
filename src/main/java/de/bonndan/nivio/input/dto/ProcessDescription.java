package de.bonndan.nivio.input.dto;

import de.bonndan.nivio.model.ComponentClass;
import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import org.springframework.lang.NonNull;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * todo parent getchildren
 */
public class ProcessDescription extends ComponentDescription {

    private String landscape;

    private List<BranchDescription> branches = new ArrayList<>();

    @NonNull
    @Override
    public URI getFullyQualifiedIdentifier() {
        return FullyQualifiedIdentifier.build(ComponentClass.process, landscape, getIdentifier());
    }

    @Override
    public String getParentIdentifier() {
        return getLandscape();
    }

    public String getLandscape() {
        return landscape;
    }

    public void setLandscape(String landscape) {
        this.landscape = landscape;
    }

    public List<BranchDescription> getBranches() {
        return branches;
    }

    public void setBranches(@NonNull final List<BranchDescription> branches) {
        this.branches = Objects.requireNonNull(branches);
    }

    public void assignNotNull(ProcessDescription increment) {
        super.assignNotNull(increment);
        if (increment.getBranches() != null) {
            setBranches(increment.getBranches());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProcessDescription)) return false;

        return getIdentifier().equals(((ProcessDescription) o).getIdentifier());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
