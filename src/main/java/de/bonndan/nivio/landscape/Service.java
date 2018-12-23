package de.bonndan.nivio.landscape;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import de.bonndan.nivio.input.dto.InterfaceDescription;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.*;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames={"landscape_identifier", "identifier", "group"}))
public class Service implements LandscapeItem {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @Pattern(regexp = LandscapeItem.IDENTIFIER_VALIDATION)
    private String identifier;

    @NotNull
    @ManyToOne
    @JsonBackReference
    private Landscape landscape;

    private String layer = LandscapeItem.LAYER_APPLICATION;

    private String type = LandscapeItem.TYPE_SERVICE;

    private String name;

    private String owner;

    private String team;

    private String contact;

    private String homepage;

    private String description;

    private String short_name;

    private String version;

    private String software;

    private String repository;

    @Column(name = "`group`")
    private String group;

    private String visibility;

    private String[] tags;

    @ElementCollection(targetClass = String.class)
    private Set<String> networks;

    private String machine;

    private String scale;

    private String host_type;

    @JsonManagedReference
    @OneToMany(targetEntity = ServiceStatus.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "service")
    private Set<ServiceStatus> statuses = new HashSet<>();

    @JsonManagedReference
    @OneToMany(targetEntity = DataFlow.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "sourceEntity")
    private Set<DataFlowItem> dataFlow = new HashSet<>();

    @JsonBackReference
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "TYPE_INFRASTRUCTURE",
            joinColumns = {@JoinColumn(name = "service_id")},
            inverseJoinColumns = {@JoinColumn(name = "infrastructure_identifier")})
    private Set<Service> providedBy = new HashSet<>();

    @JsonBackReference
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE, mappedBy = "providedBy")
    /* integration test fails with two relations
    @JoinTable(name = "TYPE_INFRASTRUCTURE",
            joinColumns = {@JoinColumn(name = "infrastructure_identifier")},
            inverseJoinColumns = {@JoinColumn(name = "service_identifier")})*/
    private Set<Service> provides = new HashSet<>();

    private String note;

    @ElementCollection(targetClass = InterfaceDescription.class)
    private Set<InterfaceItem> interfaces;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        if (StringUtils.isEmpty(identifier)) {
            throw new RuntimeException("Identifier must not be empty");
        }
        this.identifier = identifier.toLowerCase();
    }

    @Override
    @Transient
    public FullyQualifiedIdentifier getFullyQualifiedIdentifier() {
        return FullyQualifiedIdentifier.build(landscape == null ? "" : landscape.getIdentifier(), group, identifier);
    }

    public Landscape getLandscape() {
        return landscape;
    }

    public void setLandscape(Landscape landscape) {
        this.landscape = landscape;
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

    @Override
    public Map<String, Status> getStatuses() {

        var st = new HashMap<String, Status>();
        statuses.forEach(serviceStatus -> st.put(serviceStatus.getLabel(), serviceStatus.getStatus()));
        return st;
    }

    public void setStatuses(Map<String, Status> statuses) {
        statuses.forEach((label, status) -> {
            Optional<ServiceStatus> existing = this.statuses.stream()
                    .filter(serviceStatus -> serviceStatus.getLabel().equals(label))
                    .findFirst();

            existing.ifPresentOrElse(
                    serviceStatus -> serviceStatus.setStatus(status),
                    () -> {
                        var added = new ServiceStatus();
                        added.setService(this);
                        added.setLabel(label);
                        added.setStatus(status);
                        this.statuses.add(added);
                    });
        });
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public Set<String> getNetworks() {
        return networks;
    }

    public void setNetworks(Set<String> networks) {
        this.networks = networks;
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

    public Set<DataFlowItem> getDataFlow() {
        return dataFlow;
    }

    public void setDataFlow(Set<DataFlowItem> outgoing) {
        dataFlow.addAll(outgoing);
    }

    public Set<Service> getProvidedBy() {
        return providedBy;
    }

    public String getLayer() {
        return layer;
    }

    public void setLayer(String layer) {
        this.layer = layer;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getNote() {
        return note;
    }

    public Set<Service> getProvides() {
        return provides;
    }

    public void setProvides(Set<Service> provides) {
        this.provides = provides;
    }

    public void setInterfaces(Set<InterfaceItem> interfaces) {
        this.interfaces = interfaces;
    }

    public Set<InterfaceItem> getInterfaces() {
        return interfaces;
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
     *
     * @return the fully qualified identifier for this service
     */
    @Override
    public String toString() {
        if (landscape == null)
            return identifier;

       return getFullyQualifiedIdentifier().toString();
    }

}
