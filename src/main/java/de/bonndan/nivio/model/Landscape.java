package de.bonndan.nivio.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.bonndan.nivio.assessment.Assessable;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.assessment.kpi.KPI;
import de.bonndan.nivio.input.ProcessLog;
import io.swagger.v3.oas.annotations.media.Schema;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static de.bonndan.nivio.model.Item.IDENTIFIER_VALIDATION;

/**
 * Think of a group of servers and apps, like a "project", "workspace" or stage.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Landscape implements Linked, Component, Labeled, Assessable {

    /**
     * Immutable unique identifier. Maybe use an URN.
     */
    @NonNull
    @Pattern(regexp = IDENTIFIER_VALIDATION)
    private final String identifier;

    /**
     * Human readable name.
     */
    @NonNull
    private final String name;

    /**
     * Maintainer email
     */
    @Nullable
    private final String contact;

    @Nullable
    private final String owner;

    private final String description;

    private final String source;

    @JsonIgnore
    private final ItemIndex items = new ItemIndex();

    private final LandscapeConfig config;

    private final Map<String, Group> groups;

    private final ProcessLog processLog;

    private final Map<String, String> labels = new HashMap<>();
    private final Map<String, Link> links = new HashMap<>();

    /**
     * all KPIs for the landscape, configured and initialized
     */
    private Map<String, KPI> kpis = new HashMap<>();

    public Landscape(@NonNull final String identifier,
                     @NonNull final Map<String, Group> groups,
                     @NonNull final String name,
                     @Nullable final String contact,
                     @Nullable final String owner,
                     @Nullable final String description,
                     @Nullable final String source,
                     @Nullable final LandscapeConfig config,
                     @Nullable final ProcessLog processLog
    ) {
        this.identifier = validateIdentifier(Objects.requireNonNull(identifier));
        this.groups = groups;
        this.name = Objects. requireNonNull(name);
        this.contact = contact;

        this.owner = owner;
        this.description = description;
        this.source = source;
        this.config = config != null ? config : new LandscapeConfig();
        this.processLog = processLog != null ? processLog : new ProcessLog(LoggerFactory.getLogger(Landscape.class));
    }

    @NonNull
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public FullyQualifiedIdentifier getFullyQualifiedIdentifier() {
        return FullyQualifiedIdentifier.build(identifier, null, null);
    }

    private String validateIdentifier(String identifier) {
        if (StringUtils.isEmpty(identifier) || !identifier.matches(IDENTIFIER_VALIDATION)) {
            throw new IllegalArgumentException("Invalid landscape identifier given: '" + identifier + "', it must match " + IDENTIFIER_VALIDATION);
        }
        return StringUtils.trimAllWhitespace(identifier);
    }

    @NonNull
    public String getName() {
        return name;
    }

    @JsonIgnore
    public ItemIndex getItems() {
        return items;
    }

    public void setItems(Set<Item> items) {
        this.items.setItems(items);
    }

    public String getSource() {
        return source;
    }

    @Override
    @Nullable
    public String getContact() {
        return contact;
    }

    public LandscapeConfig getConfig() {
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

    @JsonIgnore
    public ProcessLog getLog() {
        return processLog;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getOwner() {
        return owner;
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
        return getLabel("icon");
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
}
