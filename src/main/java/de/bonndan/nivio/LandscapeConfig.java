package de.bonndan.nivio;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.bonndan.nivio.assessment.kpi.AbstractKPI;
import de.bonndan.nivio.assessment.kpi.KPI;
import de.bonndan.nivio.assessment.kpi.KPIFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LandscapeConfig {

    private boolean greedy = true;

    private JGraphXConfig jgraphx = new JGraphXConfig();

    private final List<String> groupBlacklist = new ArrayList<>();
    private final List<String> labelBlacklist = new ArrayList<>();
    private final Branding branding = new Branding();

    /**
     * KPIs by their unique identifier.
     */
    @JsonDeserialize(using = KPIFactory.class)
    private final Map<String, KPI> kpis = new HashMap<>();

    public JGraphXConfig getJgraphx() {
        return jgraphx;
    }

    public void setJgraphx(JGraphXConfig jgraphx) {
        if (jgraphx != null) {
            this.jgraphx = jgraphx;
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

    /**
     * @link https://jgraph.github.io/mxgraph/java/docs/com/mxgraph/layout/mxOrganicLayout.html
     */
    public static class JGraphXConfig {
        private Double edgeLengthCostFactor;
        private Double nodeDistributionCostFactor;
        private Double borderLineCostFactor;
        private Integer triesPerCell;
        private Integer maxIterations;
        private Integer initialTemp;
        private Float forceConstantFactor = 1f;
        private Float minDistanceLimitFactor = 1f;

        public Double getEdgeLengthCostFactor() {
            return edgeLengthCostFactor;
        }

        public void setEdgeLengthCostFactor(Double edgeLengthCostFactor) {
            if (edgeLengthCostFactor != null)
                this.edgeLengthCostFactor = edgeLengthCostFactor;
        }

        public Double getNodeDistributionCostFactor() {
            return nodeDistributionCostFactor;
        }

        public void setNodeDistributionCostFactor(Double nodeDistributionCostFactor) {
            if (nodeDistributionCostFactor != null)
                this.nodeDistributionCostFactor = nodeDistributionCostFactor;
        }

        public Double getBorderLineCostFactor() {
            return borderLineCostFactor;
        }

        public void setBorderLineCostFactor(Double borderLineCostFactor) {
            if (borderLineCostFactor != null)
                this.borderLineCostFactor = borderLineCostFactor;
        }

        public Integer getTriesPerCell() {
            return triesPerCell;
        }

        public void setTriesPerCell(Integer triesPerCell) {
            if (triesPerCell != null)
                this.triesPerCell = triesPerCell;
        }

        public Integer getMaxIterations() {
            return maxIterations;
        }

        public void setMaxIterations(Integer maxIterations) {
            this.maxIterations = maxIterations;
        }

        public Integer getInitialTemp() {
            return initialTemp;
        }

        public void setInitialTemp(Integer initialTemp) {
            this.initialTemp = initialTemp;
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
    }

    /**
     * The active KPIs.
     *
     * @return all active {@link AbstractKPI}s with their unique identifier
     */
    public Map<String, KPI> getKPIs() {
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
