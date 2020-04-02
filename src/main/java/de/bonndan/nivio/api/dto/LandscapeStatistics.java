package de.bonndan.nivio.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import de.bonndan.nivio.assessment.Status;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LandscapeStatistics {
    public Integer items;
    public Integer groups;
    public Status overallStatus;
    public String[] teams;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    public Date lastUpdate;
}
