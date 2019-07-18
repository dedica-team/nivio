package de.bonndan.nivio.landscape;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * Indication of an incoming or outgoing data flow.
 * <p>
 * Outgoing flows having a target which matches a service identifier will cause a relation to be created.
 */
@Entity
public class DataFlow implements DataFlowItem, Serializable {

    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    @ManyToOne
    @JsonBackReference
    private Service sourceEntity;

    private String target;

    private String description;

    private String format;

    public DataFlow() {
    }

    public DataFlow(Service origin, FullyQualifiedIdentifier fullyQualifiedIdentifier) {
        this.sourceEntity = origin;
        this.target = fullyQualifiedIdentifier.toString();
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

    public Service getSourceEntity() {
        return sourceEntity;
    }

    public void setSourceEntity(Service sourceEntity) {
        this.sourceEntity = sourceEntity;
    }

    public String getTarget() {
        return target;
    }

    @Override
    public String getSource() {
        return sourceEntity.getIdentifier();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataFlow dataFlow = (DataFlow) o;
        return Objects.equals(sourceEntity.getIdentifier(), dataFlow.sourceEntity.getIdentifier()) &&
                Objects.equals(target, dataFlow.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceEntity.getIdentifier(), target);
    }

}
