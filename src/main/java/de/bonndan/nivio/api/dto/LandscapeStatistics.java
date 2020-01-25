package de.bonndan.nivio.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.bonndan.nivio.model.Status;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LandscapeStatistics {
    public Integer items;
    public Integer groups;
    public Status overallStatus;
    public String[] teams;
}
