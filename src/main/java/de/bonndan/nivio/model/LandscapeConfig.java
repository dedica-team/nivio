package de.bonndan.nivio.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.bonndan.nivio.assessment.kpi.AbstractKPI;
import de.bonndan.nivio.assessment.kpi.KPIConfig;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Global configuration for a landscape.
 *
 *
 */
public class LandscapeConfig {

    @Schema(description = "Flag that enables instant creation items based relation targets that cannot be found in the sources.")
    private boolean greedy = true;

    @Schema(description = "Settings to tweak the positioning of groups in the map")
    private LayoutConfig groupLayoutConfig = new LayoutConfig();

    @Schema(description = "Settings to tweak the positioning of items in a group in the map")
    private LayoutConfig itemLayoutConfig = new LayoutConfig();

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

    public LayoutConfig getGroupLayoutConfig() {
        return groupLayoutConfig;
    }

    public void setGroupLayoutConfig(LayoutConfig groupLayoutConfig) {
        if (groupLayoutConfig != null) {
            this.groupLayoutConfig = groupLayoutConfig;
        }
    }

    public boolean isGreedy() {
        return greedy;
    }

    public void setGreedy(boolean greedy) {
        this.greedy = greedy;
    }

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

    public void setItemLayoutConfig(LayoutConfig itemLayoutConfig) {
        if (itemLayoutConfig != null) {
            this.itemLayoutConfig = itemLayoutConfig;
        }
    }

    public LayoutConfig getItemLayoutConfig() {
        return itemLayoutConfig;
    }


    @Schema(description = "Layout configuration. See https://jgraph.github.io/mxgraph/java/docs/com/mxgraph/layout/mxFastOrganicLayout.html")
    public static class LayoutConfig {

        @Schema(description = "The maximum number of iterations. More iterations theoretically lead to better results.")
        private Integer maxIterations;

        @Schema(description = "A factor to influence the attracting and repulsive forces in a layout.")
        private Float forceConstantFactor = 1f;

        @Schema(description = "A factor to influence maximum distance where forces are applied.")
        private Float maxDistanceLimitFactor = 1f;

        @Schema(description = "A factor to influence minimum distance where forces are applied.")
        private Float minDistanceLimitFactor = 1f;

        public Integer getMaxIterations() {
            return maxIterations;
        }

        public void setMaxIterations(Integer maxIterations) {
            this.maxIterations = maxIterations;
        }

        public Float getForceConstantFactor() {
            return forceConstantFactor;
        }

        public void setForceConstantFactor(Float forceConstantFactor) {
            this.forceConstantFactor = forceConstantFactor;
        }

        public Float getMinDistanceLimitFactor() {
            return minDistanceLimitFactor;
        }

        public void setMinDistanceLimitFactor(Float minDistanceLimitFactor) {
            this.minDistanceLimitFactor = minDistanceLimitFactor;
        }

        public Float getMaxDistanceLimitFactor() {
            return maxDistanceLimitFactor;
        }

        public void setMaxDistanceLimitFactor(Float maxDistanceLimitFactor) {
            this.maxDistanceLimitFactor = maxDistanceLimitFactor;
        }
    }


    /**
     * The configured KPIs.
     *
     * @return all active {@link AbstractKPI}s with their unique identifier
     */
    public Map<String, KPIConfig> getKPIs() {
        return kpis;
    }

    /**
     * Configuration options for corporate identity branding.
     */
    public static class Branding {

        @Schema(description = "A resolvable URL pointing to a CSS stylesheet. This stylesheet is included in the generated SVG map. Use is to style the appearance of the map.",
                example = "https://acme.com/css/acme.css")
        private String mapStylesheet;

        @Schema(description = "A resolvable URL pointing to an image. This image is included (embedded as data-url) in the generated SVG map.",
                example = "https://acme.com/images/logo.png")
        private String mapLogo;

        public String getMapStylesheet() {
            return mapStylesheet;
        }

        public void setMapStylesheet(String mapStylesheet) {
            this.mapStylesheet = mapStylesheet;
        }

        public String getMapLogo() {
            return mapLogo;
        }

        public void setMapLogo(String mapLogo) {
            this.mapLogo = mapLogo;
        }
    }
}
