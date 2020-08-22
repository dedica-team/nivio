package de.bonndan.nivio;

import de.bonndan.nivio.assessment.kpi.AbstractKPI;
import de.bonndan.nivio.assessment.kpi.KPIConfig;

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

    private boolean greedy = true;

    private LayoutConfig groupLayoutConfig = new LayoutConfig();
    private LayoutConfig itemLayoutConfig = new LayoutConfig();

    private final List<String> groupBlacklist = new ArrayList<>();
    private final List<String> labelBlacklist = new ArrayList<>();
    private final Branding branding = new Branding();

    /**
     * KPI configs by their unique identifier.
     */
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

    /**
     * @link https://jgraph.github.io/mxgraph/java/docs/com/mxgraph/layout/mxFastOrganicLayout.html
     */
    public static class LayoutConfig {
        private Integer maxIterations;
        private Float forceConstantFactor = 1f;
        private Float maxDistanceLimitFactor = 1f;
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
     * The active KPIs.
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

        private String mapStylesheet;
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
