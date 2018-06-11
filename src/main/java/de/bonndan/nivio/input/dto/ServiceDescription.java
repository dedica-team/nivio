package de.bonndan.nivio.input.dto;


import de.bonndan.nivio.landscape.LandscapeItem;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ServiceDescription implements LandscapeItem {

    @NotEmpty
    private String environment;

    @NotEmpty
    private String identifier;

    @NotEmpty
    private String name;

    private String short_name;
    private String version;
    private String software;
    private String owner;
    private String description;
    private String team;
    private String contact;
    private String homepage;
    private String repository;
    private String bounded_context;
    private String visibility;
    private String[] tags;
    private String network_zone;
    private String machine;
    private String scale;
    private String host_type;

    private Set<DataFlow> interfaces = new HashSet<>();

    private Set<DataFlow> dataFlow = new HashSet<>();

    private List<ServiceDescription> infrastructure = new ArrayList<>();
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getBounded_context() {
        return bounded_context;
    }

    public void setBounded_context(String bounded_context) {
        this.bounded_context = bounded_context;
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

    public Set<DataFlow> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(Set<DataFlow> interfaces) {
        this.interfaces = interfaces;
    }

    public Set<DataFlow> getDataFlow() {
        return dataFlow;
    }

    public void setDataFlow(Set<DataFlow> dataFlow) {
        this.dataFlow = dataFlow;
    }

    public List<ServiceDescription> getInfrastructure() {
        return infrastructure;
    }

    public void setInfrastructure(List<ServiceDescription> infrastructure) {
        this.infrastructure = infrastructure;
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
}
