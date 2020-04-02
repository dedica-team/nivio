package de.bonndan.nivio.input.dto;

import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.assessment.StatusValue;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class StatusDescription implements StatusValue {

    @NotEmpty
    private String label;

    @NotNull
    private Status status;

    private String message;

    public StatusDescription() {
    }

    public StatusDescription(String label, Status status) {
        this.label = label;
        this.status = status;
        this.message = "";
    }

    public StatusDescription(String label, Status status, String message) {
        this.label = label;
        this.status = status;
        this.message = message;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
