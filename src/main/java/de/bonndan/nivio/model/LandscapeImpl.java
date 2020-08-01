package de.bonndan.nivio.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import de.bonndan.nivio.LandscapeConfig;
import de.bonndan.nivio.assessment.Assessable;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.input.ProcessLog;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.util.StringUtils;

import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Think of a group of servers and apps, like a "project", "workspace" or stage.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LandscapeImpl implements Landscape, Labeled, Assessable {

    /**
     * Immutable unique identifier. Maybe use an URN.
     */
    @Pattern(regexp = LandscapeItem.IDENTIFIER_VALIDATION)
    private String identifier;

    /**
     * Human readable name.
     */
    private String name;

    /**
     * Maintainer email
     */
    private String contact;

    private String description;

    private String source;

    @JsonIgnore
    private final LandscapeItems items = new LandscapeItems();

    private LandscapeConfig config;

    private final Map<String, GroupItem> groups = new HashMap<>();

    private ProcessLog processLog;

    private final Map<String, String> labels = new HashMap<>();
    private final Map<String, Link> links = new HashMap<>();
    private String owner;

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public FullyQualifiedIdentifier getFullyQualifiedIdentifier() {
        return FullyQualifiedIdentifier.build(identifier, null, null);
    }

    public void setIdentifier(String identifier) {
        this.identifier = StringUtils.trimAllWhitespace(identifier);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public LandscapeItems getItems() {
        return items;
    }

    public void setItems(Set<Item> items) {
        this.items.setItems(items);
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String getContact() {
        return contact;
    }

    @Override
    public LandscapeConfig getConfig() {
        if (config == null) {
            config = new LandscapeConfig();
        }
        return config;
    }

    @JsonIgnore
    @Override
    public Map<String, GroupItem> getGroups() {
        return groups;
    }

    @JsonGetter("groups")
    public Collection<GroupItem> getGroupItems() {
        return groups.values();
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void addItem(Item item) {
        item.setLandscape(this);
        item.getProvidedBy().forEach(s -> s.setLandscape(this));
        items.add(item);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LandscapeImpl landscape = (LandscapeImpl) o;

        return StringUtils.trimAllWhitespace(identifier).equals(StringUtils.trimAllWhitespace(landscape.identifier));
    }

    @Override
    public int hashCode() {
        return Objects.hash(StringUtils.trimAllWhitespace(identifier));
    }

    public void setConfig(LandscapeConfig config) {
        this.config = config;
    }

    public void addGroup(Group g) {
        g.setLandscape(this.identifier);
        if (groups.containsKey(g.getIdentifier())) {
            Groups.merge((Group) groups.get(g.getIdentifier()), g);
        } else {
            groups.put(g.getIdentifier(), g);
        }
    }

    /**
     * Returns the group with the given name.
     *
     * @param group name
     * @return group or null if the group cannot be found as optional
     */
    public Optional<Group> getGroup(String group) {
        return Optional.ofNullable((Group) groups.get(group));
    }

    public void setProcessLog(ProcessLog processLog) {
        this.processLog = processLog;
    }

    @JsonIgnore
    public ProcessLog getLog() {
        return processLog;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public Map<String, String> getLabels() {
        return labels;
    }

    @Override
    public String getLabel(String key) {
        return labels.get(key);
    }

    @Override
    public void setLabel(String key, String value) {
        labels.put(key, value);
    }

    @Override
    public Set<StatusValue> getAdditionalStatusValues() {
        return StatusValue.fromMapping(indexedByPrefix(Label.status));
    }

    @Override
    public List<? extends Assessable> getChildren() {
        return getGroups().values().stream().map(groupItem -> (Assessable)groupItem).collect(Collectors.toList());
    }

    @Override
    @Schema(name = "_links")
    public Map<String, Link> getLinks() {
        return links;
    }

    @JsonGetter("lastUpdate")
    public LocalDateTime getLastUpdate() {
        return this.processLog == null ? null : this.processLog.getLastUpdate();
    }
}
