package de.bonndan.nivio.assessment.kpi;

import java.util.HashMap;
import java.util.Map;

/**
 * POJO representing the yaml configuration.
 */
class KPIConfig {

    /**
     * Description of the KPI
     */
    public String description;

    /**
     * which label to read, e.g. "costs"
     */
    public String label;

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

}
