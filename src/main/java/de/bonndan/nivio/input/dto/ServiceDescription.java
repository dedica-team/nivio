package de.bonndan.nivio.input.dto;


import de.bonndan.nivio.landscape.LandscapeItem;

import javax.validation.constraints.NotEmpty;
import java.util.*;

/**
 * This is representation of a service in the textual form as described in a source file.
 */
public class ServiceDescription implements LandscapeItem {

    @NotEmpty
    private String environment;

    @NotEmpty
    private String type;

    @NotEmpty
    private String identifier;

    @NotEmpty
    private String name;

    private String note;

    private String short_name;
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
    private String network_zone;
    private String machine;
    private String scale;
    private String host_type;

    private Map<String, String> statuses = new HashMap<>();

    private Set<DataFlowDescription> interfaces = new HashSet<>();

    private Set<DataFlowDescription> dataFlow = new HashSet<>();

    private List<String> provided_by = new ArrayList<>();
    private String protocol;
    private Integer port;

    public ServiceDescription() {
    }

    public ServiceDescription(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
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

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public String getNetwork_zone() {
        return network_zone;
    }

    public void setNetwork_zone(String network_zone) {
        this.network_zone = network_zone;
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

    public void setHost_type(String host_type) {
        this.host_type = host_type;
    }

    public Set<DataFlowDescription> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(Set<DataFlowDescription> interfaces) {
        this.interfaces = interfaces;
    }

    public Set<DataFlowDescription> getDataFlow() {
        return dataFlow;
    }

    public void setDataFlow(Set<DataFlowDescription> dataFlow) {
        this.dataFlow = dataFlow;
    }

    public List<String> getProvided_by() {
        return provided_by;
    }

    public void setProvided_by(List<String> provided_by) {
        this.provided_by = provided_by;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getPort() {
        return port;
    }

    public Map<String, String> getStatuses() {
        return statuses;
    }

    public void setStatuses(Map<String, String> statuses) {
        this.statuses = statuses;
    }
}
