package de.bonndan.nivio.landscape;


import org.neo4j.ogm.annotation.*;
import org.springframework.stereotype.Indexed;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.*;

@NodeEntity
public class Service implements LandscapeItem {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private String environment;

    @NotNull
    @Pattern(regexp = LandscapeItem.IDENTIFIER_VALIDATION)
    private String identifier;

    @NotNull
    private String type;

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

    @Relationship(type = "DATAFLOW")
    Set<DataFlow> dataFlow = new HashSet<>();

    @Relationship(type = "PROVIDED_BY", direction = Relationship.INCOMING)
    Set<Service> providedBy = new HashSet<>();

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier.toLowerCase();
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

    public Set<DataFlow> getDataFlow() {
        return dataFlow;
    }

    public void setDataFlow(Set<DataFlow> outgoing) {
        this.dataFlow = outgoing;
    }

    public Set<Service> getProvidedBy() {
        return providedBy;
    }

    public void setProvidedBy(Set<Service> providedBy) {
        this.providedBy = providedBy;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * Check if service provides any other.
     *
     * Returns false if an infrasttructure item has no "providedBy" relationship.
     */
    public boolean providesAny() {
        if (LandscapeItem.APPLICATION.equals(type)) {
            return true;
        }

        return !providedBy.isEmpty();
    }
}
