package de.bonndan.nivio.output.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.bonndan.nivio.assessment.kpi.KPI;
import de.bonndan.nivio.model.*;
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
        super(landscape);
        this.landscape = Objects.requireNonNull(landscape);
        hateoasLinks.putAll(landscape.getLinks());
    }

    @Nullable
    public String getContact() {
        return landscape.getContact();
    }

    public LandscapeConfig getConfig() {
        return landscape.getConfig();
    }

    public Set<UnitApiModel> getUnits() {
        return landscape.getReadAccess().all(Unit.class).stream()
                .map(UnitApiModel::new)
                .collect(Collectors.toSet());
    }

    public Set<GroupApiModel> getGroups() {
        return landscape.getGroupItems().stream()
                .map(group -> new GroupApiModel(group, Set.copyOf(group.getChildren())))
                .collect(Collectors.toSet());
    }

    public ZonedDateTime getLastUpdate() {
        return landscape.getLastUpdate();
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
