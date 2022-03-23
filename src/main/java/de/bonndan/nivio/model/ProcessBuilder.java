package de.bonndan.nivio.model;

import java.util.ArrayList;
import java.util.List;

public final class ProcessBuilder extends GraphNodeBuilder<ProcessBuilder, Process, Landscape> {

    private List<Branch> branches = new ArrayList<>();

    private ProcessBuilder() {
    }

    public ProcessBuilder withBranches(List<Branch> branches) {
        this.branches = branches;
        return this;
    }

    public static ProcessBuilder aProcess() {
        return new ProcessBuilder();
    }

    @Override
    public ProcessBuilder getThis() {
        return this;
    }

    @Override
    public Process build() {
        Process process = new Process(identifier, name, owner, contact, description, type, branches, parent);
        process.setLinks(links);
        process.setLabels(labels);
        return process;
    }
}
