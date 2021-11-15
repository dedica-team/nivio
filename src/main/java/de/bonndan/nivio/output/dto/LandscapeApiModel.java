package de.bonndan.nivio.output.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.bonndan.nivio.assessment.kpi.KPI;
import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.model.LandscapeConfig;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LandscapeApiModel extends ComponentApiModel {

    @NonNull
    private final Landscape landscape;

    public LandscapeApiModel(@NonNull final Landscape landscape) {
        this.landscape = Objects.requireNonNull(landscape);
        hateoasLinks.putAll(landscape.getLinks());
    }

    @NonNull
    public String getIdentifier() {
        return landscape.getIdentifier();
    }

    @NonNull
    public FullyQualifiedIdentifier getFullyQualifiedIdentifier() {
        return landscape.getFullyQualifiedIdentifier();
    }

    @NonNull
    public String getName() {
        return landscape.getName();
    }

    @Nullable
    public String getContact() {
        return landscape.getContact();
    }

    public LandscapeConfig getConfig() {
        return landscape.getConfig();
    }

    public Set<GroupApiModel> getGroups() {
        return landscape.getGroupItems().stream()
                .map(group -> new GroupApiModel(group, landscape.getItems().retrieve(group.getItems())))
                .collect(Collectors.toSet());
    }

    public String getDescription() {
        return landscape.getDescription();
    }

    public String getOwner() {
        return landscape.getOwner();
    }

    @Override
    public Map<String, String> getLabels() {
        return getPublicLabels(landscape.getLabels());
    }

    public ZonedDateTime getLastUpdate() {
        return landscape.getLastUpdate();
    }

    public String getIcon() {
        return landscape.getLabel(Label._icondata);
    }

    /**
     * Returns all KPIs for the landscape.
     *
     * @return kpis, configured and initialized
     */
    public Map<String, KPI> getKpis() {
        return landscape.getKpis();
    }
}
