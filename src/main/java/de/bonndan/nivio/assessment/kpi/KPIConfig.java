package de.bonndan.nivio.assessment.kpi;

import java.util.HashMap;
import java.util.Map;

/**
 * POJO representing the yaml configuration.
 */
public class KPIConfig {

    /**
     * Description of the KPI
     */
    public String description;

    /**
     * which label to read, e.g. "costs"
     */
    public String label;

    /**
     * What label content to display in the message
     */
    public String messageLabel = null;

    /**
     * GREEN: 0;99.999999
     * YELLOW: 100;199.999999
     * RED: 200;499.999999
     * BROWN: 500;1000000
     */
    public Map<String, String> ranges = new HashMap<>();

    /**
     * GREEN: "OK;good;nice"
     * RED: "BAD;err.*"
     */
    public Map<String, String> matches = new HashMap<>();

    /**
     * whether the kpi is active
     */
    public boolean enabled = true;

}
