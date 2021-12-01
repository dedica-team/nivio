package de.bonndan.nivio.input.compose2;

import de.bonndan.nivio.input.dto.InterfaceDescription;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.model.RelationFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ComposeService {

    private String identifier;

    private List<String> links;

    private Map<String, String> labels;

    private List<String> ports;

    private List<Network> networks;

    /**
     * Transforms into a dto.
     */
    public ItemDescription getDescription() {
        ItemDescription description = new ItemDescription(identifier);
        if (networks != null) {
            networks.forEach(network -> description.setLabel(
                            Label.network.withPrefix(network.toString()), network.getName()
                    )
            );
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
            links.forEach(s -> description.addOrReplaceRelation(RelationFactory.createProviderDescription(s, description.getFullyQualifiedIdentifier().toString())));
        }

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

    public Map<String, String> getLabels() {
        return labels;
    }

    public void setLabels(Map<String, String> labels) {
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
