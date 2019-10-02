package de.bonndan.nivio.input.dto;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.bonndan.nivio.model.*;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotEmpty;
import java.util.*;

/**
 * This is representation of a service in the textual form as described in a source file.
 */
public class ItemDescription implements LandscapeItem {

    @NotEmpty
    private String environment;

    @NotEmpty
    private String layer = LandscapeItem.LAYER_APPLICATION;

    private String type;

    @NotEmpty
    private String identifier;

    @NotEmpty
    private String name;

    private String note;

    private String short_name;
    private String icon;

    private String version;
    private String software;
    private String owner;
    private String description;
    private String team;
    private String contact;
    private String homepage;
    private String repository;
    private String group;
    private String visibility;
    private String[] tags;
    private Set<String> networks = new HashSet<>();
    private String machine;
    private String scale;
    private String host_type;

    private String costs;
    private String capability;

    @JsonDeserialize(contentAs = StatusDescription.class)
    private Set<StatusItem> statuses = new HashSet<>();

    @JsonDeserialize(contentAs = InterfaceDescription.class)
    private Set<InterfaceItem> interfaces = new HashSet<>();

    @JsonDeserialize(contentAs = DataFlowDescription.class)
    private Set<DataFlowDescription> dataFlow = new HashSet<>();

    private List<String> provided_by = new ArrayList<>();

    private Lifecycle lifecycle;

    private Map<String, String> labels = new HashMap<>();

    public ItemDescription() {
    }

    public ItemDescription(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = StringUtils.trimAllWhitespace(identifier);
    }

    @Override
    public FullyQualifiedIdentifier getFullyQualifiedIdentifier() {
        return FullyQualifiedIdentifier.build(environment, group, identifier);
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getLayer() {
        return layer;
    }

    public void setLayer(String layer) {
        this.layer = layer;
    }

    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getShort_name() {
        return short_name;
    }

    public void setShort_name(String short_name) {
        this.short_name = short_name;
    }

    @Override
    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSoftware() {
        return software;
    }

    public void setSoftware(String software) {
        this.software = software;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setLifecycle(Lifecycle lifecycle) {
        this.lifecycle = lifecycle;
    }

    @Override
    public Lifecycle getLifecycle() {
        return lifecycle;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    @Override
    public Map<String, String> getLabels() {
        return labels;
    }

    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }

    public String getMachine() {
        return machine;
    }

    public void setMachine(String machine) {
        this.machine = machine;
    }

    public String getScale() {
        return scale;
    }

    public void setScale(String scale) {
        this.scale = scale;
    }

    public String getHost_type() {
        return host_type;
    }

    public Set<String> getNetworks() {
        return networks;
    }

    public void setHost_type(String host_type) {
        this.host_type = host_type;
    }

    public Set<InterfaceItem> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(Set<InterfaceItem> interfaces) {
        this.interfaces = interfaces;
    }

    /**
     * Returns a copy, do not use for adding elements
     *
     * @return
     */
    public Set<DataFlowItem> getDataFlow() {
        dataFlow.forEach(dataFlowItem -> dataFlowItem.setSource(identifier));
        return new HashSet<>(dataFlow);
    }

    public void addDataFlow(DataFlowItem dataFlow) {
        this.dataFlow.add((DataFlowDescription) dataFlow);
    }

    public List<String> getProvided_by() {
        return provided_by;
    }

    public void setProvided_by(List<String> provided_by) {
        this.provided_by = provided_by;
    }

    public Set<StatusItem> getStatuses() {
        return (Set<StatusItem>) statuses;
    }

    @Override
    public void setStatus(StatusItem statusItem) {
        statuses.add(statusItem);
    }

    public void setNetworks(Set<String> networks) {
        this.networks = networks;
    }

    @Override
    public String getCapability() {
        return capability;
    }

    public void setCapability(String capability) {
        this.capability = capability;
    }

    @Override
    public String getCosts() {
        return costs;
    }

    public void setCosts(String costs) {
        this.costs = costs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        LandscapeItem landscapeItem = (LandscapeItem) o;

        return toString().equals(landscapeItem.toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(toString());
    }

    /**
     * @return the fully qualified identifier for this service description
     */
    @Override
    public String toString() {
        if (StringUtils.isEmpty(environment))
            return identifier;

        return FullyQualifiedIdentifier.build(environment, group, identifier).toString();
    }
}
