package de.bonndan.nivio.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.io.Serializable;
import java.util.Objects;

/**
 * Indication of an incoming or outgoing relation like data flow or dependency (provider).
 *
 * <p>
 * Outgoing flows having a target which matches a service identifier will cause a relation to be created.
 */
//TODO make it typed
public class Relation implements RelationItem, Serializable {

    @JsonBackReference
    private Item sourceEntity;

    private String target;

    private String description;

    private String format;

    private RelationType type;

    public Relation() {
    }

    public Relation(Item origin, FullyQualifiedIdentifier fullyQualifiedIdentifier) {
        this.sourceEntity = origin;
        this.target = fullyQualifiedIdentifier.toString();
    }

    @Override
    public RelationType getType() {
        return type;
    }

    public void setType(RelationType type) {
        this.type = type;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Item getSourceEntity() {
        return sourceEntity;
    }

    public void setSourceEntity(Item sourceEntity) {
        this.sourceEntity = sourceEntity;
    }

    @Override
    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public void getTargetEntity() {

    }
    @Override
    public String getSource() {
        return sourceEntity.getIdentifier();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Relation relation = (Relation) o;
        return Objects.equals(sourceEntity.getIdentifier(), relation.sourceEntity.getIdentifier()) &&
                Objects.equals(target, relation.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceEntity.getIdentifier(), target);
    }

}
