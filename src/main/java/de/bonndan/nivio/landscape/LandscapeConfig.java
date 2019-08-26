package de.bonndan.nivio.landscape;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LandscapeConfig {

    private JGraphXConfig jgraphx = new JGraphXConfig();
    private Map<String, GroupConfig> groups = new HashMap<>();

    public Optional<GroupConfig> getGroupConfig(String group) {
        return Optional.ofNullable(groups.get(group));
    }

    public Map<String, GroupConfig> getGroups() {
        return groups;
    }

    public void setGroups(Map<String, GroupConfig> groups) {
        this.groups = groups;
    }

    public JGraphXConfig getJgraphx() {
        return jgraphx;
    }

    public void setJgraphx(JGraphXConfig jgraphx) {
        this.jgraphx = jgraphx;
    }

    public static class GroupConfig {
        private String color;

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }
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
    }
}
