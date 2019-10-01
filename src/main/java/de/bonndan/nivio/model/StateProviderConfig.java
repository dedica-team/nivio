package de.bonndan.nivio.model;

import java.io.Serializable;

public class StateProviderConfig implements Serializable {

    public static final String TYPE_PROMETHEUS_EXPORTER = "prometheus-exporter";

    private String type;
    private String target;

    public StateProviderConfig() {

    }

    public StateProviderConfig(String type, String target) {
        this.type = type;
        this.target = target;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return "[" + type +  " with target: " + target + "]";
    }
}
