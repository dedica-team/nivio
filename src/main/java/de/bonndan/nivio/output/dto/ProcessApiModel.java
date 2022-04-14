package de.bonndan.nivio.output.dto;

import de.bonndan.nivio.model.Process;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProcessApiModel extends ComponentApiModel {

    private final List<List<URI>> branches;
    private final String contact;

    public ProcessApiModel(Process process) {
        super(process);
        this.contact = process.getContact();
        this.branches = process.getBranches().stream()
                .map(branch -> new ArrayList<>(branch.getEdges()))
                .collect(Collectors.toList());
    }

    public String getContact() {
        return contact;
    }

    public List<List<URI>> getBranches() {
        return branches;
    }
}
