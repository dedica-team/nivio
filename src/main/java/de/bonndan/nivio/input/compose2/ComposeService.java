package de.bonndan.nivio.input.compose2;

import de.bonndan.nivio.input.dto.ServiceDescription;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class ComposeService {

    private String identifier;

    private List<String> links;

    private List<String> labels;

    private List<Integer> ports;

    private List<Network> networks;

    /**
     * Transforms into a dto.
     */
    public ServiceDescription toDto() {
        ServiceDescription description = new ServiceDescription(identifier);
        if (networks != null) {
            List<String> nets = networks.stream().map(Network::getName).collect(Collectors.toList());
            description.setNetworks(new HashSet<>(nets));
        }
        return description;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public List<Network> getNetworks() {
        return networks;
    }
}
