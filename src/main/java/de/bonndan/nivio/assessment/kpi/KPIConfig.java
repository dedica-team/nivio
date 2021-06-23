package de.bonndan.nivio.assessment.kpi;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.HashMap;
import java.util.Map;

/**
 * POJO representing the yaml configuration.
 */
@Schema(description = "The configuration of landscape specific key performance indicators that derive status information from landscape components. Usually the KPIs work on labels")
public class KPIConfig {

    @Schema(description = "Description of the purpose of the KPI")
    public String description;

    @Schema(description = "Key of the label to evaluate", example = "costs", required = true)
    public String label;

    @Schema(description = "Template for the displayed message, containing a placeholder for the assessed value '%s", example = "The current value is: %s")
    public String messageTemplate = null;

    /**
     * GREEN: 0;99.999999
     * YELLOW: 100;199.999999
     * RED: 200;499.999999
     * BROWN: 500;1000000
     */
    @Schema(description = "A map of number based ranges that determine the resulting status (GREEN|YELLOW|ORANGE|RED|BROWN). Use a semicolon to separate upper and lower bounds. Tries to evaluate label values as numbers.",
            example = "GREEN: 0;99.999999")
    public Map<String, String> ranges = new HashMap<>();

    @Schema(description = "A map of string based matchers that determine the resulting status (GREEN|YELLOW|ORANGE|RED|BROWN). Use a semicolon to separate matchers.",
            example = "RED: BAD;err.*")
    public Map<String, String> matches = new HashMap<>();

    @Schema(description = "A flag indicating that the KPI is active. Can be used to disable default kpis.")
    public boolean enabled = true;

}
