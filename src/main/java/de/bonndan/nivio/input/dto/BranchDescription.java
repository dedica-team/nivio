package de.bonndan.nivio.input.dto;

import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BranchDescription {

    private List<String> nodes = new ArrayList<>();

    public BranchDescription() {

    }

    public BranchDescription(@NonNull final List<String> nodes) {
        this.nodes = Objects.requireNonNull(nodes);
    }


    public List<String> getNodes() {
        return nodes;
    }

    public void setNodes(@NonNull final List<String> nodes) {
        this.nodes = Objects.requireNonNull(nodes);
    }
}
