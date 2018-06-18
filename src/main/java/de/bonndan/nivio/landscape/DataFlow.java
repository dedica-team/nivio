package de.bonndan.nivio.landscape;

import javax.persistence.*;

/**
 * Indication of an incoming or outgoing data flow.
 *
 * Outgoing flows having a target which matches a service identifier will cause a relation to be created.
 */
@Entity
@IdClass(DataFlowId.class)
public class DataFlow {

    @Id
    @Column(insertable = false, updatable = false)
    private String source_identifier;

    @Id
    @Column(insertable = false, updatable = false)
    private String target_identifier;

    @ManyToOne
    @PrimaryKeyJoinColumn(name="source_identifier", referencedColumnName="identifier")
    Service source;

    @ManyToOne
    @PrimaryKeyJoinColumn(name="target_identifier", referencedColumnName="identifier")
    Service target;

    private String description;

    private String format;

    public DataFlow(Service origin, Service target) {
        this.source = origin;
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

    public Service getSource() {
        return source;
    }

    public void setSource(Service source) {
        this.source = source;
    }

    public Service getTarget() {
        return target;
    }

    public void setTarget(Service target) {
        this.target = target;
    }
}
