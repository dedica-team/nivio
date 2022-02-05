package de.bonndan.nivio.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.bonndan.nivio.assessment.Assessable;
import de.bonndan.nivio.assessment.kpi.KPI;
import de.bonndan.nivio.input.ProcessLog;
import de.bonndan.nivio.input.dto.Source;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Think of a group of servers and apps, like a "project", "workspace" or stage.
 *
 * This is the root node of the landscape graph.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Landscape extends GraphComponent implements Linked, Labeled, Assessable {

    //todo make configurable
    public static final String DEFAULT_COMPONENT = "default";

    @JsonIgnore
    private final Source source;

    private final LandscapeConfig config;

    private ProcessLog processLog;

    /**
     * all KPIs for the landscape, configured and initialized
     */
    private final Map<String, KPI> kpis;

    private final Index<GraphComponent> index;

    /**
     * see {@link LandscapeBuilder}
     */
    Landscape(@NonNull final String identifier,
              @NonNull final String name,
              @Nullable final String contact,
              @Nullable final String owner,
              @Nullable final String description,
              @Nullable final String type,
              @Nullable final Source source,
              @Nullable final LandscapeConfig config,
              @NonNull final Map<String, KPI> kpis,
              @NonNull final Index<GraphComponent> index
    ) {
        super(identifier, name, owner, contact, description, type, null);

        this.source = source;
        this.config = config != null ? config : new LandscapeConfig();
        this.kpis = kpis;
        this.index = index;
        attach(new IndexReadAccess<>(index));
        index.addOrReplace(this);
    }

    /**
     * @return a builder with all values including index
     */
    @JsonIgnore //for internal debugging
    @NonNull
    public LandscapeBuilder getConfiguredBuilder() {
         return LandscapeBuilder.aLandscape()
                 .withIdentifier(getIdentifier())
                 .withName(getName())
                 .withConfig(getConfig())
                 .withDescription(getDescription())
                 .withContact(getContact())
                 .withOwner(getOwner())
                 .withSource(getSource())
                 .withIndex(index)
                 .withLabels(getLabels())
                 .withLinks(getLinks())
                 .withKpis(getKpis());
    }

    @Override
    protected URI verifyParent(final URI uri) {
        return null;
    }

    @NonNull
    @Override
    public GraphComponent getParent() {
        throw new IllegalStateException("Landscape has no parent.");
    }

    @NonNull
    @Override
    public Set<? extends GraphComponent> getChildren() {
        return getChildren(component -> true, GraphComponent.class);
    }

    @JsonIgnore
    public IndexReadAccess<GraphComponent> getIndexReadAccess() {
        return new IndexReadAccess<>(index);
    }

    @JsonIgnore
    public GraphWriteAccess<GraphComponent> getIndexWriteAccess() {
        return new GraphWriteAccess<>(index, getIndexReadAccess());
    }

    @JsonIgnore
    @Nullable
    public Source getSource() {
        return source;
    }

    public LandscapeConfig getConfig() {
        return config;
    }

    @JsonIgnore
    public Map<URI, Group> getGroups() {
        return indexReadAccess.all(Group.class).stream()
                .collect(Collectors.toMap(GraphComponent::getFullyQualifiedIdentifier, group -> group));
    }

    @JsonGetter("groups")
    public Collection<Group> getGroupItems() {
        return getGroups().values();
    }

    /**
     * Returns the group with the given name.
     *
     * @param group name
     * @return group or null if the group cannot be found as optional
     */
    public Optional<Group> getGroup(String group) {
        return indexReadAccess.all(Group.class).stream()
                .filter(group1 -> group1.getIdentifier().equals(group))
                .findFirst();
    }

    @JsonIgnore
    public ProcessLog getLog() {
        return processLog;
    }

    public void setLog(@NonNull final ProcessLog log) {
        this.processLog = Objects.requireNonNull(log, "log is null");
    }


    /**
     * Returns the labels without the internal ones (having prefixes).
     *
     * @return filtered labels
     */
    @JsonProperty("labels")
    public Map<String, String> getJSONLabels() {

        return Labeled.groupedByPrefixes(
                Labeled.withoutKeys(getLabels(), Label.status.name(), Tagged.LABEL_PREFIX_TAG),
                ","
        );
    }

    @JsonGetter("lastUpdate")
    public ZonedDateTime getLastUpdate() {
        return this.processLog == null ? null : this.processLog.getLastUpdate();
    }

    /**
     * Returns all KPIs for the landscape.
     *
     * @return kpis, configured and initialized
     */
    public Map<String, KPI> getKpis() {
        return kpis;
    }

    @NonNull
    @Override
    public String getParentIdentifier() {
        return "";
    }
}
