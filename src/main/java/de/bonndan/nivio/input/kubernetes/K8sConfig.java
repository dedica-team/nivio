package de.bonndan.nivio.input.kubernetes;

import java.util.Map;

public class K8sConfig {
    private boolean active;
    private int minMatchingLabel;
    private Map<String, Integer> level;

    public K8sConfig() {
    }

    public K8sConfig(boolean active, int minMatchingLabel, Map<String, Integer> level) {
        this.active = active;
        this.minMatchingLabel = minMatchingLabel;
        this.level = level;
    }

    public boolean isActive() {
        return active;
    }

    public int getMinMatchingLabel() {
        return minMatchingLabel;
    }

    public Map<String, Integer> getLevel() {
        return level;
    }

    public void setMinMatchingLabel(int minMatchingLabel) {
        this.minMatchingLabel = minMatchingLabel;
    }

    public void setLevel(Map<String, Integer> level) {
        this.level = level;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
