package de.bonndan.nivio.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.bonndan.nivio.assessment.Assessable;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.assessment.kpi.KPI;
import de.bonndan.nivio.input.ProcessLog;
import de.bonndan.nivio.search.ItemIndex;
import de.bonndan.nivio.search.SearchIndex;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import javax.validation.constraints.Pattern;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static de.bonndan.nivio.model.Item.IDENTIFIER_VALIDATION;

/**
 * Think of a group of servers and apps, like a "project", "workspace" or stage.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Landscape implements Linked, Component, Labeled, Assessable {

    private final SearchIndex searchIndex;

    /**
     * Immutable unique identifier. Maybe use an URN.
     */
    @Pattern(regexp = IDENTIFIER_VALIDATION)
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
    private final ItemIndex<Item> items;

    private LandscapeConfig config;

    private final Map<String, Group> groups = new HashMap<>();

    private ProcessLog processLog;

    private final Map<String, String> labels = new HashMap<>();
    private final Map<String, Link> links = new HashMap<>();
    private String owner;

    /**
     * all KPIs for the landscape, configured and initialized
     */
    private Map<String, KPI> kpis = new HashMap<>();

    private String icon;

    public Landscape(@NonNull String identifier, @NonNull Group defaultGroup) {
        setIdentifier(identifier);
        this.addGroup(defaultGroup);
        this.searchIndex = new SearchIndex();
        this.items = new ItemIndex<>(searchIndex, Item.class);
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public FullyQualifiedIdentifier getFullyQualifiedIdentifier() {
        return FullyQualifiedIdentifier.build(identifier, null, null);
    }

    public void setIdentifier(String identifier) {

        if (StringUtils.isEmpty(identifier) || !identifier.matches(IDENTIFIER_VALIDATION)) {
            throw new IllegalArgumentException("Invalid landscape identifier given: '" + identifier + "', it must match " + IDENTIFIER_VALIDATION);
        }
        this.identifier = StringUtils.trimAllWhitespace(identifier);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public ItemIndex<Item> getItems() {
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

    public LandscapeConfig getConfig() {
        if (config == null) {
            config = new LandscapeConfig();
        }
        return config;
    }

    @JsonIgnore
    public Map<String, Group> getGroups() {
        return groups;
    }

    @JsonGetter("groups")
    public Collection<Group> getGroupItems() {
        return new ArrayList<>(groups.values());
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    @JsonIgnore
    @Override
    public String getAddress() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Landscape landscape = (Landscape) o;

        return StringUtils.trimAllWhitespace(identifier).equals(StringUtils.trimAllWhitespace(landscape.identifier));
    }

    @Override
    public int hashCode() {
        return Objects.hash(StringUtils.trimAllWhitespace(identifier));
    }

    public void setConfig(LandscapeConfig config) {
        this.config = config;
    }

    public void addGroup(@NonNull Group group) {
        if (group == null) {
            throw new IllegalArgumentException("Trying to add null group");
        }

        group.setLandscape(this.identifier);
        if (groups.containsKey(group.getIdentifier())) {
            Groups.merge(groups.get(group.getIdentifier()), group);
        } else {
            groups.put(group.getIdentifier(), group);
        }
    }

    /**
     * Returns the group with the given name.
     *
     * @param group name
     * @return group or null if the group cannot be found as optional
     */
    public Optional<Group> getGroup(String group) {
        return Optional.ofNullable(groups.get(group));
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

    @JsonIgnore
    @Override
    public Map<String, String> getLabels() {
        return labels;
    }

    /**
     * Returns the labels without the internal ones (having prefixes).
     *
     * @return filtered labels
     */
    @JsonProperty("labels")
    public Map<String, String> getJSONLabels() {

        return Labeled.groupedByPrefixes(
                Labeled.withoutPrefixes(labels, Label.status.name(), Tagged.LABEL_PREFIX_TAG),
                ","
        );
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
        return getGroups().values().stream().map(groupItem -> (Assessable) groupItem).collect(Collectors.toList());
    }

    @Schema(name = "_links")
    public Map<String, Link> getLinks() {
        return links;
    }

    @JsonGetter("lastUpdate")
    public LocalDateTime getLastUpdate() {
        return this.processLog == null ? null : this.processLog.getLastUpdate();
    }

    @Override
    public String getColor() {
        return null;
    }

    @Override
    public String getIcon() {
        return icon;
    }

    public void setIcon(String iconUrl) {
        icon = iconUrl;
    }

    /**
     * Returns all KPIs for the landscape.
     *
     * @return kpis, configured and initialized
     */
    @JsonIgnore
    public Map<String, KPI> getKpis() {
        return kpis;
    }

    public void setKpis(Map<String, KPI> kpis) {
        this.kpis = kpis;
    }

    @JsonIgnore
    public SearchIndex getSearchIndex() {
        return searchIndex;
    }
}
