package de.bonndan.nivio.model;

public class LandscapeConfig {

    private boolean greedy = true;

    private JGraphXConfig jgraphx = new JGraphXConfig();

    public JGraphXConfig getJgraphx() {
        return jgraphx;
    }

    public void setJgraphx(JGraphXConfig jgraphx) {
        this.jgraphx = jgraphx;
    }

    public boolean isGreedy() {
        return greedy;
    }

    public void setGreedy(boolean greedy) {
        this.greedy = greedy;
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
}
