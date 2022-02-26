package de.bonndan.nivio.model;

import de.bonndan.nivio.assessment.kpi.AbstractKPI;
import de.bonndan.nivio.assessment.kpi.KPIConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Global configuration for a landscape.
 */
public class LandscapeConfig {

    @Schema(description = "Settings to tweak the positioning of groups in the map")
    private LayoutConfig layoutConfig = new LayoutConfig();

    @Schema(description = "Names or patterns of groups that should be excluded from the landscape. Used to improve automatic scanning results.",
            example = ".*infra.*")
    private final List<String> groupBlacklist = new ArrayList<>();

    @Schema(description = "Names or patterns of labels that should be ignored. Used to improve automatic scanning results.",
            example = ".*COMPOSITION.*")
    private final List<String> labelBlacklist = new ArrayList<>();

    @Schema(description = "Map branding (tweaks visuals)")
    private final Branding branding = new Branding();

    @Schema(description = "Key performance indicator configs. Each KPI must have a unique identifier.")
    private final Map<String, KPIConfig> kpis = new HashMap<>();

    /**
     * @return a list of group identifiers which are excluded from the landscape
     */
    public List<String> getGroupBlacklist() {
        return groupBlacklist;
    }

    /**
     * @return a list of matchers / regex for labels should not be examined for relations
     */
    public List<String> getLabelBlacklist() {
        return labelBlacklist;
    }

    public Branding getBranding() {
        return branding;
    }


    /**
     * The configured KPIs.
     *
     * @return all active {@link AbstractKPI}s with their unique identifier
     */
    public Map<String, KPIConfig> getKPIs() {
        return kpis;
    }

    public void setLayoutConfig(LayoutConfig layoutConfig) {
        this.layoutConfig = Objects.requireNonNull(layoutConfig);
    }

    @NonNull
    public LayoutConfig getLayoutConfig() {
        return layoutConfig;
    }

    /**
     * Merges the values of the update if present
     *
     * @param update object with values used to overwrite the current
     * @return a new config
     */
    public LandscapeConfig merge(LandscapeConfig update) {
        LandscapeConfig landscapeConfig = new LandscapeConfig();
        landscapeConfig.layoutConfig = layoutConfig;
        landscapeConfig.groupBlacklist.addAll(groupBlacklist);
        landscapeConfig.labelBlacklist.addAll(labelBlacklist);
        landscapeConfig.branding.setMapStylesheet(branding.mapStylesheet);
        landscapeConfig.kpis.putAll(kpis);

        landscapeConfig.kpis.putAll(update.kpis);

        landscapeConfig.layoutConfig.setItemLayoutInitialTemp(update.layoutConfig.getItemLayoutInitialTemp());
        landscapeConfig.layoutConfig.setGroupLayoutInitialTemp(update.layoutConfig.getGroupLayoutInitialTemp());
        landscapeConfig.layoutConfig.setItemMinDistanceLimit(update.layoutConfig.getItemMinDistanceLimit());
        landscapeConfig.layoutConfig.setItemMaxDistanceLimit(update.layoutConfig.getItemMaxDistanceLimit());
        landscapeConfig.layoutConfig.setGroupMinDistanceLimit(update.layoutConfig.getGroupMinDistanceLimit());
        landscapeConfig.layoutConfig.setGroupMaxDistanceLimit(update.layoutConfig.getGroupMaxDistanceLimit());

        if (!update.groupBlacklist.isEmpty()) {
            landscapeConfig.groupBlacklist.clear();
            landscapeConfig.groupBlacklist.addAll(update.groupBlacklist);
        }
        if (!update.labelBlacklist.isEmpty()) {
            landscapeConfig.labelBlacklist.clear();
            landscapeConfig.labelBlacklist.addAll(update.labelBlacklist);
        }
        if (StringUtils.hasLength(update.branding.mapStylesheet)) {
            landscapeConfig.branding.mapStylesheet = update.branding.mapStylesheet;
        }

        return landscapeConfig;
    }

    /**
     * Configuration options for corporate identity branding.
     */
    public static class Branding {

        @Schema(description = "A resolvable URL pointing to a CSS stylesheet. This stylesheet is included in the generated SVG map. Use is to style the appearance of the map.",
                example = "https://acme.com/css/acme.css")
        private String mapStylesheet;

        public String getMapStylesheet() {
            return mapStylesheet;
        }

        public void setMapStylesheet(String mapStylesheet) {
            this.mapStylesheet = mapStylesheet;
        }
    }
}
