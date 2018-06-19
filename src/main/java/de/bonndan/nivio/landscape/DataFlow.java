package de.bonndan.nivio.landscape;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Indication of an incoming or outgoing data flow.
 * <p>
 * Outgoing flows having a target which matches a service identifier will cause a relation to be created.
 */
@Entity
public class DataFlow implements Serializable {

    @Id
    @GeneratedValue
    private Long id;


    @ManyToOne
    @JoinColumn(name = "source_identifier", referencedColumnName = "identifier")
    private Service source;

    @ManyToOne
    @JoinColumn(name = "target_identifier", referencedColumnName = "identifier")
    private Service target;

    private String description;

    private String format;

    public DataFlow() {
    }

    public DataFlow(Service origin, Service target) {
        this.source = origin;
        this.target = target;
    }

    public long getId() {
        return id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataFlow dataFlow = (DataFlow) o;
        return Objects.equals(source.getIdentifier(), dataFlow.source.getIdentifier()) &&
                Objects.equals(target.getIdentifier(), dataFlow.target.getIdentifier());
    }

    @Override
    public int hashCode() {
        return Objects.hash(source.getIdentifier(), target.getIdentifier());
    }
}
