package de.bonndan.nivio.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import javax.validation.constraints.NotEmpty;

public class ItemStatus implements StatusItem {

    @JsonBackReference
    private Item item;

    @NotEmpty
    private String label;

    private String message;

    private Status status;

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
