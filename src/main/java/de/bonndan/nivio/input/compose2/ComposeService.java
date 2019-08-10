package de.bonndan.nivio.input.compose2;

import de.bonndan.nivio.input.dto.InterfaceDescription;
import de.bonndan.nivio.input.dto.ServiceDescription;
import de.bonndan.nivio.landscape.Groups;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class ComposeService {

    private String identifier;

    private List<String> links;

    private List<String> labels;

    private List<String> ports;

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

        /*
         * ports become interfaces
         */
        if (ports != null) {
            description.setInterfaces(
                    new HashSet<>(
                        ports.stream().map(InterfaceDescription::new).collect(Collectors.toList())
                    )
            );
        }

        /*
          link targets become providers
         */
        if (links != null) {
            links.forEach(s -> description.getProvided_by().add(s));
        }

        description.setGroup(Groups.COMMON);
        return description;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public List<Network> getNetworks() {
        return networks;
    }

    public String getIdentifier() {
        return identifier;
    }

    public List<String> getLinks() {
        return links;
    }

    public void setLinks(List<String> links) {
        this.links = links;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public List<String> getPorts() {
        return ports;
    }

    public void setPorts(List<String> ports) {
        this.ports = ports;
    }

    public void setNetworks(List<Network> networks) {
        this.networks = networks;
    }
}
