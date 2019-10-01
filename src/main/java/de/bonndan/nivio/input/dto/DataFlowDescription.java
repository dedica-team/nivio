package de.bonndan.nivio.input.dto;

import de.bonndan.nivio.model.DataFlowItem;

public class DataFlowDescription implements DataFlowItem {

    private String description;
    private String format;
    private String source;
    private String target;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getTarget() {
        return target;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setTarget(String target) {
        this.target = target;
    }

}
