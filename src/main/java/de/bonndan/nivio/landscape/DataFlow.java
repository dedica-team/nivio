package de.bonndan.nivio.landscape;

/**
 * Indication of an incoming or outgoing data flow.
 *
 * Outgoing flows having a target which matches a service identifier will cause a relation to be created.
 */
public class DataFlow {

    private String description;

    private String format;

    Service origin;

    Service target;

    public DataFlow(Service origin, Service target) {
        this.origin = origin;
        this.target = target;
    }

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
}
